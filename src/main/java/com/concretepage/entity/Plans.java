package com.concretepage.entity;

import lombok.Data;

@Data
public class Plans {

public String getplanId() {
		return planId;
	}

	public void setplanId(String planId) {
		this.planId = planId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getmetalLevel() {
		return metalLevel;
	}

	public void setmetalLevel(String metalLevel) {
		this.metalLevel = metalLevel;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public Integer getRateArea() {
		return rateArea;
	}

	public void setRateArea(Integer rateArea) {
		this.rateArea = rateArea;
	}

public Plans (String planId, String state, String metalLevel, float rate, int rateArea) {
	this.planId = planId;
	this.state = state;
	this.metalLevel = metalLevel;
	this.rate = rate;
	this.rateArea=rateArea;
	}

private String planId;
	
	private String state;
	
	private String metalLevel;
	
	private Float rate;
	
	private Integer rateArea;

	
}
