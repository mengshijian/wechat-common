package com.cootf.wechat.example;

import com.alibaba.fastjson.JSON;

import com.cootf.wechat.api.PayMchAPI;
import com.cootf.wechat.api.UserAPI;
import com.cootf.wechat.bean.paymch.Refundquery;
import com.cootf.wechat.client.LocalHttpClient;
import com.cootf.wechat.client.ResultErrorHandler;

public class ResultErrorHandlerExample extends ResultErrorHandler{

	@Override
	protected void handle(String uriId, String uri, String requestEntity, Object result) {
		System.out.println("uriId:" + uriId);
		System.out.println("uri:" + uri);
		System.out.println("requestEntity:" + requestEntity);
		System.out.println("result:" + result);
		System.out.println("resultJSON:" + JSON.toJSONString(result));
	}

	
	public static void main(String[] args) {
		//设置数据错误处理
		LocalHttpClient.setResultErrorHandler(new ResultErrorHandlerExample());
		
		UserAPI.tagsCreate("access_token","test");
		PayMchAPI.payRefundquery(new Refundquery(), "key");
	}
}
