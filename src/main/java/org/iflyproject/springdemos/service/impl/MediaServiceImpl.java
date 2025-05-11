// MediaServiceImpl.java
package org.iflyproject.springdemos.service.impl;

import org.iflyproject.springdemos.model.dto.AudioConfig;
import org.iflyproject.springdemos.model.dto.VideoConfig;
import org.iflyproject.springdemos.service.MediaService;
import org.springframework.stereotype.Service;

@Service
public class MediaServiceImpl implements MediaService {

    @Override
    public boolean testAudio(AudioConfig config) {
        // 实现音频测试逻辑
        System.out.println("Testing audio device: " + config.getDeviceName());
        System.out.println("Sample rate: " + config.getSampleRate() + "Hz");
        System.out.println("Channels: " + config.getChannels());
        return true;
    }

    @Override
    public boolean testVideo(VideoConfig config) {
        // 实现视频测试逻辑
        System.out.println("Testing video device: " + config.getDeviceName());
        System.out.println("Resolution: " + config.getResolution());
        System.out.println("Frame rate: " + config.getFrameRate() + "fps");
        return true;
    }
}