package com.cootf.wechat.bean.paymch;

import com.cootf.wechat.bean.paymch.base.BillResult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class DownloadbillResult extends MchBase implements BillResult {

	private String data;

	@Override
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
