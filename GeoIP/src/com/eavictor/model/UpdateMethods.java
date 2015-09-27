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
import org.hibernate.HibernateException;
import org.hibernate.Session;

import hibernate.util.HibernateUtil;

public class UpdateMethods {
	private String path;
	private String gzipFile = "countryip.gz";
	private String csvFile = "countryip.csv";
	private static final String TRUNCATE = "TRUNCATE TABLE GEOIP";
	private static final String INSERT = "INSERT INTO GEOIP (ip_start,ip_end,country) VALUES(?,?,?)";
	private static final String hibernate_TRUNCATE = "truncate table GEOIP";
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
		System.out.println("File location : " + path);
		if (this.path != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean downloadZip() {
		if (path != null) {
			try {
				System.out.println("start downloading zip file");
				String dateString = new SimpleDateFormat("yyyy-MM").format(new Date());
				URL url = new URL("http://download.db-ip.com/free/dbip-country-" + dateString + ".csv.gz");
				File file = new File(path + gzipFile);
				FileUtils.copyURLToFile(url, file, 5000, 5000);
				System.out.println("zip file download complete");
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
		System.out.println("start unzip");
		byte[] buffer = new byte[8192];
		try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(path + gzipFile));
				FileOutputStream outputStream = new FileOutputStream(path + csvFile);) {
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			System.out.println("unzip complete");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean doUpdate() {
		List<IPBean> ipbeans = new ArrayList<IPBean>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		int count = 0;
		try (
				BufferedReader br = new BufferedReader(new FileReader(path + csvFile));
				) {
			
			// wipe old data
			session.beginTransaction();
			session.createSQLQuery(hibernate_TRUNCATE).executeUpdate(); //do not commit !!
			
			// read IP block from file, and store it into a list
			String oneLine = br.readLine();
			while (oneLine != null) {
				String[] oneLineMatrix = oneLine.split(",");
				IPBean ipbean = new IPBean();
				ipbean.setIpstart(oneLineMatrix[0].replace("\"",""));
				ipbean.setIpend(oneLineMatrix[1].replace("\"",""));
				ipbean.setCountry(oneLineMatrix[2].replace("\"",""));
				ipbeans.add(ipbean);
				oneLine = br.readLine();
			}
			
			// insert into database
			try {
				session.beginTransaction();
				for (IPBean ipbean : ipbeans) {
					session.saveOrUpdate(ipbean);
					count++;
					if (count%1000==0){
						System.out.println(count);
					}
				}
				System.out.println(count);
				session.getTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
//	public boolean doUpdate() {
//		BufferedReader br = null;
//		String line = null;
//		Connection connection = null;
//		PreparedStatement pstmt = null;
//		try {
//			System.out.println("start update");
//			br = new BufferedReader(new FileReader(path + csvFile));
//			connection = ds.getConnection();
//			List<IPBean> ipBlock = new ArrayList<>();
//			connection.setAutoCommit(false);
//			pstmt = connection.prepareStatement(TRUNCATE);
//			pstmt.executeUpdate();
//			connection.commit();
//			pstmt = connection.prepareStatement(INSERT);
//			line = br.readLine();
//			while (line != null) {
//				String[] stringArray = line.replace("\"", "").split(",");
//				line = br.readLine();
//				IPBean ipBlockBean = new IPBean();
//				ipBlockBean.setIpstart(stringArray[0]);
//				ipBlockBean.setIpend(stringArray[1]);
//				ipBlockBean.setCountry(stringArray[2]);
//				ipBlock.add(ipBlockBean);
//			}
//			System.out.println("read from csv file complete");
//			System.out.println("insert new data into database");
//			int count = 0;
//			for (IPBean ipBlockBean : ipBlock) {
//				pstmt.setString(1, ipBlockBean.getIpstart());
//				pstmt.setString(2, ipBlockBean.getIpend());
//				pstmt.setString(3, ipBlockBean.getCountry());
//				pstmt.addBatch();
//				count++;
//				if (count % 1000 == 0) {
//					System.out.println("execute batch update "+Math.floor(count/1000));
//					pstmt.executeBatch();
//					connection.commit();
//				}
//			}
//			System.out.println("execute batch update (final)");
//			pstmt.executeBatch();
//			connection.commit();
//			System.out.println("commit OK");
//			connection.setAutoCommit(true);
//			System.out.println("set auto commit true");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			if (connection != null) {
//				try {
//					connection.rollback();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//			return false;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			if (connection != null) {
//				try {
//					connection.rollback();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//			return false;
//		} catch (IOException e) {
//			e.printStackTrace();
//			if (connection != null) {
//				try {
//					connection.rollback();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//			return false;
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return true;
//	}

}
