package gpsdatareceiver;

import java.util.HashMap;

public class SegmentManager
{
	private HashMap<String, Long> segment_map;
	
	private static SegmentManager instance;

	private SegmentManager()
	{
		segment_map = new HashMap<String, Long>();
	}
	
	public static SegmentManager getInstance()
	{
		if(instance == null)
			instance = new SegmentManager();
		
		return instance;
	}
	
	public void addSegment(String user, Long segmentId)
	{
		segment_map.put(user, segmentId);
	}
	
	public Long getSegment(String user)
	{
		return segment_map.get(user);
	}
}
