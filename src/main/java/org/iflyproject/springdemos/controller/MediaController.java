// MediaController.java
//已完成测试
package org.iflyproject.springdemos.controller;

import org.iflyproject.springdemos.model.dto.AudioConfig;
import org.iflyproject.springdemos.model.dto.VideoConfig;
import org.iflyproject.springdemos.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping("/audio/test")
    public ResponseEntity<Boolean> testAudio(@RequestBody AudioConfig config) {
        boolean result = mediaService.testAudio(config);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/video/test")
    public ResponseEntity<Boolean> testVideo(@RequestBody VideoConfig config) {
        boolean result = mediaService.testVideo(config);
        return ResponseEntity.ok(result);
    }
}