package com.cootf.alipay;

/**
 * 支付宝支付配置
 * @Auther: win7
 * @Date: 2019/4/19 19:55
 * @Description:
 */
public abstract class AliPayConfig {

  public abstract String getAppID();

  public abstract String getServerUrl();

  public abstract String getTestPrivateKey();

  public abstract String getTestPublicKey();

  public abstract String getSignType();
}
