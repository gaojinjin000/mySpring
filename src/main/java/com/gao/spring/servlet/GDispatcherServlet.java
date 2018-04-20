package com.gao.spring.servlet;

import com.gao.spring.annotation.GAutowired;
import com.gao.spring.annotation.GController;
import com.gao.spring.annotation.GService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 20170707365 on 2018/4/20.
 */
public class GDispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> beanMaps = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }


    @Override
    public void init(ServletConfig config) throws ServletException {

        try {
            //定位,找到application.properties,并将其载入Properties中
            doLoadConfig(config.getInitParameter("contextConfigLocation"));

            //载入,将扫描的包中的所有类放入classNames中
            doSanner(contextConfig.getProperty("scanPackage"));

            //注册
            doRegister(classNames);

            //依赖注入属性
            doAutowired(beanMaps);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    private void doAutowired(Map<String, Object> beanMaps) throws IllegalAccessException {
        if (beanMaps.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : beanMaps.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!(field.isAnnotationPresent(GAutowired.class))) {
                    continue;
                }
                GAutowired gAutowired = field.getAnnotation(GAutowired.class);
                String beanName = gAutowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = lowerFirstCast(field.getType().getName());
                }
                field.setAccessible(true);
                field.set(entry.getValue(), beanMaps.get(beanName));


            }
        }

    }


    private void doRegister(List<String> classNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (classNames.isEmpty()) {
            return;
        }

        for (String className : classNames) {
            Class clazz = Class.forName(className);
            String beanName = null;
            if (clazz.isAnnotationPresent(GController.class)) {
                GController gController = (GController) clazz.getAnnotation(GController.class);
                if (!("".equals(gController.value().trim()))) {
                    beanName = gController.value();
                } else {
                    beanName = lowerFirstCast(clazz.getSimpleName());
                }
                try {
                    beanMaps.put(beanName, clazz.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (clazz.isAnnotationPresent(GService.class)) {
                GService gService = (GService) clazz.getAnnotation(GService.class);
                if (!("".equals(gService.value().trim()))) {
                    beanName = gService.value();
                } else {
                    beanName = lowerFirstCast(clazz.getSimpleName());
                }

                Object instance = clazz.newInstance();

                beanMaps.put(beanName, instance);

                Class[] interfaces = clazz.getInterfaces();
                for (Class anInterface : interfaces) {
                    beanMaps.put(lowerFirstCast(anInterface.getSimpleName()), instance);
                }


            } else {
                continue;
            }

        }


    }

    private String lowerFirstCast(String className) {

        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);

    }

    private void doSanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doSanner(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", ""));
            }

        }

    }

    private void doLoadConfig(String location) {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));

        try {
            contextConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
