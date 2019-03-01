package com.cootf.wechat.bean.user;

import java.util.List;

import com.cootf.wechat.bean.BaseResult;

/**
 * 标签
 * 
 * @author mengsj
 * 
 */
public class TagsGetResult extends BaseResult {

	private List<Tag> tags;

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
}
