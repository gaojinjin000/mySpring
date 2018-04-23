package com.gao.spring.framework.core;

/**
 * Created by 20170707365 on 2018/4/23.
 */
public interface GBeanFactory {

    //    根据bean的名字，获取在IOC容器中得到bean实例
    Object getBean(String name);
}
