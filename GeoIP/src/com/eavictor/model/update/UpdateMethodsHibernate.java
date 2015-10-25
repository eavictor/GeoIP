package com.eavictor.model.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.eavictor.model.IPBean;

import hibernate.util.HibernateUtil;

public class UpdateMethodsHibernate implements UpdateMethods {
	private String path;
	private String gzipFile = "countryip.gz";
	private String csvFile = "countryip.csv";
	private static final String hibernate_TRUNCATE = "truncate table GEOIP";
	private static final String hibernate_CHECK_DATABASE_EMPTY = "select count(*) from IPBean";

	/* (non-Javadoc)
	 * @see com.eavictor.model.update.UpdateMethods#dayCount()
	 */
	@Override
	public boolean dayCount() {
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
		if (day == 2 || !this.checkDataExist()) {
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.eavictor.model.update.UpdateMethods#checkDataExist()
	 */
	@Override
	public boolean checkDataExist() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(hibernate_CHECK_DATABASE_EMPTY);
		long total = (long) query.uniqueResult();
		if (total != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.eavictor.model.update.UpdateMethods#setRealPath(java.lang.String)
	 */
	@Override
	public boolean setRealPath(String path) {
		this.path = path;
		System.out.println("File location : " + path);
		if (this.path != null) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.eavictor.model.update.UpdateMethods#downloadZip()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.eavictor.model.update.UpdateMethods#unZip()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.eavictor.model.update.UpdateMethods#doUpdate()
	 */
	@Override
	public boolean doUpdate() {
		List<IPBean> ipbeans = new ArrayList<IPBean>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		int readCount = 0;
		int insertCount = 0;
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
				readCount++;
				if (readCount%1000 == 0) {
					System.out.println(readCount + " Line readed.");
				}
			}
			System.out.println(readCount + " Line readed.");
			
			// insert into database
			try {
				session.beginTransaction();
				for (IPBean ipbean : ipbeans) {
					session.saveOrUpdate(ipbean);
					insertCount++;
					if (insertCount%100 == 0) {
						System.out.println(insertCount + " IP blocks insert processed.");
					}
				}
				System.out.println(insertCount+" IP blocks insert processed.");
				System.out.println("Inserting new data into database...");
				session.getTransaction().commit();
				System.out.println("Insert complete !!");
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
}
