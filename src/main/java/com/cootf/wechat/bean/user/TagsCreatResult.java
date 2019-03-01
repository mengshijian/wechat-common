package com.cootf.wechat.bean.user;

import com.cootf.wechat.bean.BaseResult;

/**
 * 标签
 * 
 * @author mengsj
 * 
 */
public class TagsCreatResult extends BaseResult {

	private Tag tag;

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
