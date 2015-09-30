package com.eavictor.model;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import hibernate.util.HibernateUtil;

public class IPListDAO {
	private static final String hibernate_COUNTRY = "from IPBean where upper(country)=:country";
	private static final String hibernate_COUNTRY_IPv6 = "from IPBean where upper(country)=:country and ipstart like '%::'";
	private static final String hibernate_COUNTRY_IPv4 = "from IPBean where upper(country)=:country and ipstart like '%.%.%.%'";
	/*
	 * https://docs.jboss.org/hibernate/orm/3.6/reference/en-US/html/queryhql.
	 * html
	 * 
	 * hibernate HQL example 16.10. Expressions
	 */

	// IPv6 and IPv4
	// hibernate 5

	@SuppressWarnings("unchecked")
	public StringBuffer IPList(String country) {
		List<IPBean> list = null;
		StringBuffer sb = new StringBuffer();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		try {
			session.beginTransaction();
			Query query = session.createQuery(hibernate_COUNTRY);

				if (country == null || country.trim().length() != 2) {
					return sb.append("# illegal Country Code: " + country + "\r\n");
				}
				query.setString("country", country);
				list = query.list();
				session.getTransaction().commit();

				sb.append("/ipv6 firewall address-list remove [/ipv6 firewall address-list find list=" + country
						+ "]\r\n");
				sb.append("/ip firewall address-list remove [/ip firewall address-list find list=" + country
						+ "]\r\n");

				Iterator<IPBean> iterator = list.iterator();
				while (iterator.hasNext()) {
					IPBean ipbean = iterator.next();
					String IPStart = ipbean.getIpstart();
					if (IPStart.contains("::")) {
						String[] IPEnd = ipbean.getIpend().split(":");
						int count = 0;
						for (int i = 0; i < IPEnd.length; i++) {
							if (IPEnd[i].equals("ffff")) {
								count++;
							}
						}
						String prefix = IPListDAO.calculatePrefix(count);
						sb.append("/ipv6 firewall address-list add address=" + IPStart + prefix + " list=" + country
								+ "\r\n");
					} else if (IPStart.contains(".")) {
						String IPEnd = ipbean.getIpend();
						sb.append(
								"/ip firewall address-list add address=" + IPStart + "-" + IPEnd + " list=" + country + "\r\n");
					}
				}
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return sb;
	}

	// IPv6
	// hibernate 5

	@SuppressWarnings("unchecked")
	public StringBuffer IPv6List(String country) {
		List<IPBean> list = null;
		StringBuffer sb = new StringBuffer();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		try {
			session.beginTransaction();
			Query query = session.createQuery(hibernate_COUNTRY_IPv6);

				if (country == null || country.trim().length() != 2) {
					return sb.append("# illegal Country Code: " + country + "\r\n");
				}
				query.setString("country", country);
				list = query.list();
				session.getTransaction().commit();

				sb.append("/ipv6 firewall address-list remove [/ipv6 firewall address-list find list=" + country
						+ "]\r\n");

				Iterator<IPBean> iterator = list.iterator();
				while (iterator.hasNext()) {
					IPBean ipbean = iterator.next();
					String IPStart = ipbean.getIpstart();
					String[] IPEnd = ipbean.getIpend().split(":");
					int count = 0;
					for (int i = 0; i < IPEnd.length; i++) {
						if (IPEnd[i].equals("ffff")) {
							count++;
						}
					}
					String prefix = IPListDAO.calculatePrefix(count);
					sb.append("/ipv6 firewall address-list add address=" + IPStart + prefix + " list=" + country + "\r\n");
				}
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return sb;
	}

	// IPv4
	// hibernate 5

	@SuppressWarnings("unchecked")
	public StringBuffer IPv4List(String country) {
		List<IPBean> list = null;
		StringBuffer sb = new StringBuffer();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Query query = session.createQuery(hibernate_COUNTRY_IPv4);
			if (country == null || country.trim().length() != 2) {
				return sb.append("# illegal Country Code: " + country + "\r\n");
			}
			query.setString("country", country);
			list = query.list();
			session.getTransaction().commit();

			sb.append("/ip firewall address-list remove [/ip firewall address-list find list=" + country + "]\r\n");

			Iterator<IPBean> iterator = list.iterator();
			while (iterator.hasNext()) {
				IPBean ipbean = iterator.next();
				String IPStart = ipbean.getIpstart();
				String IPEnd = ipbean.getIpend();
				sb.append("/ip firewall address-list add address=" + IPStart + "-" + IPEnd + " list=" + country + "\r\n");
			}
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return sb;
	}
	
	private static String calculatePrefix(int count) {
		String prefix = "/128";
		switch(count) {
		case 0:
			break;
		case 1:
			prefix = "/112";
			break;
		case 2:
			prefix = "/96";
			break;
		case 3:
			prefix = "/80";
			break;
		case 4:
			prefix = "/64";
			break;
		case 5:
			prefix = "/48";
			break;
		case 6:
			prefix = "/32";
			break;
		case 7:
			prefix = "/16";
			break;
		case 8:
			prefix = "/0";
			break;
		}
		return prefix;
	}
	
}
