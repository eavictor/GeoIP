package com.eavictor.model;

public class IPListService {
	public String IPLists(String country, String clientIP) {
		IPListDAO dao = new IPListDAOHibernate();
		if (dao.clientRequestCount(clientIP) < Limits.getRequestLimit()) {
			StringBuffer sb = new StringBuffer();
			String[] countries = country.toUpperCase().split(",");
			for (int i = 0; i < countries.length; i++) {
				sb.append(dao.IPv4List(countries[i])).append(dao.IPv6List(countries[i]));
			}
			return sb.toString();
		} else {
			return "# update limit exceed, please don't attack server.";
		}
	}

	public String IPv4Lists(String country, String clientIP) {
		IPListDAO dao = new IPListDAOHibernate();
		if (dao.clientRequestCount(clientIP) < Limits.getRequestLimit()) {
			StringBuffer sb = new StringBuffer();
			String[] countries = country.toUpperCase().split(",");
			for (int i = 0; i < countries.length; i++) {
				sb.append(dao.IPv4List(countries[i]));
			}
			return sb.toString();
		} else {
			return "# update limit exceed, please don't attack server.";
		}
	}

	public String IPv6Lists(String country, String clientIP) {
		IPListDAO dao = new IPListDAOHibernate();
		if (dao.clientRequestCount(clientIP) < Limits.getRequestLimit()) {
			StringBuffer sb = new StringBuffer();
			String[] countries = country.toUpperCase().split(",");
			for (int i = 0; i < countries.length; i++) {
				sb.append(dao.IPv6List(countries[i]));
			}
			return sb.toString();
		} else {
			return "# update limit exceed, please don't attack server.";
		}
	}
}
