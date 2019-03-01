package com.cootf.wechat.bean.wxa;

import java.util.List;

import com.cootf.wechat.bean.BaseResult;

public class GetPageResult extends BaseResult {

	private List<String> page_list;

	public List<String> getPage_list() {
		return page_list;
	}

	public void setPage_list(List<String> page_list) {
		this.page_list = page_list;
	}

}
