package com.cootf.wechat.api;

import com.cootf.wechat.bean.paymch.Authcodetoopenid;
import com.cootf.wechat.bean.paymch.AuthcodetoopenidResult;
import com.cootf.wechat.bean.paymch.Closeorder;
import com.cootf.wechat.bean.paymch.DownloadbillResult;
import com.cootf.wechat.bean.paymch.MchBaseResult;
import com.cootf.wechat.bean.paymch.MchDownloadbill;
import com.cootf.wechat.bean.paymch.MchOrderInfoResult;
import com.cootf.wechat.bean.paymch.MchOrderquery;
import com.cootf.wechat.bean.paymch.MchReverse;
import com.cootf.wechat.bean.paymch.MchReverseResult;
import com.cootf.wechat.bean.paymch.MchShorturl;
import com.cootf.wechat.bean.paymch.MchShorturlResult;
import com.cootf.wechat.bean.paymch.Micropay;
import com.cootf.wechat.bean.paymch.MicropayResult;
import com.cootf.wechat.bean.paymch.Refundquery;
import com.cootf.wechat.bean.paymch.RefundqueryResult;
import com.cootf.wechat.bean.paymch.Report;
import com.cootf.wechat.bean.paymch.SecapiPayRefund;
import com.cootf.wechat.bean.paymch.SecapiPayRefundResult;
import com.cootf.wechat.bean.paymch.Unifiedorder;
import com.cootf.wechat.bean.paymch.UnifiedorderResult;
import com.cootf.wechat.config.WXPayConfig;
import com.cootf.wechat.util.JsonUtil;
import com.cootf.wechat.util.MapUtil;
import com.qq.weixin.mp.wxpay.WXPay;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信支付 基于V3.X 版本
 * @author mengsj
 *
 */
public class PayMchNAPI extends BaseAPI{


	private static Logger logger = LoggerFactory.getLogger(PayMchNAPI.class);

	private static ThreadLocal<Boolean> sandboxnew = new ThreadLocal<Boolean>();

	/**
	 * 微信支付配置
	 */
	private static WXPayConfig config = null;

	/**
	 * 微信支付对象
	 */
	private static WXPay wxPay = null;

	/**
	 * 仿真测试 开始
	 * @since 2.8.6
	 */
	public static void sandboxnewStart(){
		sandboxnew.set(true);
	}

	/**
	 * 仿真测试 结束
	 * @since 2.8.6
	 */
	public static void sandboxnewEnd(){
		sandboxnew.set(null);
	}

	/**
	 * 设置微信支付配置
	 * @param t 配置对象
	 * @param <T> 配置对象实例
	 */
	public static <T extends WXPayConfig> void setConfig(T t){
		config = t;
	}

	private static void initWxPay(){
		try {
			if (config != null){
				wxPay = new WXPay(config, config.getNotifyUrl(), config.isAutoReport(),config.isUseSandbox());
			}
		} catch (Exception e) {
			logger.error("init WXPay fail",e);
		}
	}
	
	/**
	 * 获取支付base URI路径
	 * @return baseURI
	 */
	private static String baseURI(){
		if(sandboxnew.get() == null){
			return MCH_URI;
		}else{
			return MCH_URI + "/sandboxnew";
		}
	}
	
