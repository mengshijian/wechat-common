package com.cootf.wechat.api;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.cootf.wechat.bean.token.Token;
import com.cootf.wechat.client.LocalHttpClient;

/**
 * ACCESS_TOKEN API
 * @author mengsj
 *
 */
public class TokenAPI extends BaseAPI{

	/**
	 * 获取access_token
	 * @param appid appid
	 * @param secret secret
	 * @param code 授权码
	 * @return Token
	 */
	public static Token token(String appid,String secret,String code){
		HttpUriRequest httpUriRequest = RequestBuilder.get()
				.setUri(BASE_URI + "/sns/oauth2/access_token")
				.addParameter("appid", appid)
				.addParameter("secret", secret)
				.addParameter("code", code)
				.addParameter("grant_type","client_credential")
				.build();

		return LocalHttpClient.executeJsonResult(httpUriRequest,Token.class);
	}
}
