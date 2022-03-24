package com.hc.facerecogniton.service;

import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.hc.facerecogniton.dto.FaceUserInfo;
import com.hc.facerecogniton.dto.ProcessInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FaceEngineService {

    List<FaceInfo> detectFaces(ImageInfo imageInfo);

    List<ProcessInfo> process(ImageInfo imageInfo);

    byte[] extractFaceFeature(ImageInfo imageInfo) throws InterruptedException;

    List<FaceUserInfo> compareFaceFeature(byte[] faceFeature, Integer groupId) throws InterruptedException, ExecutionException;
}
