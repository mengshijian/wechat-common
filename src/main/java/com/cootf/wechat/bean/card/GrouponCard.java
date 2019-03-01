package com.cootf.wechat.bean.card;

/**
 * 团购券
 * 
 * @author Moyq5
 *
 */
public class GrouponCard extends AbstractCard {

	private Groupon groupon;

	public GrouponCard() {
		super("GROUPON");
	}

	public Groupon getGroupon() {
		return groupon;
	}

	public void setGroupon(Groupon groupon) {
		this.groupon = groupon;
	}

}