	/**
	 * 统一下单
	 * @param unifiedorder unifiedorder
	 * @return UnifiedorderResult
	 */
	public static UnifiedorderResult payUnifiedorder(Unifiedorder unifiedorder){
		Map<String,String> map = MapUtil.objectToMap(unifiedorder, "detail", "scene_info");
		//@since 2.8.8 detail 字段签名处理
		if(unifiedorder.getDetail() != null){
			map.put("detail",JsonUtil.toJSONString(unifiedorder.getDetail()));
		}
		//@since 2.8.21 scene_info 字段签名处理
		if(unifiedorder.getScene_info() != null){
			map.put("scene_info",JsonUtil.toJSONString(unifiedorder.getScene_info()));
		}
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.unifiedOrder(map);
				return MapUtil.mapToObject(result,UnifiedorderResult.class);
			}
			return null;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 刷卡支付  提交付款码支付API
	 * @param micropay micropay
	 * @return MicropayResult
	 */
	public static MicropayResult payMicropay(Micropay micropay){
		Map<String,String> map = MapUtil.objectToMap(micropay);
		//@since 2.8.14 detail 字段签名处理
		if(micropay.getDetail() != null){
			map.put("detail",JsonUtil.toJSONString(micropay.getDetail()));
		}
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.microPay(map);
				return MapUtil.mapToObject(result,MicropayResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 查询订单
	 * @param mchOrderquery mchOrderquery
	 * @return MchOrderInfoResult
	 */
	public static MchOrderInfoResult payOrderquery(MchOrderquery mchOrderquery){
		Map<String,String> map = MapUtil.objectToMap(mchOrderquery);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.orderQuery(map);
				return MapUtil.mapToObject(result,MchOrderInfoResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}



	/**
	 * 关闭订单
	 * @param closeorder closeorder
	 * @return MchBaseResult
	 */
	public static MchBaseResult payCloseorder(Closeorder closeorder){
		Map<String,String> map = MapUtil.objectToMap(closeorder);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.closeOrder(map);
				return MapUtil.mapToObject(result,MchBaseResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}


	/**
	 * 申请退款
	 *
	 * 注意：
	 *	1.交易时间超过半年的订单无法提交退款；
	 *	2.微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。一笔退款失败后重新提交，要采用原来的退款单号。总退款金额不能超过用户实际支付金额。
	 * @param secapiPayRefund secapiPayRefund
	 * @return SecapiPayRefundResult
	 */
	public static SecapiPayRefundResult secapiPayRefund(SecapiPayRefund secapiPayRefund){
		Map<String,String> map = MapUtil.objectToMap( secapiPayRefund);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.refund(map);
				return MapUtil.mapToObject(result,SecapiPayRefundResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 撤销订单
	 * 7天以内的交易单可调用撤销，其他正常支付的单如需实现相同功能请调用申请退款API。提交支付交易后调用【查询订单API】，没有明确的支付结果再调用【撤销订单API】。<br>
	 * 调用支付接口后请勿立即调用撤销订单API，建议支付后至少15s后再调用撤销订单接口。
	 * @param mchReverse mchReverse
	 * @return MchReverseResult
	 */
	public static MchReverseResult secapiPayReverse(MchReverse mchReverse){
		Map<String,String> map = MapUtil.objectToMap( mchReverse);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.reverse(map);
				return MapUtil.mapToObject(result,MchReverseResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 查询退款
	 *
	 * 提交退款申请后，通过调用该接口查询退款状态。退款有一定延时，用零钱支付的退款
	 * 20 分钟内到账，银行卡支付的退款3 个工作日后重新查询退款状态。
	 * @param refundquery refundquery
	 * @return RefundqueryResult
	 */
	public static RefundqueryResult payRefundquery(Refundquery refundquery){
		Map<String,String> map = MapUtil.objectToMap(refundquery);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.refundQuery(map);
				return MapUtil.mapToObject(result,RefundqueryResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 下载对账单
	 * @param downloadbill downloadbill
	 * @return DownloadbillResult
	 */
	public static DownloadbillResult payDownloadbill(MchDownloadbill downloadbill){
		Map<String,String> map = MapUtil.objectToMap(downloadbill);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.downloadBill(map);
				return MapUtil.mapToObject(result,DownloadbillResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}


	/**
	 * 短链接转换
	 * @param shorturl shorturl
	 * @return MchShorturlResult
	 */
	public static MchShorturlResult toolsShorturl(MchShorturl shorturl){
		Map<String,String> map = MapUtil.objectToMap(shorturl);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.shortUrl(map);
				return MapUtil.mapToObject(result,MchShorturlResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 刷卡支付 授权码查询OPENID接口
	 * @param authcodetoopenid authcodetoopenid
	 * @return AuthcodetoopenidResult
	 */
	public static AuthcodetoopenidResult toolsAuthcodetoopenid(Authcodetoopenid authcodetoopenid){
		Map<String,String> map = MapUtil.objectToMap(authcodetoopenid);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.authCodeToOpenid(map);
				return MapUtil.mapToObject(result,AuthcodetoopenidResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 交易保障 <br> 
	 * 测速上报
	 * @param report report
	 * @return MchBaseResult
	 */
	public static MchBaseResult payitilReport(Report report){
		Map<String,String> map = MapUtil.objectToMap(report);
		try {
			initWxPay();
			if (wxPay != null){
				Map<String, String> result = wxPay.report(map);
				return MapUtil.mapToObject(result,MchBaseResult.class);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
}
