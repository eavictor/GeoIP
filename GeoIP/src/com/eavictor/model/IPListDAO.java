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
	// private static final String[] GLOBAL_COUNTRIES = { "AD", "AE", "AF",
	// "AG", "AI", "AL", "AM", "AN", "AO", "AQ", "AR",
	// "AS", "AT", "AU", "AW", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH",
	// "BI", "BL", "BM", "BN", "BO", "BR",
	// "BS", "BT", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI",
	// "CK", "CL", "CM", "CN", "CO", "CR",
	// "CU", "CV", "CW", "CX", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ",
	// "EC", "EE", "EG", "EH", "ER", "ES",
	// "ET", "FI", "FJ", "FK", "FM", "FO", "FR", "GA", "GB", "GD", "GE", "GG",
	// "GH", "GI", "GL", "GM", "GN", "GQ",
	// "GR", "GT", "GU", "GW", "GY", "HK", "HN", "HR", "HT", "HU", "ID", "IE",
	// "IL", "IM", "IN", "IO", "IQ", "IR",
	// "IS", "IT", "JE", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM", "KN",
	// "KP", "KR", "KW", "KY", "KZ", "LA",
	// "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC",
	// "MD", "ME", "MF", "MG", "MH", "MK",
	// "ML", "MM", "MN", "MO", "MP", "MR", "MS", "MT", "MU", "MV", "MW", "MX",
	// "MY", "MZ", "NA", "NC", "NE", "NG",
	// "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "OM", "PA", "PE", "PF", "PG",
	// "PH", "PK", "PL", "PM", "PN", "PR",
	// "PS", "PT", "PW", "PY", "QA", "RE", "RO", "RS", "RU", "RW", "SA", "SB",
	// "SC", "SD", "SE", "SG", "SH", "SI",
	// "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "SS", "ST", "SV", "SX", "SY",
	// "SZ", "TC", "TD", "TG", "TH", "TJ",
	// "TK", "TL", "TM", "TN", "TO", "TR", "TT", "TV", "TW", "TZ", "UA", "UG",
	// "US", "UY", "UZ", "VA", "VC", "VE",
	// "VG", "VI", "VN", "VU", "WF", "WS", "XK", "YE", "YT", "ZA", "ZM", "ZW" };
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
