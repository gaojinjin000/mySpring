package com.gao.spring.framework.context;

import com.gao.spring.framework.annotation.GAutowired;
import com.gao.spring.framework.annotation.GController;
import com.gao.spring.framework.annotation.GService;
import com.gao.spring.framework.beans.BeanDefinition;
import com.gao.spring.framework.beans.BeanPostProcessor;
import com.gao.spring.framework.beans.BeanWrapper;
import com.gao.spring.framework.context.support.BeanDefinitionReader;
import com.gao.spring.framework.core.GBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 20170707365 on 2018/4/23.
 */
public class GApplicationContext implements GBeanFactory {

    private String[] configLocations;
    //beanDefinitionMap用来保存配置信息
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    //用来保证注册式单例的容器
    private Map<String, Object> beanCacheMap = new HashMap<>();

    //用来存储所有的被代理过的对象
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();


    BeanDefinitionReader reader;

    public GApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    private void refresh() {

        try {


            //定位
            reader = new BeanDefinitionReader(configLocations);

            //载入，向List中载入所有的classname
            List<String> registyBeanClasses = reader.loadBeanDefinitions();

            //注册，将BeanDefinition注册到BeanDefinitionMap中
            doRegistry(registyBeanClasses);

            //依赖注入（lazy-init = false），要是执行依赖注入
            //在这里自动调用getBean方法
            doAutowired();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void doAutowired() {

        //先将beanDefinitionMap中所有beanDefinition包含的beanClassName进行实例化（反射）
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {

            String beanName = beanDefinitionEntry.getKey();

            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }

        //向实例化对象中引用的对象进行依赖注入
        for (Map.Entry<String, BeanWrapper> beanWrapperEntry : beanWrapperMap.entrySet()) {
            populateBean(beanWrapperEntry.getKey(), beanWrapperEntry.getValue().getWrapperInstance());
        }

    }

    private void populateBean(String beanName, Object wrapperInstance) {

        Class clazz = wrapperInstance.getClass();

        if (!(clazz.isAnnotationPresent(GController.class) ||
                clazz.isAnnotationPresent(GService.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!(field.isAnnotationPresent(GAutowired.class))) {
                continue;
            }
            GAutowired gAutowired = field.getAnnotation(GAutowired.class);

            String autowiredBeanName = gAutowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = lowerFirstCast(field.getType().getName());
            }
            field.setAccessible(true);
            try {
                field.set(wrapperInstance, beanWrapperMap.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    private void doRegistry(List<String> registyBeanClasses) throws ClassNotFoundException {

        //beanName有三种情况:
        //1、默认是类名首字母小写
        //2、自定义名字
        //3、接口注入
        for (String registyBeanClass : registyBeanClasses) {

            Class clazz = Class.forName(registyBeanClass);

            //如果是接口，则不进行注册
            if (clazz.isInterface()) {
                continue;
            }


            BeanDefinition beanDefinition = reader.registerBean(registyBeanClass);
            if (beanDefinition != null) {
                beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            }


            //TODO如果是接口的实现类


            //IOC容器初始化完毕

        }

    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) {

        BeanDefinition definition = beanDefinitionMap.get(beanName);

        ////生成通知事件
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();


        Object instance = instializationBean(definition);
        if (instance == null) {
            return null;
        }

        //在实例初始化以前调用一次
        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

        BeanWrapper beanWrapper = new BeanWrapper(instance);
        beanWrapper.setPostProcessor(beanPostProcessor);
        beanWrapperMap.put(beanName, beanWrapper);

        //在实例初始化以后调用一次
        beanPostProcessor.postProcessAfterInitialization(instance, beanName);


        return beanWrapperMap.get(beanName);


    }

    //传一个BeanDefinition，就返回一个实例Bean
    private Object instializationBean(BeanDefinition definition) {

        String beanClassName = definition.getBeanClassName();
        String factoryBeanName = definition.getFactoryBeanName();
        try {

            if (beanCacheMap.containsKey(factoryBeanName)) {
                return beanCacheMap.get(factoryBeanName);
            } else {
                Class clazz = Class.forName(beanClassName);
                Object instance = clazz.newInstance();
                beanCacheMap.put(factoryBeanName, instance);
                return instance;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String lowerFirstCast(String className) {

        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);

    }
}
