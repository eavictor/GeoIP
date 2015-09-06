package com.eavictor.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class UpdateHandler {
	private static final String CHECK_DATABASE_EMPTY = "SELECT ip_start FROM GEOIP WHERE country = 'CC'";
	private int day;
	UpdateMethods updateMethods = new UpdateMethods();
	DataSource ds = null;

	public UpdateHandler() {
		Context context = null;
		try {
			context = new InitialContext();
			ds = (DataSource) context.lookup("java:comp/env/jdbc/GEOIP");
			dayCount();
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	public void setPath(String path) {
		updateMethods.setRealPath(path);
	}

	private boolean checkDataExist() {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = ds.getConnection();
			pstmt = connection.prepareStatement(CHECK_DATABASE_EMPTY);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString(1) != null) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean dayCount() {
		day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
		if (day == 2 || !checkDataExist()) {
			return true;
		} else {
			return false;
		}
	}

	public void processUpdate() {
		if (dayCount()) {
			if (updateMethods.downloadZip()) {
				if (updateMethods.unZip()) {
					if (updateMethods.doUpdate()) {
						System.out.println("Database update complete !!");
					}
				}
			}
		} else {
			System.out.println("No update needed");
		}
	}
}
