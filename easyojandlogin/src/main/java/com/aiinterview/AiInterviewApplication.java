package com.aiinterview;

import cn.hutool.core.io.resource.ClassPathResource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * AI面试系统启动类
 */
@SpringBootApplication
@EnableScheduling
@MapperScan({"com.aiinterview.**.mapper", "com.aiinterview.repository"})
public class AiInterviewApplication {

    private Process pythonProcess;

    public static void main(String[] args) {
        SpringApplication.run(AiInterviewApplication.class, args);

    }

    @PostConstruct
    public void startPythonService() {
        try {
            // 从classpath获取python目录
            ClassPathResource resource = new ClassPathResource("python/employment_warning_server.py");
            File scriptFile = resource.getFile();
            String scriptPath = scriptFile.getAbsolutePath();
            String pythonDir = scriptFile.getParent();

            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
            pb.directory(new File(pythonDir));
            pb.redirectErrorStream(true);

            pythonProcess = pb.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(pythonProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Python] " + line);
                    }
                } catch (Exception e) {}
            }).start();

            Thread.sleep(3000);
            System.out.println("就业预警Python服务已启动");

        } catch (Exception e) {
            System.out.println("就业预警Python服务启动失败: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stopPythonService() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            pythonProcess.destroy();
            System.out.println("就业预警Python服务已停止");
        }
    }
}
