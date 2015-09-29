package com.eavictor.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Query;

//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;

import org.hibernate.Session;

import hibernate.util.HibernateUtil;

public class UpdateHandler {
//	private static final String CHECK_DATABASE_EMPTY = "SELECT ip_start FROM GEOIP WHERE country='CC'";
	private static final String hibernate_CHECK_DATABASE_EMPTY = "select count(*) from IPBean";
	private int day;
	UpdateMethods updateMethods = new UpdateMethods();
//	DataSource ds = null;

//	public UpdateHandler() {
//		Context context = null;
//		try {
//			context = new InitialContext();
//			ds = (DataSource) context.lookup("java:comp/env/jdbc/GEOIP");
//			dayCount();
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
//	}

	public void setPath(String path) {
		updateMethods.setRealPath(path);
	}
	
	private boolean checkDataExist() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(hibernate_CHECK_DATABASE_EMPTY);
		long total = (long) query.uniqueResult();
		System.out.println(total+ " IP blocks in database.");
		if (total != 0) {
			return true;
		} else {
			return false;
		}
	}
	
//	private boolean checkDataExist() {
//		Connection connection = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//			connection = ds.getConnection();
//			pstmt = connection.prepareStatement(CHECK_DATABASE_EMPTY);
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				if (rs.getString(1) != null) {
//					return true;
//				} else {
//					return false;
//				}
//			} else {
//				return false;
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	private boolean dayCount() {
		day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
		if (day == 2 || !checkDataExist()) {
			return true;
		} else {
			return false;
		}
	}

	public void processUpdate() {
		while (true) {
			if (dayCount()) {
				if (updateMethods.downloadZip()) {
					if (updateMethods.unZip()) {
						if (updateMethods.doUpdate()) {
							System.out.println("Database update complete !!");
							try {
								Thread.sleep(86400000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} else {
				System.out.println("No update needed.");
				try {
					Thread.sleep(86400000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
