package com.cootf.wechat.support;

import com.cootf.wechat.bean.paymch.MchPayNotify;
import com.cootf.wechat.bean.paymch.RefundNotifyReqInfo;
import java.util.Map;

/**
 * 微信支付回调接口
 * @author mengsj
 * @since : 2.8.26
 */
public abstract class WeChatNotifyProcessor {

  /**
   * 微信支付通知/退款通知
   * @param xmlData 通知参数xml
   * @return 通知处理结果响应
   */
  public abstract String processNotify(String xmlData);

  /**
   * 支付通知回调
   * @param notify 支付通知对象
   * @return 支付通知处理标识
   */
  public abstract boolean payResultProcess(MchPayNotify notify);

  /**
   * 退款通知回调
   * @param refundNotify 退款通知对象
   * @return 退款通知处理标识
   */
  public abstract boolean refundResultProcess(RefundNotifyReqInfo refundNotify);

}
