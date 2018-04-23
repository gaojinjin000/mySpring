package com.gao.spring.framework.webmvc.servlet;

import com.gao.spring.framework.annotation.GAutowired;
import com.gao.spring.framework.annotation.GController;
import com.gao.spring.framework.annotation.GService;
import com.gao.spring.framework.context.GApplicationContext;

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

    private final String LOCATION = "contextConfigLocation";

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


        GApplicationContext gApplicationContext = new GApplicationContext(config.getInitParameter(LOCATION));


    }



}
