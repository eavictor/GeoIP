package com.eavictor.restful.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.eavictor.model.IPListService;

@Path("/ip")
public class RESTfulServices {
	
	@GET
	@Path("/v4/{country}")
	@Produces("text/plain")
	public String getIPv4List(@PathParam("country") String country) {
		IPListService service = new IPListService();
		return service.IPv4Lists(country);
	}
	
	@GET
	@Path("/v6/{country}")
	@Produces("text/plain")
	public String getIPv6List(@PathParam("country") String country) {
		IPListService service = new IPListService();
		
		return service.IPv6Lists(country);
	}
	
	@GET
	@Path("/both/{country}")
	@Produces("text/plain")
	public String getIPList(@PathParam("country") String country) {
		IPListService service = new IPListService();
		return service.IPLists(country);
	}
}
