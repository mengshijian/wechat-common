package com.cootf.wechat.bean.wxopen;

import java.util.List;

import com.cootf.wechat.bean.BaseResult;

public class TemplateListResult extends BaseResult {

	private List<TemplateListItem> list;

	public List<TemplateListItem> getList() {
		return list;
	}

	public void setList(List<TemplateListItem> list) {
		this.list = list;
	}

}
