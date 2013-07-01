package sistematica.sintelimporter.data;

import org.apache.log4j.Logger;

/**
 * @author gsilvestri
 *
 */
public class Message
{
	static Logger log4j = Logger.getLogger(Message.class);
	
	public final static int TYPE_GET_VERSION = 1;
	public final static int TYPE_GET_DATA = 2;
	public final static int TYPE_GET_EVENTS = 3;
	public final static int TYPE_GET_IMAGE = 4;
	public final static int TYPE_GET_PRODUCT_INFORMATION = 5;
	public final static int TYPE_GET_TIME = 6;
	public final static int TYPE_SET_TIME = 7;
	public final static int TYPE_CLEAR_DATA = 8;
	
//	int msg_type;
//	private byte[] msg = null;
	
	public static String getMessage(int msg_type)
	{
		String msg = "";
		
		if(msg_type == TYPE_GET_VERSION)
		{
			msg = "<Message Type=\"GetVersion\"/>";
		}
		else if(msg_type == TYPE_GET_DATA)
		{
			msg = "<Message Type=\"GetData\"/>";
//			msg = "<PublicSDK Message=\"GetIntegratedData\"/>";
		}
		else if(msg_type == TYPE_GET_EVENTS)
		{
			msg = "<Message Type=\"GetEvents\"/>";
		}
		else if(msg_type == TYPE_GET_IMAGE)
		{
			msg = "<Message Type=\"GetImage\"/>";
		}
		else if(msg_type == TYPE_GET_PRODUCT_INFORMATION)
		{
			msg = "<Message Type=\"GetProductInformation\"/>";
		}
		else if(msg_type == TYPE_GET_TIME)
		{
			msg = "<Message Type=\"GetTime\"/>";
		}
		else if(msg_type == TYPE_SET_TIME)
		{
			msg = "<PublicSDK Message=\"SetTime\" UTC=\"%s\" Milliseconds=\"%s\"/>";
		}
		else if(msg_type == TYPE_CLEAR_DATA)
		{
			msg = "<PublicSDK Message=\"ClearData\"/>";
		}
		else
		{
			log4j.warn("No message mapped for msg_type = " + msg_type + ". Return msg = null..");
			msg = null;
		}
		
		return msg;
	}
	
	
	public static String getTagMessage(int msg_type)
	{
		String msg = "";
		
		if(msg_type == TYPE_GET_VERSION)
		{
			msg = "VERSION";
		}
		else if(msg_type == TYPE_GET_DATA)
		{
			msg = "DATA";
		}
		else if(msg_type == TYPE_GET_EVENTS)
		{
			msg = "EVENTS";
		}
		else if(msg_type == TYPE_GET_IMAGE)
		{
			msg = "IMAGE";
		}
		else if(msg_type == TYPE_GET_PRODUCT_INFORMATION)
		{
			msg = "PROD_INFO";
		}
		else if(msg_type == TYPE_GET_TIME)
		{
			msg = "GET_TIME";
		}
		else if(msg_type == TYPE_SET_TIME)
		{
			msg = "SET_TIME";
		}
		else if(msg_type == TYPE_CLEAR_DATA)
		{
			msg = "CLEAR_DATA";
		}
		else
		{
			log4j.warn("No message mapped for msg_type = " + msg_type + ". Return msg = null..");
			msg = null;
		}
		
		return msg;
	}
}
