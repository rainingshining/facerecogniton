package com.hc.facerecogniton.config;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.hc.facerecogniton.factory.FaceEngineFactory;
import com.hc.facerecogniton.utils.CompoundUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FaceEngineConfig {

    private Logger logger = LoggerFactory.getLogger(FaceEngineConfig.class);

    @Value("${config.arcface-sdk.sdk-lib-path}")
    public String sdkLibPath;
    @Value("${config.arcface-sdk.app-id}")
    public String appId;

    @Value("${config.arcface-sdk.sdk-key}")
    public String sdkKey;

    @Value("${config.arcface-sdk.thread-pool-size}")
    public Integer threadPoolSize;

    @Value("${config.face.pass.rate}")
    private Integer passRate;

    private ExecutorService executorService;

    private GenericObjectPool<FaceEngine> faceEngineObjectPool;

    @PostConstruct
    public void init() {
        logger.info("Start to initialize Face Recognition Engine ...");
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(threadPoolSize);
        poolConfig.setMaxTotal(threadPoolSize);
        poolConfig.setMinIdle(threadPoolSize);
        poolConfig.setLifo(false);

        //引擎配置
        logger.info("Prepare settings for engine ...");
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        logger.info("Load functions ...");
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);

        logger.info("Setting and Functions are all set ...");

        String absoluteLibPath = CompoundUtils.getAbsolutePath(sdkLibPath);

        //底层库算法对象池
        faceEngineObjectPool = new GenericObjectPool(new FaceEngineFactory(absoluteLibPath, appId, sdkKey, engineConfiguration), poolConfig);

        logger.info("Face engine initialized");
    }

    public FaceEngine borrow() throws Exception {
        return faceEngineObjectPool.borrowObject();
    }

    public void returnEngine(FaceEngine faceEngine){
        faceEngineObjectPool.returnObject(faceEngine);
    }

    public ExecutorService getThreadPool(){
        return executorService;
    }

    public Integer getPassRate() {
        return passRate;
    }

    public GenericObjectPool<FaceEngine> getFaceEngineObjectPool() {
        return faceEngineObjectPool;
    }
}
