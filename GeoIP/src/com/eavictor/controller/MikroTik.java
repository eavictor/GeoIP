package com.eavictor.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eavictor.model.IPListService;
import com.eavictor.model.update.UpdateHandler;

@WebServlet(value = "/MikroTik.do", loadOnStartup = 1)
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
		String country = request.getParameter("countries");
		String listType = request.getParameter("listType");
		IPListService service = new IPListService();
		PrintWriter out = response.getWriter();
		if (listType.equals("v4v6")) {
			out.print(service.IPLists(country));
		} else if (listType.equals("v4")) {
			out.print(service.IPv4Lists(country));
		} else if (listType.equals("v6")) {
			out.print(service.IPv6Lists(country));
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
