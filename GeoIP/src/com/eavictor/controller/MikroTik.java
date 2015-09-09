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

@WebServlet("/MikroTik.do")
public class MikroTik extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MikroTik() {
		super();
	}

	@SuppressWarnings("null")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String realPath = this.getServletContext().getRealPath("/");
		UpdateHandler uh = new UpdateHandler();
		uh.setPath(realPath);
		uh.processUpdate();

		// input null = fail
		String[] countries = request.getParameter("countries").split(",");
		if (countries[0] == null || countries[0].length()==0) {
			
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
