package com.eavictor.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6AddressRange;
import com.googlecode.ipv6.IPv6Network;

import hibernate.util.HibernateUtil;

public class IPListDAOHibernate implements IPListDAO {
	private static final String hibernate_GET_Client_Request_Count = "from ClientIPBean where clientIP=:clientIP";
	private static final String hibernate_COUNTRY_IPv6 = "from IPBean where upper(country)=:country and ipstart like '%::'";
	private static final String hibernate_COUNTRY_IPv4 = "from IPBean where upper(country)=:country and ipstart like '%.%.%.%'";
	/*
	 * https://docs.jboss.org/hibernate/orm/3.6/reference/en-US/html/queryhql.
	 * html
	 * 
	 * hibernate HQL example 16.10. Expressions
	 */
	
	@Override
	@SuppressWarnings("unchecked")
	public int clientRequestCount(String clientIP) {
		List<ClientIPBean> list = null;
		ClientIPBean clientIPBean = null;
		int requestCount = 0;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		try {
			session.beginTransaction();
			Query query = session.createQuery(hibernate_GET_Client_Request_Count);
			
			query.setString("clientIP", clientIP);
			query.setCacheable(false);
			list = query.list();
			Iterator<ClientIPBean> iterator = list.iterator();
			if(iterator.hasNext()) {
				clientIPBean = iterator.next();
				requestCount = clientIPBean.getRequestCount();
			}
			if (clientIPBean == null || requestCount < Limits.getRequestLimit()) {
				if(clientIPBean==null) {
					clientIPBean = new ClientIPBean();
				}
				clientIPBean.setClientIP(clientIP);
				clientIPBean.setRequestCount(++requestCount);
				session.saveOrUpdate(clientIPBean);
				session.getTransaction().commit();
				return requestCount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(requestCount);
		return requestCount;
	}
	
	// IPv6
	// hibernate 5
	@Override
	@SuppressWarnings("unchecked")
	public StringBuffer IPv6List(String country) {
		List<IPBean> list = null;
		StringBuffer sb = new StringBuffer();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		try {
			session.beginTransaction();
			Query query = session.createQuery(hibernate_COUNTRY_IPv6);

			if (country == null || country.trim().length() != 2) {
				return sb.append("# illegal Country Code (IPv6): " + country + "\r\n");
			} else if (Integer.parseInt(new SimpleDateFormat("dd").format(new Date()))==2) {
				return sb.append("# scheduled database maintenance on every 2nd day of the month !! (IPv6)");
			}
			query.setString("country", country);
			query.setCacheable(true);
			list = query.list();
			session.getTransaction().commit();

			sb.append("/ipv6 firewall address-list\r\nremove [/ipv6 firewall address-list find list=" + country + "]\r\n");

			Iterator<IPBean> iterator = list.iterator();
			while (iterator.hasNext()) {
				IPBean ipbean = iterator.next();
				String IPStart = ipbean.getIpstart();
				String IPEnd = ipbean.getIpend();
				IPv6AddressRange range = IPv6AddressRange.fromFirstAndLast(IPv6Address.fromString(IPStart),
						IPv6Address.fromString(IPEnd));
				Iterator<IPv6Network> subnetsIterator = range.toSubnets();
				while (subnetsIterator.hasNext()) {
					sb.append("add address=" + subnetsIterator.next() + " list=" + country + "\r\n");
				}
			}
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return sb;
	}

	// IPv4
	// hibernate 5
	@Override
	@SuppressWarnings("unchecked")
	public StringBuffer IPv4List(String country) {
		List<IPBean> list = null;
		StringBuffer sb = new StringBuffer();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Query query = session.createQuery(hibernate_COUNTRY_IPv4);
			if (country == null || country.trim().length() != 2) {
				return sb.append("# illegal Country Code (IPv4): " + country + "\r\n");
			} else if (Integer.parseInt(new SimpleDateFormat("dd").format(new Date()))==2) {
				return sb.append("# scheduled database maintenance on every 2nd day of the month !! (IPv4)");
			}
			query.setString("country", country);
			query.setCacheable(true);
			list = query.list();
			session.getTransaction().commit();

			sb.append("/ip firewall address-list\r\nremove [/ip firewall address-list find list=" + country + "]\r\n");

			Iterator<IPBean> iterator = list.iterator();
			while (iterator.hasNext()) {
				IPBean ipbean = iterator.next();
				String IPStart = ipbean.getIpstart();
				String IPEnd = ipbean.getIpend();
				sb.append("add address=" + IPStart + "-" + IPEnd + " list=" + country + "\r\n");
			}
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return sb;
	}
}
