package com.aiinterview.flink.recommend;
import org.apache.flink.configuration.Configuration;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

import java.sql.*;

public class BehaviorRealTimeProfileJob {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setInteger("rest.port", 8081);

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.enableCheckpointing(60000);

        // 1. 配置Kafka Source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("master:9092")
                .setTopics("user_action_log")
                .setGroupId("behavior-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // 消费日志并丰富岗位信息
        DataStream<JSONObject> behaviorStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafka-behavior-source")
                .map(json -> JSONObject.parseObject(json))
                .filter(data -> data.containsKey("userId") && data.containsKey("actionType")
                        && data.containsKey("jobId") && data.containsKey("score"))
                .filter(data -> {
                    String actionType = data.getString("actionType");
                    return "VIEW".equals(actionType) || "FAV".equals(actionType) || "APPLY".equals(actionType);
                })
                // 修复：正确的数据库查询逻辑
                .map(data -> {
                    String jobId = data.getString("jobId");
                    String url = "jdbc:mysql://localhost:3306/ai_interview_v2?useSSL=false&serverTimezone=UTC";
                    String username = "root";
                    String password = "jyl123456";
                    String sql = "select job_type as type, location as city, min_salary as salaryMin, max_salary as salaryMax from jobs where job_id = ?";

                    // 修复1：正确调用querySingle方法（传入jobId参数）
                    JSONObject jobInfo = JdbcConnectionUtils.querySingle(url, username, password, sql, jobId, (ResultSet rs) -> {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("type", rs.getString("type"));
                            obj.put("city", rs.getString("city"));
                            obj.put("salaryMin", rs.getInt("salaryMin"));
                            obj.put("salaryMax", rs.getInt("salaryMax"));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return obj;
                    });

                    if (jobInfo == null || jobInfo.isEmpty()) {
                        return null;
                    }
                    data.put("jobInfo", jobInfo);
                    return data;
                })
                .filter(data -> data != null); // 过滤掉查询失败的记录

        // 2. 按用户ID分组，滑动窗口聚合
        DataStream<JSONObject> realTimeProfileStream = behaviorStream
                .keyBy(data -> data.getString("userId"))
                .window(SlidingProcessingTimeWindows.of(Time.days(7), Time.hours(1)))
                .aggregate(new RealTimeAggregate());

        // 输出到控制台，便于观察最终写入 Redis 的实时画像结果
        realTimeProfileStream.map(value -> value.toJSONString()).print("real-time-profile");

        // 3. 配置Redis Sink
        FlinkJedisPoolConfig redisConfig = new FlinkJedisPoolConfig.Builder()
                .setHost("localhost")
                .setPort(6379)
                .build(); // 如果没有密码，不要调用setPassword

        realTimeProfileStream.addSink(new RedisSink<>(redisConfig, new RealTimeRedisMapper()));

        env.execute("User-Behavior-RealTime-Profile");
    }

    // 修复2：修正JdbcConnectionUtils的方法签名
    public static class JdbcConnectionUtils {
        public static JSONObject querySingle(String url, String username, String password,
                                             String sql, String jobId,
                                             java.util.function.Function<ResultSet, JSONObject> mapper) {
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, jobId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapper.apply(rs);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 加权聚合函数
    public static class RealTimeAggregate implements AggregateFunction<JSONObject, JSONObject, JSONObject> {
        @Override
        public JSONObject createAccumulator() {
            return new JSONObject();
        }

        @Override
        public JSONObject add(JSONObject value, JSONObject accumulator) {
            int weight = value.getIntValue("score");
            JSONObject jobInfo = value.getJSONObject("jobInfo");

            if (jobInfo == null) return accumulator;

            // 岗位类型统计
            String jobType = jobInfo.getString("type");
            if (jobType != null && !jobType.isEmpty()) {
                accumulator.put(jobType, accumulator.getIntValue(jobType) + weight);
            }

            // 城市统计
            String city = jobInfo.getString("city");
            if (city != null && !city.isEmpty()) {
                accumulator.put("city_" + city, accumulator.getIntValue("city_" + city) + weight);
            }

            // 薪资统计
            if (jobInfo.containsKey("salaryMin") && jobInfo.containsKey("salaryMax")) {
                int salaryMin = jobInfo.getIntValue("salaryMin");
                int salaryMax = jobInfo.getIntValue("salaryMax");
                accumulator.put("salary_min_total", accumulator.getIntValue("salary_min_total") + salaryMin * weight);
                accumulator.put("salary_max_total", accumulator.getIntValue("salary_max_total") + salaryMax * weight);
            }

            accumulator.put("count", accumulator.getIntValue("count") + weight);
            accumulator.put("user_id", value.getString("userId"));
            return accumulator;
        }

        @Override
        public JSONObject getResult(JSONObject accumulator) {
            JSONObject result = new JSONObject();
            String userId = accumulator.getString("user_id");
            int count = accumulator.getIntValue("count");

            if (count == 0 || userId == null) return result;

            // 提取Top3实时偏好岗位
            String realJobTypes = accumulator.entrySet().stream()
                    .filter(entry -> !entry.getKey().startsWith("city_")
                            && !entry.getKey().startsWith("salary")
                            && !entry.getKey().equals("user_id")
                            && !entry.getKey().equals("count"))
                    .sorted((a, b) -> Integer.compare((Integer) b.getValue(), (Integer) a.getValue()))
                    .limit(3)
                    .map(entry -> entry.getKey())
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            // 提取Top2实时偏好城市
            String realCities = accumulator.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("city_"))
                    .sorted((a, b) -> Integer.compare((Integer) b.getValue(), (Integer) a.getValue()))
                    .limit(2)
                    .map(entry -> entry.getKey().replace("city_", ""))
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            // 计算实时偏好薪资
            int avgSalaryMin = accumulator.getIntValue("salary_min_total") / count;
            int avgSalaryMax = accumulator.getIntValue("salary_max_total") / count;

            result.put("user_id", userId);
            result.put("real_time_job_types", realJobTypes);
            result.put("real_time_cities", realCities);
            result.put("real_time_salary_min", avgSalaryMin);
            result.put("real_time_salary_max", avgSalaryMax);
            return result;
        }

        @Override
        public JSONObject merge(JSONObject a, JSONObject b) {
            // 修复merge逻辑
            JSONObject merged = new JSONObject();
            merged.putAll(a);
            b.forEach((key, value) -> {
                if (value instanceof Integer) {
                    merged.put(key, a.getIntValue(key) + (Integer) value);
                }
            });
            return merged;
        }
    }

    // 修复3：修正Redis Mapper实现
    public static class RealTimeRedisMapper implements RedisMapper<JSONObject> {
        @Override
        public RedisCommandDescription getCommandDescription() {
            // 使用HASH结构，hash key固定，field为user_id
            return new RedisCommandDescription(RedisCommand.HSET, "user:profile:real");
        }

        @Override
        public String getKeyFromData(JSONObject data) {
            String userId = data.getString("user_id");
            return userId == null ? "" : userId;
        }

        @Override
        public String getValueFromData(JSONObject data) {
            // 存储整个JSON字符串作为value
            return data.toJSONString();
        }
    }
}