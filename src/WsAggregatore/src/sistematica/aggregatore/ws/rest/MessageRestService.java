package sistematica.aggregatore.ws.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

@Path("/test")
public class MessageRestService
{
    private static final Logger log4j = Logger.getLogger(MessageRestService.class);


	@GET
	public Response printMessage()
	{
		log4j.info("RestEasy WS status: Ok");
		String result = "RestEasy WS status: Ok";
		
		return Response.status(200).entity(result).build();
	}
}
