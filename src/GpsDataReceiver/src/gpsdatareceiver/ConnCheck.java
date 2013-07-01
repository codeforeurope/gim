package gpsdatareceiver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ConnCheck
 */
public class ConnCheck extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    public ConnCheck()
    {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		out.println("OK");
		
		out.flush();
		out.close();
	}
}
