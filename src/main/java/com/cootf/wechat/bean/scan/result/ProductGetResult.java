package com.cootf.wechat.bean.scan.result;


import com.cootf.wechat.bean.BaseResult;
import com.cootf.wechat.bean.scan.crud.ProductCreate;

public class ProductGetResult extends BaseResult {
    private ProductCreate productCreate;

    public ProductCreate getProductCreate() {
        return productCreate;
    }

    public void setProductCreate(ProductCreate productCreate) {
        this.productCreate = productCreate;
    }
}
