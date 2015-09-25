package com.eavictor.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class IPListDAO {
	private static final String COUNTRY = "SELECT ip_start,ip_end FROM GEOIP WHERE country=? ORDER BY ip_start";
	private static final String COUNTRY_IPv6 = "SELECT ip_start,ip_end FROM GEOIP WHERE country=? AND ip_start LIKE '%::'";
	private static final String COUNTRY_IPv4 = "SELECT ip_start,ip_end FROM GEOIP WHERE country=? AND ip_start LIKE '%.%.%.%'";
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
