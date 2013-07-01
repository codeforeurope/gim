/**
 * 
 */
package sistematica.sintelimporter.data;

import java.util.ArrayList;

/**
 * @author gsilvestri
 *
 */
public class BodyIntegrated
{
	public final String type = "IntegratedData";
	public String intervalTime = "";
	public String dataNumber = "";
	public String utc = "";
	public String milliseconds;
	
	public ArrayList<ZoneIntegrated> listZoneIntegrated;
}
