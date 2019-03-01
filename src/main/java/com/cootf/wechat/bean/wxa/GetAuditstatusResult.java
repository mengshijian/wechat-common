package com.cootf.wechat.bean.wxa;

import com.cootf.wechat.bean.BaseResult;

public class GetAuditstatusResult extends BaseResult {

	private Integer status;
	private String reason; // 当status=1，审核被拒绝时，返回的拒绝原因

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public String getReason(){
		return reason;
	}
	public void setReason(String reason){
		this.reason = reason;
	}

}
