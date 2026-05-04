package com.aiinterview.flink.recommend.contoller;



import com.aiinterview.flink.recommend.entity.Position;
import com.aiinterview.flink.recommend.service.PositionRecommendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendController {
    @Resource
    private PositionRecommendService recommendService;

    /**
     * 用户职位实时推荐接口
     * @param userId 用户ID
     * @return 推荐职位列表（来自jobs表真实数据）
     */
    @GetMapping("/position/{userId}")
    public List<Position> recommendPosition(@PathVariable String userId) {
        return recommendService.recommendPosition(userId);
    }
}

