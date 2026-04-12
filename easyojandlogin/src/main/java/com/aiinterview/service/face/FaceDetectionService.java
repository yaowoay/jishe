package com.aiinterview.service.face;


import com.aiinterview.service.impl.face.WebFaceDetect;

public interface FaceDetectionService {

    public WebFaceDetect.ResponseData detectFace(String imagePath) throws Exception;
}
