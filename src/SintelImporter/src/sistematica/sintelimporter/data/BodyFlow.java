/**
 * 
 */
package sistematica.sintelimporter.data;

import java.util.ArrayList;

/**
 * @author gsilvestri
 *
 */
public class BodyFlow
{
	public final String type = "FlowSpeedData";
	public String intervalTime = "";
	public String dataNumber = "";
	public String utc = "";
	public String milliseconds;
	
	public ArrayList<ZoneFlow> listZoneFlow;
}
