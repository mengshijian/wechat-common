package com.cootf.wechat.support;

import com.cootf.wechat.bean.paymch.MchPayNotify;
import com.cootf.wechat.bean.paymch.RefundNotifyReqInfo;
import java.util.Map;

/**
 * 微信支付回调接口
 * @Auther: win7
 * @Date: 2019/4/19 17:18
 * @Description:
 */
public abstract class WeChatNotifyProcessor {

  /**
   * 微信支付通知/退款通知
   * @param xmlData
   * @return
   */
  public abstract String processNotify(String xmlData);

  /**
   * 支付通知回调
   * @param notify
   * @return
   */
  public abstract boolean payResultProcess(MchPayNotify notify);

  /**
   * 退款通知回调
   * @param refundNotify
   * @return
   */
  public abstract boolean refundResultProcess(RefundNotifyReqInfo refundNotify);

}
