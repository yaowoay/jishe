package com.aiinterview.controller.behavior;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户行为记录 Controller
 * 用于前端主动上报用户行为
 */
@Slf4j
@RestController
@RequestMapping("/behavior")
@CrossOrigin(origins = "*")
public class UserBehaviorController {

    /**
     * 记录用户行为（前端主动上报）
     * @param behaviorData 行为数据
     * @return 响应结果
     */
    @PostMapping("/log")
    public BaseResponse<String> logUserBehavior(@RequestBody Map<String, Object> behaviorData) {
        try {
            log.info("收到前端行为上报: {}", behaviorData);
            // 这里可以添加额外的业务逻辑，如存储到数据库
            return ResultUtils.success("行为记录成功");
        } catch (Exception e) {
            log.error("记录用户行为失败", e);
            return ResultUtils.error(500, "记录失败: " + e.getMessage());
        }
    }
}
