package com.eavictor.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Query;
import org.hibernate.Session;

import hibernate.util.HibernateUtil;

public class IPListDAO {
	private static final String COUNTRY = "SELECT ip_start,ip_end FROM GEOIP WHERE country=?";
	private static final String COUNTRY_IPv6 = "SELECT ip_start,ip_end FROM GEOIP WHERE country=? AND ip_start LIKE '%::'";
	private static final String COUNTRY_IPv4 = "SELECT ip_start,ip_end FROM GEOIP WHERE country=? AND ip_start LIKE '%.%.%.%'";

	private static final String hibernate_COUNTRY = "from IPBean where country=?";
	private static final String hibernate_COUNTRY_IPv6 = "from IPBean where upper(country)=? and IPStart like '%::'";
	private static final String hibernate_COUNTRY_IPv4 = "from IPBean where upper(country)=? and IPStart like '%.%.%.%'";
	/*
	 * https://docs.jboss.org/hibernate/orm/3.6/reference/en-US/html/queryhql.
	 * html
	 * 
	 * search keyword : 16.10. Expressions from DomesticCat cat where
	 * upper(cat.name) like 'FRI%'
	 * 
	 */

	private DataSource ds = null;

	public IPListDAO() {
		Context context = null;
		try {
			context = new InitialContext();
			ds = (DataSource) context.lookup("java:comp/env/jdbc/GEOIP");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	// IPv6 and IPv4
	@SuppressWarnings("unchecked")
	public String IPListVer2(String country) {
		String[] countries = country.split(",");
		List<IPBean> list = null;
		StringBuffer sb = new StringBuffer();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		try {
			session.beginTransaction();
			Query query = session.createQuery(hibernate_COUNTRY);
			
			for (int i = 0; i < countries.length; i++) {
				
				query.setString(0, countries[i]);
				list = query.list();
				session.getTransaction().commit();
				
				sb.append("/ipv6 firewall address-list remove [/ipv6 firewall address-list find list="+countries[i]+"]\r\n");
				sb.append("/ip firewall address-list remove [/ip firewall address-list find list="+countries[i]+"]\r\n");
				
				Iterator<IPBean> iterator = list.iterator();
				while (iterator.hasNext()) {
					IPBean ipbean = iterator.next();
					String IPStart = ipbean.getIpstart();
					String IPEnd = ipbean.getIpend();
					if (IPStart.contains("::")){
						sb.append("/ipv6 firewall add address="+IPStart+"-"+IPEnd+" list="+countries[i]+"\r\n");
					} else if (IPStart.contains(".")) {
						sb.append("/ip firewall add address="+IPStart+"-"+IPEnd+" list="+countries[i]+"\r\n");
					}
				}
			}
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String IPList(String country) {
		String[] countries = country.split(",");
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		try (Connection connection = ds.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(COUNTRY);) {
			for (int i = 0; i < countries.length; i++) {
				pstmt.setString(1, countries[i]);
				rs = pstmt.executeQuery();
				sb.append("/ip firewall address-list remove [/ip firewall address-list find list=" + countries[i]
						+ "]\r\n" + "/ipv6 firewall address-list remove [/ipv6 firewall address-list find list="
						+ countries[i] + "]\r\n");
				while (rs.next()) {
					String IPStart = rs.getString(1);
					String IPEnd = rs.getString(2);
					if (IPStart.contains("::")) {
						sb.append("/ipv6 firewall add address=" + IPStart + "-" + IPEnd + " list=" + countries[i]
								+ "\r\n");
					} else {
						sb.append(
								"/ip firewall add address=" + IPStart + "-" + IPEnd + " list=" + countries[i] + "\r\n");
					}
				}
			}
			return sb.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// IPv6
	public String IPv6List(String country) {
		String[] countries = country.split(",");
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		try (Connection connection = ds.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(COUNTRY_IPv6);) {
			for (int i = 0; i < countries.length; i++) {
				pstmt.setString(1, countries[i]);
				rs = pstmt.executeQuery();
				sb.append("/ipv6 firewall address-list remove [/ipv6 firewall address-list find list=" + countries[i]
						+ "]\r\n");
				while (rs.next()) {
					sb.append("add address=" + rs.getString(1) + "-" + rs.getString(2) + " list=" + countries[i]
							+ "\r\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	// IPv4
	public String IPv4List(String country) {
		String[] countries = country.split(",");
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		try (Connection connection = ds.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(COUNTRY_IPv4);) {
			for (int i = 0; i < countries.length; i++) {
				pstmt.setString(1, countries[i]);
				rs = pstmt.executeQuery();
				sb.append("/ip firewall address-list remove[/ip firewall address-list find list=" + countries[i]
						+ "]\r\n");
				while (rs.next()) {
					sb.append("add address=" + rs.getString(1) + "-" + rs.getString(2) + " list=" + countries[i]
							+ "\r\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
