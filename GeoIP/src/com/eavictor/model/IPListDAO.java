package com.eavictor.model;

public interface IPListDAO {
	
	int clientRequestCount(String clientIP);
	
	StringBuffer IPv6List(String country);

	StringBuffer IPv4List(String country);

}