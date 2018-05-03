package com.gao.spring.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 20170707365(gaojinjin) on 2018/5/3.
 */
public class GAopProxy implements InvocationHandler {
    private Object target;
    private GAopConfig gAopConfig;

    public Object getProxy(Object instance) {
        this.target = instance;
        Class clazz = instance.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    public void setgAopConfig(GAopConfig gAopConfig) {
        this.gAopConfig = gAopConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        GAopConfig.GAspect gAspect = gAopConfig.get(method);
        //before
        gAspect.getPoints()[0].invoke(gAspect.getAspect());
        //目标方法
        Object obj = method.invoke(target);

        //after
        gAspect.getPoints()[1].invoke(gAspect.getAspect());

        //
        return obj ;
    }
}
