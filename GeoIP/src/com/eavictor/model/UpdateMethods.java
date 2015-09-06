package com.eavictor.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;

public class UpdateMethods {
	private String path;
	private String gzipFile = "countryip.gz";
	private String csvFile = "countryip.csv";
	private static final String TRUNCATE = "TRUNCATE TABLE GEOIP";
	private static final String INSERT = "INSERT INTO GEOIP (ip_start,ip_end,country) VALUES(?,?,?)";
	private DataSource ds = null;

	public UpdateMethods() {
		Context context = null;
		try {
			context = new InitialContext();
			ds = (DataSource) context.lookup("java:comp/env/jdbc/GEOIP");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public boolean setRealPath(String path) {
		this.path = path;
		if (this.path != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean downloadZip() {
		if (path != null) {
			try {
				String dateString = new SimpleDateFormat("yyyy-MM").format(new Date());
				URL url = new URL("http://download.db-ip.com/free/dbip-country-" + dateString + ".csv.gz");
				File file = new File(path + gzipFile);
				FileUtils.copyURLToFile(url, file, 5000, 5000);
				return true;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean unZip() {
		byte[] buffer = new byte[8192];
		GZIPInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new GZIPInputStream(new FileInputStream(path + gzipFile));
			outputStream = new FileOutputStream(path + csvFile);
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean doUpdate() {
		BufferedReader br = null;
		String line = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			br = new BufferedReader(new FileReader(path + csvFile));
			connection = ds.getConnection();
			List<IPBlockBean> ipBlock = new ArrayList<>();
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(TRUNCATE);
			pstmt.executeUpdate();
			connection.commit();
			pstmt = connection.prepareStatement(INSERT);
			line = br.readLine();
			while (line != null) {
				String[] stringArray = line.split(",");
				line = br.readLine();
				IPBlockBean ipBlockBean = new IPBlockBean();
				ipBlockBean.setIPStart(stringArray[0].replace("\"", ""));
				ipBlockBean.setIPEnd(stringArray[1].replace("\"", ""));
				ipBlockBean.setCountry(stringArray[2].replace("\"", ""));
				ipBlock.add(ipBlockBean);
			}
			for (IPBlockBean ipBlockBean : ipBlock) {
				pstmt.setString(1, ipBlockBean.getIPStart());
				pstmt.setString(2, ipBlockBean.getIPEnd());
				pstmt.setString(3, ipBlockBean.getCountry());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			connection.commit();
			connection.setAutoCommit(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
