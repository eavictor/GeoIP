package com.eavictor.model;

import java.io.Serializable;

public class IPBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String ipstart;
	private String ipend;
	private String country;
	public String getIpstart() {
		return ipstart;
	}
	public void setIpstart(String ipstart) {
		this.ipstart = ipstart;
	}
	public String getIpend() {
		return ipend;
	}
	public void setIpend(String ipend) {
		this.ipend = ipend;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
}