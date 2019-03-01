package com.cootf.wechat.bean.wxa;

import java.awt.image.BufferedImage;

import com.cootf.wechat.bean.BaseResult;

public class GetQrcodeResult extends BaseResult{

	private BufferedImage bufferedImage;

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}
	
}
