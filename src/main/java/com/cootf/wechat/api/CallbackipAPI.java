package com.cootf.wechat.api;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.cootf.wechat.bean.callbackip.Callbackip;
import com.cootf.wechat.client.LocalHttpClient;

/**
 * 获取微信服务器IP地址
 * @author mengsj
 *
 */
public class CallbackipAPI extends BaseAPI{

	/**
	 * 获取微信服务器IP地址
	 * @param access_token access_token
	 * @return Callbackip
	 */
	public static Callbackip getcallbackip(String access_token){
		HttpUriRequest httpUriRequest = RequestBuilder.get()
				.setUri(BASE_URI + "/cgi-bin/getcallbackip")
				.addParameter(PARAM_ACCESS_TOKEN,API.accessToken(access_token))
				.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,Callbackip.class);
	}
}
