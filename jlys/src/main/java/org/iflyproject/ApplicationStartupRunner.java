package org.iflyproject;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查并创建上传目录
        initUploadDirectory();

        // 其他初始化操作
        System.out.println("面试系统初始化完成");
    }

    private void initUploadDirectory() {
        // 实现上传目录检查逻辑
    }
}