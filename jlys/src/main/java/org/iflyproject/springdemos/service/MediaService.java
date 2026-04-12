package org.iflyproject.springdemos.service;


import org.iflyproject.springdemos.model.dto.AudioConfig;
import org.iflyproject.springdemos.model.dto.VideoConfig;

public interface MediaService {
    boolean testAudio(AudioConfig config);
    boolean testVideo(VideoConfig config);
}