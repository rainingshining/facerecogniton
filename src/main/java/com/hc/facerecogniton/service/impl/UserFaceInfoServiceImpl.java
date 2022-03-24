package com.hc.facerecogniton.service.impl;

import com.hc.facerecogniton.domain.UserFaceInfo;
import com.hc.facerecogniton.mapper.MybatisUserFaceInfoMapper;
import com.hc.facerecogniton.service.UserFaceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFaceInfoServiceImpl implements UserFaceInfoService {

    @Autowired
    private MybatisUserFaceInfoMapper userFaceInfoMapper;

    @Override
    public void insertSelective(UserFaceInfo userFaceInfo) {
        userFaceInfoMapper.insertUserFaceInfo(userFaceInfo);
    }

}
