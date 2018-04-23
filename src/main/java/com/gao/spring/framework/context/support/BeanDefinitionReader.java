package com.gao.spring.framework.context.support;

import com.gao.spring.framework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by 20170707365 on 2018/4/23.
 */

//用对配置文件进行查找，读取、解析
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registyBeanClasses = new ArrayList();

    private final String SCANPACK = "scanPackage";




    public BeanDefinitionReader(String ... location) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location[0].replace("classpath:", ""));

        try {
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doSanner(config.getProperty(SCANPACK));


    }


    private void doSanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doSanner(packageName + "." + file.getName());
            } else {
                registyBeanClasses.add(packageName + "." + file.getName().replace(".class", ""));
            }

        }

    }

    public List<String> loadBeanDefinitions() {
//        doSanner(config.getProperty(SCANPACK));
        return registyBeanClasses;
    }


    //每注册一个className，就返回一个BeanDefinition，我自己包装
    //只是为了对配置信息进行一个包装
    public BeanDefinition registerBean(String className) {
        if (registyBeanClasses.contains(className)) {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCast(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }


    private String lowerFirstCast(String className) {

        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);

    }



}
