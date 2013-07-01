/**
 * 
 */
package sistematica.sintelimporter.data;

import org.apache.log4j.Logger;

/**
 * @author gsilvestri
 *
 */
public class XmlDataMsg
{
	Logger log4j = Logger.getLogger(XmlDataMsg.class);
	
	public BodyRoot bodyRoot;

	public void stamp()
	{
		if(this.bodyRoot.listMessageType != null)
		{
			log4j.debug("-------------------------------------------------");
			log4j.debug("Find 10 MessageType: ");
			int countType = 0;
			for(MessageType type: this.bodyRoot.listMessageType)
			{
				
				if(type.bodyFlow != null)
				{
					countType++;
					
					log4j.debug("--> MessageType #" + countType + " - FlowSpeedData: ");
					log4j.debug("	IntervalTime = " + type.bodyFlow.intervalTime);
					log4j.debug("	  DataNumber = " + type.bodyFlow.dataNumber);
					log4j.debug("	         UTC = " + type.bodyFlow.utc);
					log4j.debug("	Milliseconds = " + type.bodyFlow.milliseconds);
					
					if(type.bodyFlow.listZoneFlow != null)
					{
						log4j.debug("	List of zone (size = " + type.bodyFlow.listZoneFlow.size() + "):");
						
						int countZone = 0;
						for(ZoneFlow zone: type.bodyFlow.listZoneFlow)
						{
							countZone++;
							log4j.debug("	--> ZoneFlow #" + countZone + ": ");
							log4j.debug("		       ZoneId = " + zone.zoneId);
							log4j.debug("		    FlowSpeed = " + zone.flowSpeed);
							log4j.debug("		ZoneOccupancy = " + zone.zoneOccupancy);
						}
					}
				}
				else if(type.bodyIntegrated != null)
				{
					countType++;
					
					log4j.debug("--> MessageType #" + countType + " - IntegratedData: ");
					log4j.debug("	IntervalTime = " + type.bodyIntegrated.intervalTime);
					log4j.debug("	  DataNumber = " + type.bodyIntegrated.dataNumber);
					log4j.debug("	         UTC = " + type.bodyIntegrated.utc);
					log4j.debug("	Milliseconds = " + type.bodyIntegrated.milliseconds);
					
					if(type.bodyIntegrated.listZoneIntegrated != null)
					{
						log4j.debug("	List of zone (size = " + type.bodyIntegrated.listZoneIntegrated.size() + "):");
						
						int countZone = 0;
						for(ZoneIntegrated zone: type.bodyIntegrated.listZoneIntegrated)
						{
							countZone++;
							log4j.debug("	--> ZoneIntegrated #" + countZone + ": ");
							log4j.debug("		    ZoneId = " + zone.zoneId);
							log4j.debug("		 Occupancy = " + zone.occupancy);
							log4j.debug("		Confidence = " + zone.confidence);
							log4j.debug("		    Length = " + zone.length);
							log4j.debug("		   HeadWay = " + zone.headWay);
							log4j.debug("		   Density = " + zone.density);
							log4j.debug("		 HeadWaySq = " + zone.headWaySq);
							
							if(zone.listClassType != null)
							{
								int countClass = 0;
								for(ClassType _class: zone.listClassType)
								{
									countClass++;
									log4j.debug("		--> ClassType #" + countClass + ": ");
									log4j.debug("			  ClassNr = " + _class.classNr);
									log4j.debug("			   NumVeh = " + _class.numVeh);
									log4j.debug("			    Speed = " + _class.speed);
									log4j.debug("			  GapTime = " + _class.gapTime);
									log4j.debug("			  SpeedSq = " + _class.speedSq);
									log4j.debug("			GapTimeSq = " + _class.gapTimeSq);
								}
							}
						}
					}
				}
				
			}
		}
		
	}
}
