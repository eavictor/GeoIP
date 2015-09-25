package com.eavictor.model;

import java.io.Serializable;

public class IPBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String IPStart;
	private String IPEnd;
	private String Country;

	public String getIPStart() {
		return IPStart;
	}

	public void setIPStart(String iPStart) {
		IPStart = iPStart;
	}

	public String getIPEnd() {
		return IPEnd;
	}

	public void setIPEnd(String iPEnd) {
		IPEnd = iPEnd;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}
}
