package com.hc.facerecogniton.controller;

import com.hc.facerecogniton.domain.UserFaceInfo;
import com.hc.facerecogniton.mapper.MybatisUserFaceInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class UserListController {

    @Autowired
    private MybatisUserFaceInfoMapper userFaceInfoMapper;

    @GetMapping("/userInfo")
    public List<UserFaceInfo> getUserInfo()
    {
        List<UserFaceInfo> list = new ArrayList<UserFaceInfo>();
        list = userFaceInfoMapper.findUserFaceInfoList();
        return list;
    }
}
