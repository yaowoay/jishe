package com.aiinterview.job;

import com.aiinterview.service.EmploymentCockpitScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日凌晨全量扫描驾驶舱分级预警（规则 + 模型概率 + 综合风险分）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmploymentCockpitScheduledJob {

    private final EmploymentCockpitScanService employmentCockpitScanService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void nightlyScan() {
        log.info("开始定时任务：就业驾驶舱预警扫描");
        try {
            employmentCockpitScanService.scanAllStudents();
        } catch (Exception e) {
            log.error("就业驾驶舱定时扫描失败", e);
        }
    }
}
