package com.cootf.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.cootf.exception.AppCommonException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付宝支付
 */
public class AliPay {

  private static final Logger log = LoggerFactory.getLogger(AliPay.class);

  private static AliPayConfig aliPayConfig = null;

  public static <T extends AliPayConfig> void setConfig(T t){
    aliPayConfig = t;
  }
  /**
   * 功能：请求支付宝支付，生成支付交易.
   */
  public String generateAliPay(Map<String, String> param) {
    if (aliPayConfig == null){
      return null;
    }
    AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getServerUrl(),
        aliPayConfig.getAppID(), aliPayConfig.getTestPrivateKey(), "json", "utf-8",
        aliPayConfig.getTestPublicKey(), aliPayConfig.getSignType());
    //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
    AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
    //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
    AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
    model.setBody(param.get("body"));
    model.setSubject(param.get("subject"));
    model.setOutTradeNo(param.get("outTradeNo"));
    model.setTimeoutExpress("30m");
    model.setTotalAmount(param.get("totalAmount"));
    model.setProductCode("QUICK_MSECURITY_PAY");
    request.setBizModel(model);
    request.setNotifyUrl(param.get("notifyUrl"));
    try {
      //这里和普通的接口调用不同，使用的是sdkExecute
      AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
      log.info("支付宝返回:{}", response.getBody());
      return response.getBody();
    } catch (AlipayApiException e) {
      //包含了超时的异常,抛出自定义异常
      throw new AppCommonException(e.getMessage());
    }
  }

  /**
   * 功能：封装支付宝支付请求参数.
   *
   * @param body 商品名称.
   * @param subject 交易主题.
   * @param tradeNo 订单编号.
   * @param totalFee 交易价格.
   * @param notifyUrl 支付回调地址.
   * @return 参数集合.
   */
  public Map<String, String> createAlipayReqData(String body, String subject, String tradeNo,
      String totalFee, String notifyUrl) {
    Map<String, String> param = new HashMap<>();
    param.put("body", body);
    param.put("subject", subject);
    param.put("outTradeNo", tradeNo);
    param.put("totalAmount", totalFee);
    param.put("notifyUrl", notifyUrl);
    return param;
  }

}
