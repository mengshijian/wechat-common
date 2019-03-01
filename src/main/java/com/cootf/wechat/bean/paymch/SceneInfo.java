package com.cootf.wechat.bean.paymch;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.cootf.wechat.util.JsonUtil;

public class SceneInfo {

	private H5Info h5_info;

	public H5Info getH5_info() {
		return h5_info;
	}

	public void setH5_info(H5Info h5_info) {
		this.h5_info = h5_info;
	}

	public static class H5Info {

		private String type;

		private String app_name;

		private String package_name;

		private String wap_url;

		private String wap_name;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getApp_name() {
			return app_name;
		}

		public void setApp_name(String app_name) {
			this.app_name = app_name;
		}

		public String getPackage_name() {
			return package_name;
		}

		public void setPackage_name(String package_name) {
			this.package_name = package_name;
		}

		public String getWap_url() {
			return wap_url;
		}

		public void setWap_url(String wap_url) {
			this.wap_url = wap_url;
		}

		public String getWap_name() {
			return wap_name;
		}

		public void setWap_name(String wap_name) {
			this.wap_name = wap_name;
		}
	}

	static class JsonXmlAdapter extends XmlAdapter<String, SceneInfo> {

		@Override
		public String marshal(SceneInfo arg0) throws Exception {
			return "<![CDATA[" + JsonUtil.toJSONString(arg0) + "]]>";
		}

		@Override
		public SceneInfo unmarshal(String arg0) throws Exception {
			return JsonUtil.parseObject(arg0, SceneInfo.class);
		}
	}

}
