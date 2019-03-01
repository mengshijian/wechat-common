package com.cootf.wechat.bean.card.get;

import com.cootf.wechat.bean.BaseResult;
import com.cootf.wechat.bean.card.AbstractCard;

/**
 * 
 * @author Moyq5
 *
 * @param <T>可以是CreateCash、CreateDiscount、CreateGeneralCoupon、CreateGift、CreateGroupon
 */
public class GetResult<T extends AbstractCard> extends BaseResult {

	private T card;

	public T getCard() {
		return card;
	}

	public void setCard(T card) {
		this.card = card;
	}
}