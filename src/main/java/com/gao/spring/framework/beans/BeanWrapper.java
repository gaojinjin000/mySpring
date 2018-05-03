package com.gao.spring.framework.beans;

import com.gao.spring.framework.aop.GAopConfig;
import com.gao.spring.framework.aop.GAopProxy;

/**
 * Created by 20170707365 on 2018/4/23.
 */
public class BeanWrapper {

    private GAopProxy gAopProxy = new GAopProxy();
    //还会用到  观察者  模式
    //1、支持事件响应，会有一个监听
    private BeanPostProcessor postProcessor;

    private Object wrapperInstance;
    //原始的通过反射new出来，要把包装起来，存下来
    private Object originalInstance;

    public BeanWrapper(Object originalInstance) {
        this.originalInstance = originalInstance;
        this.wrapperInstance = gAopProxy.getProxy(originalInstance);
    }

    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public void setOriginalInstance(Object originalInstance) {
        this.originalInstance = originalInstance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public void setGAopConfig(GAopConfig gAopConfig) {
        gAopProxy.setgAopConfig(gAopConfig);
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }
}
