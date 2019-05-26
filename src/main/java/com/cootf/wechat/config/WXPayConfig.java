package com.cootf.wechat.config;

import com.qq.weixin.mp.wxpay.IWXPayDomain;
import java.io.InputStream;

/**
 * 微信配置对象
 * @author mengsj
 * @since 2.8.26
 */
public abstract class WXPayConfig {


    /**
     * 是否自动上报
     * @return boolean
     */
    public abstract boolean isAutoReport();

    /**
     * 是否沙箱
     * @return boolean
     */
    public abstract boolean isUseSandbox();

    /**
     * 回调地址
     * @return 回调地址
     */
    public abstract String getNotifyUrl();

    /**
     * 获取 App ID
     *
     * @return App ID
     */
    public abstract String getAppID();


    /**
     * 获取 Mch ID
     *
     * @return Mch ID
     */
    public abstract String getMchID();


    /**
     * 获取 API 密钥
     *
     * @return API密钥
     */
    public abstract String getKey();


    /**
     * 获取商户证书内容
     *
     * @return 商户证书内容
     */
    public abstract InputStream getCertStream();

    /**
     * HTTP(S) 连接超时时间，单位毫秒
     *
     * @return 连接超时时间
     */
    public int getHttpConnectTimeoutMs() {
        return 6*1000;
    }

    /**
     * HTTP(S) 读数据超时时间，单位毫秒
     *
     * @return 读超时时间
     */
    public int getHttpReadTimeoutMs() {
        return 8*1000;
    }

    /**
     * 获取WXPayDomain, 用于多域名容灾自动切换
     * @return WXPayDomain
     */
    public abstract IWXPayDomain getWXPayDomain();

    /**
     * 是否自动上报。
     * 若要关闭自动上报，子类中实现该函数返回 false 即可。
     *
     * @return boolean
     */
    public boolean shouldAutoReport() {
        return true;
    }

    /**
     * 进行健康上报的线程的数量
     *
     * @return 数量
     */
    public int getReportWorkerNum() {
        return 6;
    }


    /**
     * 健康上报缓存消息的最大数量。会有线程去独立上报
     * 粗略计算：加入一条消息200B，10000消息占用空间 2000 KB，约为2MB，可以接受
     *
     * @return 数量
     */
    public int getReportQueueMaxSize() {
        return 10000;
    }

    /**
     * 批量上报，一次最多上报多个数据
     *
     * @return 批量大小
     */
    public int getReportBatchSize() {
        return 10;
    }

}
