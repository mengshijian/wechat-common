package com.cootf.wechat.example;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.cootf.wechat.support.TokenManager;

public class TokenManagerListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//WEB容器 初始化时调用
		TokenManager.init("appid", "secret","code");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		//WEB容器  关闭时调用
		TokenManager.destroyed();
	}
}
