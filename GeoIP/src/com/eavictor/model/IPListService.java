package com.eavictor.model;

public class IPListService {
	public String IPLists(String country) {
		IPListDAO dao = new IPListDAOhibernate();
		StringBuffer sb = new StringBuffer();
		String[] countries = country.toUpperCase().split(",");
		for (int i = 0; i < countries.length; i++) {
			sb.append(dao.IPv4List(countries[i])).append(dao.IPv6List(countries[i]));
		}
		return sb.toString();
	}
	
	public String IPv4Lists(String country) {
		IPListDAO dao = new IPListDAOhibernate();
		StringBuffer sb = new StringBuffer();
		String[] countries = country.toUpperCase().split(",");
		for (int i = 0; i < countries.length; i++) {
			sb.append(dao.IPv4List(countries[i]));
		}
		return sb.toString();
	}
	
	public String IPv6Lists(String country) {
		IPListDAO dao = new IPListDAOhibernate();
		StringBuffer sb = new StringBuffer();
		String[] countries = country.toUpperCase().split(",");
		for (int i = 0; i < countries.length; i++) {
			sb.append(dao.IPv6List(countries[i]));
		}
		return sb.toString();
	}
}
