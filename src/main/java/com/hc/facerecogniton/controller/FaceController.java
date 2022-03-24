package com.hc.facerecogniton.controller;


import com.alibaba.fastjson.JSONObject;
import com.hc.facerecogniton.base.Result;
import com.hc.facerecogniton.base.Results;
import com.hc.facerecogniton.dto.FaceSearchResDto;
import com.hc.facerecogniton.enums.ErrorCodeEnum;
import com.hc.facerecogniton.service.CombineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaceController {

    private Logger logger = LoggerFactory.getLogger(FaceController.class);

    @Autowired
    private CombineService combineService;

    @GetMapping("index")
    public String index(){
        return "index";
    }

    @PostMapping("/faceAdd")
    @ResponseBody
    public Result<Object> faceAddByPic(@RequestBody JSONObject json){
        boolean res = combineService.register(json);
        if (res) {
            return Results.newSuccessResult("注册成功");
        }

        return Results.newFailedResult("注册失败，请重新注册");
    }

    @PostMapping("/faceSearch")
    @ResponseBody
    public Result<FaceSearchResDto> faceSearch(@RequestBody JSONObject jsonObject){
        FaceSearchResDto dto = null;
        try {
            dto = combineService.searchFace(jsonObject);
        }catch (Exception e){
            return Results.newResult(null, ErrorCodeEnum.FACE_DOES_NOT_MATCH.getDescription(), false, 0);
        }

        String msg = dto.getPass() == 0 ? "已有面部匹配" : "未找到可匹配的面部";
        boolean isSuccess = dto.getPass() == 0 ? true : false;

        return Results.newResult(dto, msg, isSuccess, 0);
    }
}
