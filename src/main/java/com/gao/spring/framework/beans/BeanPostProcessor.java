package com.gao.spring.framework.beans;

/**
 * Created by 20170707365 on 2018/4/23.
 */
//用做事件监听的
public class BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }
    public Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }
}
