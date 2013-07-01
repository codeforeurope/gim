package sistematica.aggregatore.ws.app;

import java.util.HashSet;
import java.util.Set;

import sistematica.aggregatore.ws.rest.GetFvdData;
import sistematica.aggregatore.ws.rest.MessageRestService;


public class MessageApplication extends javax.ws.rs.core.Application
{
	private Set<Object> singletons = new HashSet<Object>();

	public MessageApplication()
	{
		singletons.add(new MessageRestService());
		singletons.add(new GetFvdData());
	}

	@Override
	public Set<Object> getSingletons()
	{
		return singletons;
	}
}
