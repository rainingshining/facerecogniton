package com.hc.facerecogniton.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//@Component
public class ContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class t){
        return (T)context.getBean(t);
    }

    public static ApplicationContext getContext(){
        return context;
    }

    public static Object getBean(String name){
        return context.getBean(name);
    }

}
