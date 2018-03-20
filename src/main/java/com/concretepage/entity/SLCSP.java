package com.concretepage.entity;

import lombok.Data;

@Data
public class SLCSP {

	public SLCSP(Long zipCode, Integer rate) {
		this.zipcode = zipCode;
		this.rate = rate;
	}
	private Long zipcode;
	private Integer rate;
	public Long getZipcode() {
		return zipcode;
	}
	public void setZipcode(Long zipcode) {
		this.zipcode = zipcode;
	}
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	
}
