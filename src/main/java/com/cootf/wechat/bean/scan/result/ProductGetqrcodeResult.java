package com.cootf.wechat.bean.scan.result;

import com.cootf.wechat.bean.BaseResult;

public class ProductGetqrcodeResult extends BaseResult {
    private String pic_url;
    private String qrcode_url;

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }
}
