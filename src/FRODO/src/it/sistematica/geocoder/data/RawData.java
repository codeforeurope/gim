package it.sistematica.geocoder.data;

import java.sql.Timestamp;

public class RawData
{
	private long id_pos;
	private Timestamp timestamp;
	private double lat;
	private double lon;
	private double alt;
	private int heading;
	private double speed;
	private double hdop;
	private String event;
	private double trackingDistance;
	private double globalDistance;
	private String trackingType;
	private String vehicleType;
	private String vehicleInformation;
	private int satInUse;
	private int satInView;
	private double distanceFromStart;
	
	// PARAMETRI AGGIUNTI PER OUTPUT
	private long edgeId;
	
	public RawData()
	{}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
	}

	public int getHeading() {
		return heading;
	}

	public void setHeading(int heading) {
		this.heading = heading;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getHdop() {
		return hdop;
	}

	public void setHdop(double hdop) {
		this.hdop = hdop;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public double getTrackingDistance() {
		return trackingDistance;
	}

	public void setTrackingDistance(double trackingDistance) {
		this.trackingDistance = trackingDistance;
	}

	public double getGlobalDistance() {
		return globalDistance;
	}

	public void setGlobalDistance(double globalDistance) {
		this.globalDistance = globalDistance;
	}

	public String getTrackingType() {
		return trackingType;
	}

	public void setTrackingType(String trackingType) {
		this.trackingType = trackingType;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getVehicleInformation() {
		return vehicleInformation;
	}

	public void setVehicleInformation(String vehicleInformation) {
		this.vehicleInformation = vehicleInformation;
	}

	public void setEdgeId(long edgeId) {
		this.edgeId = edgeId;
	}

	public long getEdgeId() {
		return edgeId;
	}

	public int getSatInUse() {
		return satInUse;
	}

	public void setSatInUse(int satInUse) {
		this.satInUse = satInUse;
	}

	public int getSatInView() {
		return satInView;
	}

	public void setSatInView(int satInView) {
		this.satInView = satInView;
	}

	public void setDistanceFromStart(double distanceFromStart) {
		this.distanceFromStart = distanceFromStart;
	}

	public double getDistanceFromStart() {
		return distanceFromStart;
	}

	public void setId_pos(long id_pos) {
		this.id_pos = id_pos;
	}

	public long getId_pos() {
		return id_pos;
	}
}
