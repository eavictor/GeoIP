package com.eavictor.controller;

import java.io.IOException;
import java.io.PrintWriter;
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

	public void init() {
		//要搬到另一個Servlet內，否則此servlet會因為processUpdate是無窮迴圈導致無法完成init()
		String realPath = this.getServletContext().getRealPath("/");
		System.out.println(realPath);
		UpdateHandler uh = new UpdateHandler();
		uh.setPath(realPath);
		uh.processUpdate();
	}

	public MikroTik() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String[] countries = request.getParameter("country").split(",");
		GetIPList getIPList = new GetIPList();
		List<String> IPList = getIPList.generateList(countries);
		String[] result = new String[IPList.size()];
		//將從資料庫拿回的檔案(已經處理成字串陣列)
		//丟到前端jsp，顯示在<div id="show"></div>內。
		
		//測試用code
		PrintWriter out = response.getWriter();
		for (int i = 0; i < result.length; i++) {
			out.print(result[i]+"<br>");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
