package com.concretepage.entity;

import lombok.Data;

@Data
public class Zips {
	


	public Long getZipcode() {
		return zipcode;
	}

	public void setZipcode(Long zipcode) {
		this.zipcode = zipcode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(Integer countyCode) {
		this.countyCode = countyCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRateArea() {
		return rateArea;
	}

	public void setRateArea(Integer rateArea) {
		this.rateArea = rateArea;
	}

	private Long zipcode;
	
	private String state;
	
	private Integer countyCode;
	
	private String name;
	
	private Integer rateArea;

	public Zips(Long zipcode, String state, Integer countyCode, String name, Integer rateArea) {
		super();
		this.zipcode = zipcode;
		this.state = state;
		this.countyCode = countyCode;
		this.name = name;
		this.rateArea = rateArea;
	}


	
}
