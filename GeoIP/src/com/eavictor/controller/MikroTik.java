package com.eavictor.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eavictor.model.GetIPList;
import com.eavictor.model.UpdateHandler;

@WebServlet("/MikroTik")
public class MikroTik extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MikroTik() {
		super();
	}

	@SuppressWarnings("null")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * If the day matches 2, every request will trigger database update !!
		 * But if I put this 4 line code into init() then this system won't have
		 * auto update function.
		 */
		String realPath = this.getServletContext().getRealPath("/");
		UpdateHandler uh = new UpdateHandler();
		uh.setPath(realPath);
		uh.processUpdate();

		// input null = fail
		String[] countries = request.getParameter("country").split(",");
		
		if (countries == null) {
			countries[0] = "CC";
		}
		GetIPList getIPList = new GetIPList();
		List<String> IPList = new ArrayList<>();
		IPList = getIPList.generateList(countries);
		Iterator<String> iterator = IPList.iterator();
		PrintWriter out = response.getWriter();
		while (iterator.hasNext()) {
			out.print(iterator.next());
		}
		// 將從資料庫拿回的檔案(已經處理成字串陣列)
		// 丟到前端jsp，顯示在<div id="show"></div>內。
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}