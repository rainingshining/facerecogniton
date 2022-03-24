package com.hc.facerecogniton.service;

import com.alibaba.fastjson.JSONObject;
import com.hc.facerecogniton.dto.FaceSearchResDto;

public interface CombineService {

    FaceSearchResDto searchFace(JSONObject jsonObject) throws Exception;

    boolean register(JSONObject jsonObject);

}
