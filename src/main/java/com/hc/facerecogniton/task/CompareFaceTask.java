package com.hc.facerecogniton.task;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceSimilar;
import com.google.common.collect.Lists;
import com.hc.facerecogniton.config.FaceEngineConfig;
import com.hc.facerecogniton.dto.FaceUserInfo;
import com.hc.facerecogniton.utils.CompoundUtils;
import com.hc.facerecogniton.utils.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

public class CompareFaceTask implements Callable<List<FaceUserInfo>> {

    private Logger logger = LoggerFactory.getLogger(CompareFaceTask.class);

    private FaceEngineConfig config = ContextUtil.getBean(FaceEngineConfig.class);

    private List<FaceUserInfo> faceUserInfoList;
    private FaceFeature targetFaceFeature;

    public CompareFaceTask(List<FaceUserInfo> faceUserInfoList, FaceFeature targetFaceFeature) {
        this.faceUserInfoList = faceUserInfoList;
        this.targetFaceFeature = targetFaceFeature;
    }

    @Override
    public List<FaceUserInfo> call() throws Exception {
        FaceEngine faceEngine = null;
        List<FaceUserInfo> resultFaceInfoList = Lists.newLinkedList();//识别到的人脸列表
        try {
            faceEngine = config.borrow();
            for (FaceUserInfo faceUserInfo : faceUserInfoList) {
                FaceFeature sourceFaceFeature = new FaceFeature();
                sourceFaceFeature.setFeatureData(faceUserInfo.getFaceFeature());
                FaceSimilar faceSimilar = new FaceSimilar();
                faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
                Integer similarValue = CompoundUtils.plusHundred(faceSimilar.getScore());//获取相似值
                if (similarValue > config.getPassRate()) {//相似值大于配置预期，加入到识别到人脸的列表

                    FaceUserInfo info = new FaceUserInfo();
                    info.setName(faceUserInfo.getName());
                    info.setFaceId(faceUserInfo.getFaceId());
                    info.setSimilarValue(similarValue);
                    resultFaceInfoList.add(info);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (faceEngine != null) {
                config.returnEngine(faceEngine);
            }
        }

        return resultFaceInfoList;
    }
}
