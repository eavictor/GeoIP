package com.eavictor.model;

import java.io.Serializable;

public class ClientIPBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String clientIP;
	private int requestCount;
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public int getRequestCount() {
		return requestCount;
	}
	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}
}
