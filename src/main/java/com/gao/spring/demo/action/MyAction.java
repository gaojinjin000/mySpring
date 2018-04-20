package com.gao.spring.demo.action;

import com.gao.spring.annotation.GAutowired;
import com.gao.spring.annotation.GController;
import com.gao.spring.annotation.GRequestMapping;
import com.gao.spring.demo.service.IDemoService;

/**
 * Created by 20170707365 on 2018/4/20.
 */

@GController
public class MyAction {

    @GAutowired
    IDemoService demoService;

    @GRequestMapping("/index.html")
    public void query(){
        System.out.println("成功");
    }
}
