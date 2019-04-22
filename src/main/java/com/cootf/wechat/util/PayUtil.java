package com.cootf.wechat.util;

import com.cootf.wechat.api.PayMchAPI;
import com.cootf.wechat.bean.paymch.MchBaseResult;
import com.cootf.wechat.bean.paymch.MchPayApp;
import com.cootf.wechat.bean.paymch.MchPayNativeReply;
import com.cootf.wechat.bean.paymch.MchPayNotify;
import com.cootf.wechat.bean.paymch.PapayEntrustweb;
import com.cootf.wechat.bean.paymch.PapayH5entrustwebResult;
import com.cootf.wechat.bean.paymch.RefundNotifyReqInfo;
import com.cootf.wechat.bean.paymch.SecapiPayRefundNotify;
import com.cootf.wechat.bean.paymch.WxaEntrustwebData;
import com.cootf.wechat.support.ExpireKey;
import com.cootf.wechat.support.WeChatNotifyProcessor;
import com.cootf.wechat.support.expirekey.DefaultExpireKey;
import com.qq.weixin.mp.aes.PKCS7Encoder;
import com.qq.weixin.mp.wxpay.WXPayConstants;
import java.io.IOException;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PayUtil {
	
	private static Logger logger = LoggerFactory.getLogger(PayUtil.class);

	// 重复通知过滤
	private static ExpireKey expireKey = new DefaultExpireKey();

	/**
	 * (MCH)生成支付JS请求对象
	 * 
	 * @param prepay_id
	 *            预支付订单号
	 * @param appId
	 *            appId
	 * @param key
	 *            商户支付密钥
	 * @return json
	 */
	public static String generateMchPayJsRequestJson(String prepay_id, String appId, String key,String signType) {
		String package_ = "prepay_id=" + prepay_id;
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("appId", appId);
		map.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
		map.put("package", package_);
		map.put("signType", signType);
		map.put("timeStamp", System.currentTimeMillis() / 1000 + "");
		String paySign = SignatureUtil.generateSign(map, key);
		map.put("paySign", paySign);
		return JsonUtil.toJSONString(map);
	}

	/**
	 * 微信支付结果通知回调
	 * @param xmlData
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String processPayResult(String xmlData,String key,
			WeChatNotifyProcessor notifyProcessor) {

		// 将XML转为MAP,确保所有字段都参与签名验证
		Map<String, String> mapData = XMLConverUtil.convertToMap(xmlData);
		// 转换数据对象
		MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class, xmlData);
		// 已处理 去重
		MchBaseResult baseResult = new MchBaseResult();
		if (expireKey.exists("WX_PAY_NOTIFY" + payNotify.getTransaction_id())) {
			baseResult.setReturn_code(WXPayConstants.SUCCESS);
			baseResult.setReturn_msg("OK");
			return XMLConverUtil.convertToXML(baseResult);
		}
		// 签名验证
		if (SignatureUtil.validateSign(mapData, key) && notifyProcessor.payResultProcess(payNotify)) {
			// @since 2.8.5
			payNotify.buildDynamicField(mapData);

			expireKey.add("WX_PAY_NOTIFY" + payNotify.getTransaction_id(), 60);

			baseResult.setReturn_code(WXPayConstants.SUCCESS);
			baseResult.setReturn_msg("OK");
		} else {
			baseResult.setReturn_code(WXPayConstants.FAIL);
			baseResult.setReturn_msg("ERROR");
		}
		return XMLConverUtil.convertToXML(baseResult);
	}

	/**
	 * 退款结果通知回调
	 * @param xmlData
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String processRefundResult(String xmlData, String key,
			WeChatNotifyProcessor notifyProcessor) {
		// 转换数据对象
		SecapiPayRefundNotify refundNotify = XMLConverUtil.convertToObject(SecapiPayRefundNotify.class, xmlData);

		// 退款通知成功
		MchBaseResult baseResult = new MchBaseResult();
		if (refundNotify != null && WXPayConstants.SUCCESS.equals(refundNotify.getReturn_code())) {
			// 解密数据 req_info
			RefundNotifyReqInfo refundNotifyReqInfo = decryptRefundNotifyReqInfo(refundNotify.getReq_info(),
					key);
			if (refundNotifyReqInfo == null) {
				baseResult.setReturn_code(WXPayConstants.FAIL);
				baseResult.setReturn_msg("ERROR");
				return XMLConverUtil.convertToXML(baseResult);
			}
			// 业务处理标记检查
			if (!expireKey.exists("WX_REFUND_NOTIFY" + refundNotifyReqInfo.getRefund_id())
					&& notifyProcessor.refundResultProcess(refundNotifyReqInfo)) {
				// 添加业务处理标记
				expireKey.add("WX_REFUND_NOTIFY" + refundNotifyReqInfo.getRefund_id(), 60);

				baseResult.setReturn_code(WXPayConstants.SUCCESS);
				baseResult.setReturn_msg("OK");
			}
		} else {
			baseResult.setReturn_code(WXPayConstants.FAIL);
			baseResult.setReturn_msg("ERROR");
		}
		return XMLConverUtil.convertToXML(baseResult);
	}

	/**
	 * (MCH)生成Native支付请求URL
	 * 
	 * @param appid
	 *            appid
	 * @param mch_id
	 *            mch_id
	 * @param productid
	 *            productid
	 * @param key
	 *            key
	 * @return url
	 */
	public static String generateMchPayNativeRequestURL(String appid, String mch_id, String productid, String key) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("appid", appid);
		map.put("mch_id", mch_id);
		map.put("time_stamp", System.currentTimeMillis() / 1000 + "");
		map.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
		map.put("product_id", productid);
		String sign = SignatureUtil.generateSign(map, key);
		map.put("sign", sign);
		return "weixin://wxpay/bizpayurl?" + MapUtil.mapJoin(map, false, false);
	}

	/**
	 * (MCH)生成 native 支付回复XML
	 * 
	 * @param mchPayNativeReply
	 *            mchPayNativeReply
	 * @param key
	 *            key
	 * @return xml
	 */
	public static String generateMchPayNativeReplyXML(MchPayNativeReply mchPayNativeReply, String key) {
		Map<String, String> map = MapUtil.objectToMap(mchPayNativeReply);
		String sign = SignatureUtil.generateSign(map, key);
		mchPayNativeReply.setSign(sign);
		return XMLConverUtil.convertToXML(mchPayNativeReply);
	}

	/**
	 * (MCH)生成支付APP请求数据
	 * 
	 * @param prepay_id
	 *            预支付订单号
	 * @param appId
	 *            appId
	 * @param partnerid
	 *            商户平台号
	 * @param key
	 *            商户支付密钥
	 * @return app data
	 */
	public static MchPayApp generateMchAppData(String prepay_id, String appId, String partnerid, String key) {
		Map<String, String> wx_map = new LinkedHashMap<String, String>();
		wx_map.put("appid", appId);
		wx_map.put("partnerid", partnerid);
		wx_map.put("prepayid", prepay_id);
		wx_map.put("package", "Sign=WXPay");
		wx_map.put("noncestr", UUID.randomUUID().toString().replace("-", ""));
		wx_map.put("timestamp", System.currentTimeMillis() / 1000 + "");
		String sign = SignatureUtil.generateSign(wx_map, key);
		MchPayApp mchPayApp = new MchPayApp();
		mchPayApp.setAppid(appId);
		mchPayApp.setPartnerid(partnerid);
		mchPayApp.setPrepayid(prepay_id);
		mchPayApp.setPackage_(wx_map.get("package"));
		mchPayApp.setNoncestr(wx_map.get("noncestr"));
		mchPayApp.setTimestamp(wx_map.get("timestamp"));
		mchPayApp.setSign(sign);
		return mchPayApp;
	}

	/**
	 * 生成委托代扣-公众号、APP 纯签约
	 * 
	 * @param papayEntrustweb
	 *            papayEntrustweb
	 * @param key
	 *            key
	 * @return url
	 */
	public static String generatePapayEntrustwebURL(PapayEntrustweb papayEntrustweb, String key) {
		Map<String, String> map = MapUtil.objectToMap(papayEntrustweb);
		String sign = SignatureUtil.generateSign(map, key);
		map.put("sign", sign);
		String params = MapUtil.mapJoin(map, false, true);
		return "https://api.mch.weixin.qq.com/papay/entrustweb?" + params;
	}
	
	/**
	 * 生成委托代扣-H5 纯签约
	 * @since 2.8.24
	 * @param papayEntrustweb
	 *            papayEntrustweb
	 * @param key
	 *            key
	 * @return url
	 */
	public static String generatePapayH5EntrustwebURL(PapayEntrustweb papayEntrustweb, String key) {
		PapayH5entrustwebResult result = PayMchAPI.papayH5entrustweb(papayEntrustweb, key);
		if (result != null && "SUCCESS".equals(result.getResult_code())) {
			return result.getRedirect_url();
		}
		return null;
	}
	
	/**
	 * 生成委托代扣-小程序 纯签约
	 * @since 2.8.24
	 * @param papayEntrustweb
	 *            papayEntrustweb
	 * @param key
	 *            key
	 * @return url
	 */
	public static WxaEntrustwebData generatePapayWxaEntrustweb(PapayEntrustweb papayEntrustweb, String key) {
		Map<String, String> map = MapUtil.objectToMap(papayEntrustweb);
		String sign = SignatureUtil.generateSign(map, "HMAC-SHA256", key);
		papayEntrustweb.setSign(sign);
		WxaEntrustwebData wxaEntrustwebData = new WxaEntrustwebData();
		wxaEntrustwebData.setAppId(papayEntrustweb.getAppid());
		wxaEntrustwebData.setExtraData(papayEntrustweb);
		wxaEntrustwebData.setPath("pages/index/index");
		return wxaEntrustwebData;
	}
	
	/**
	 * 解密退款结果通知数据 <br>
	 * 
	 * <i>
	 * 解密失败解决方案：在官方网站下载JCE无限制权限策略文件（请到官网下载对应的版本) ,下载后解压。<br>
	 * 可以看到local_policy.jar和US_export_policy.jar以及readme.txt，<br>
	 * 将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件<br>
	 * 
	 * JCE7 http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html<br>
	 * JCE8 http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
	 * </i>
	 * 
	 * @since 2.8.19
	 * 
	 * @param req_info
	 *            加密数据
	 * @param key
	 *            支付key
	 * @return RefundNotifyReqInfo 解密失败返回 Null
	 */
	public static RefundNotifyReqInfo decryptRefundNotifyReqInfo(String req_info, String key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			Key sKeySpec = new SecretKeySpec(DigestUtils.md5Hex(key).getBytes(), "AES");
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
			byte[] resultByte = cipher.doFinal(Base64.decodeBase64(req_info));
			String data = new String(PKCS7Encoder.decode(resultByte));
			return XMLConverUtil.convertToObject(RefundNotifyReqInfo.class, data);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
}