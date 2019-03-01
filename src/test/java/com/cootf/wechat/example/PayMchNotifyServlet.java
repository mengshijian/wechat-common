package com.cootf.wechat.example;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cootf.wechat.bean.paymch.MchBaseResult;
import com.cootf.wechat.bean.paymch.MchPayNotify;
import com.cootf.wechat.support.ExpireKey;
import com.cootf.wechat.support.expirekey.DefaultExpireKey;
import com.cootf.wechat.util.SignatureUtil;
import com.cootf.wechat.util.StreamUtils;
import com.cootf.wechat.util.XMLConverUtil;

/**
 * 支付回调通知
 * 
 * @author mengsj
 *
 */
public class PayMchNotifyServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String key; // mch key

	// 重复通知过滤
	private static ExpireKey expireKey = new DefaultExpireKey();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取请求数据
		String xmlData = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
		// 将XML转为MAP,确保所有字段都参与签名验证
		Map<String, String> mapData = XMLConverUtil.convertToMap(xmlData);
		// 转换数据对象
		MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class, xmlData);
		// 已处理 去重
		if (expireKey.exists("WX_PAY_NOTIFY" + payNotify.getTransaction_id())) {
			MchBaseResult baseResult = new MchBaseResult();
			baseResult.setReturn_code("SUCCESS");
			baseResult.setReturn_msg("OK");
			response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
			return;
		}
		// 签名验证
		if (SignatureUtil.validateSign(mapData, key)) {
			// @since 2.8.5
			payNotify.buildDynamicField(mapData);

			expireKey.add("WX_PAY_NOTIFY" + payNotify.getTransaction_id(), 60);
			MchBaseResult baseResult = new MchBaseResult();
			baseResult.setReturn_code("SUCCESS");
			baseResult.setReturn_msg("OK");
			response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
		} else {
			MchBaseResult baseResult = new MchBaseResult();
			baseResult.setReturn_code("FAIL");
			baseResult.setReturn_msg("ERROR");
			response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
		}
	}

}
