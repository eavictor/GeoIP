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

import com.eavictor.model.IPListDAO;
import com.eavictor.model.UpdateHandler;

@WebServlet("/MikroTik.do")
public class MikroTik extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String realPath = null;

	public void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Auto Update Thread started");
				realPath = MikroTik.this.getServletContext().getRealPath("/");
				UpdateHandler uh = new UpdateHandler();
				uh.setPath(realPath);
				uh.processUpdate();
			}
		}).start();
	}

	public MikroTik() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// input null = fail
		String country = request.getParameter("countries");
		IPListDAO ip = new IPListDAO();
		PrintWriter out = response.getWriter();
		out.print(ip.IPList(country));

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
