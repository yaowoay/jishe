package com.coldwind.easyoj.service;


import com.coldwind.easyoj.model.dto.AudioConfig;
import com.coldwind.easyoj.model.dto.VideoConfig;

public interface MediaService {
    boolean testAudio(AudioConfig config);
    boolean testVideo(VideoConfig config);
}