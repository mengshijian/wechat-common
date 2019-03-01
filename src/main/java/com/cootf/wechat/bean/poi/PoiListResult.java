package com.cootf.wechat.bean.poi;

import com.alibaba.fastjson.annotation.JSONField;

import com.cootf.wechat.bean.BaseResult;

/**
 * 门店列表信息－响应对象
 * 
 * @author Moyq5
 *
 */
public class PoiListResult extends BaseResult {

	@JSONField(name = "business_list")
	private BusinessResult[] businessList;

	@JSONField(name = "total_count")
	private int totalCount;

	public BusinessResult[] getBusinessList() {
		return businessList;
	}

	public void setBusinessList(BusinessResult[] businessList) {
		this.businessList = businessList;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
