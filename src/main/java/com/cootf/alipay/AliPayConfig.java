package com.cootf.alipay;


/**
 * 支付宝支付配置
 * @author mengsj
 * @since 3.0.0
 */
public abstract class AliPayConfig {

  public abstract String getAppID();

  public abstract String getServerUrl();

  public abstract String getTestPrivateKey();

  public abstract String getTestPublicKey();

  public abstract String getSignType();
}
