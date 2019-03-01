package com.cootf.wechat.bean.scan.crud;


import com.cootf.wechat.bean.scan.base.ProductGet;

public class ProductUpdate extends ProductGet {
    private BrandInfoUpdate brand_info;

    public BrandInfoUpdate getBrand_info() {
        return brand_info;
    }

    public void setBrand_info(BrandInfoUpdate brand_info) {
        this.brand_info = brand_info;
    }
}
