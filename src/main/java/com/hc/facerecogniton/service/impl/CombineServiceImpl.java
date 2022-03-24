package com.hc.facerecogniton.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.hc.facerecogniton.constant.Constants;
import com.hc.facerecogniton.domain.UserFaceInfo;
import com.hc.facerecogniton.dto.FaceSearchResDto;
import com.hc.facerecogniton.dto.FaceUserInfo;
import com.hc.facerecogniton.dto.ProcessInfo;
import com.hc.facerecogniton.service.CombineService;
import com.hc.facerecogniton.service.FaceEngineService;
import com.hc.facerecogniton.service.UserFaceInfoService;
import com.hc.facerecogniton.utils.CompoundUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class CombineServiceImpl implements CombineService {

    private Logger logger = LoggerFactory.getLogger(CombineServiceImpl.class);

    @Autowired
    private FaceEngineService faceEngineService;
    @Autowired
    private UserFaceInfoService userFaceInfoService;

    @Override
    public FaceSearchResDto searchFace(JSONObject jsonObject) throws Exception {
        String file = jsonObject.getString("file");

        int groupId = 101;

        if (!file.startsWith("data")){
            file = Constants.BASE64_PIC_HEADER + file;
        }

        FaceSearchResDto faceSearchResDto = new FaceSearchResDto();

        byte[] decode = Base64.decode(CompoundUtils.base64Process(file));
        BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(decode));
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo(bufImage);

        //人脸特征获取
        byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
        if (bytes == null) {
            return null;
        }
        //人脸比对，获取比对结果
        List<FaceUserInfo> userFaceInfoList = faceEngineService.compareFaceFeature(bytes, groupId);

        if (CollectionUtil.isNotEmpty(userFaceInfoList)) {
            FaceUserInfo faceUserInfo = userFaceInfoList.get(0);
            BeanUtil.copyProperties(faceUserInfo, faceSearchResDto);
            List<ProcessInfo> processInfoList = faceEngineService.process(imageInfo);
            if (CollectionUtil.isNotEmpty(processInfoList)) {
                //人脸检测
                List<FaceInfo> faceInfoList = faceEngineService.detectFaces(imageInfo);
                int left = faceInfoList.get(0).getRect().getLeft();
                int top = faceInfoList.get(0).getRect().getTop();
                int width = faceInfoList.get(0).getRect().getRight() - left;
                int height = faceInfoList.get(0).getRect().getBottom() - top;

                Graphics2D graphics2D = bufImage.createGraphics();
                graphics2D.setColor(Color.RED);//红色
                BasicStroke stroke = new BasicStroke(5f);
                graphics2D.setStroke(stroke);
                graphics2D.drawRect(left, top, width, height);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufImage, "jpg", outputStream);
                byte[] bytes1 = outputStream.toByteArray();
                faceSearchResDto.setImage("data:image/jpeg;base64," + Base64Utils.encodeToString(bytes1));
                faceSearchResDto.setAge(processInfoList.get(0).getAge());
                faceSearchResDto.setGender(processInfoList.get(0).getGender().equals(1) ? "女" : "男");

            }

            faceSearchResDto.setPass(0);
            return faceSearchResDto;
        }

        faceSearchResDto.setSimilarValue(0);
        faceSearchResDto.setPass(1);
        return faceSearchResDto;
    }

    @Override
    public boolean register(JSONObject json) {
        String file = json.getString("file");
        Integer groupId = json.getInteger("groupId");
        String name = json.getString("name");

        try {
            if (file == null) {
                logger.error("file is null");
                return false;
            }
            if (groupId == null) {
                logger.error("groupId is null");
                return false;
            }
            if (name == null) {
                logger.error("name is null");
                return false;
            }

            byte[] decode = Base64.decode(CompoundUtils.base64Process(file));
            ImageInfo imageInfo = ImageFactory.getRGBData(decode);

            //人脸特征获取
            byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
            if (bytes == null) {
                return false;
            }

            UserFaceInfo userFaceInfo = new UserFaceInfo();
            userFaceInfo.setName(name);
            userFaceInfo.setGroupId(groupId);
            userFaceInfo.setFaceFeature(bytes);
            userFaceInfo.setFaceId(RandomUtil.randomString(10));

            //人脸特征插入到数据库
            userFaceInfoService.insertSelective(userFaceInfo);

            logger.info("faceAdd:" + name);
            return true;
        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }
}
