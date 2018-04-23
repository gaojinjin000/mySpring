package com.gao.spring.demo.service.impl;


import com.gao.spring.framework.annotation.GService;
import com.gao.spring.demo.service.IDemoService;

@GService
public class DemoService implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}
