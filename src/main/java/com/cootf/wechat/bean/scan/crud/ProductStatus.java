package com.cootf.wechat.bean.scan.crud;


import com.cootf.wechat.bean.scan.base.ProductGet;


public class ProductStatus extends ProductGet {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
