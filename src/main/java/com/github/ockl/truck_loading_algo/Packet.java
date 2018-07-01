package com.github.ockl.truck_loading_algo;

public class Packet {
	public Packet(String id, int size, String dischargeStation) {
		this.id = id;
		this.size = size;
		this.dischargeStation = dischargeStation;
	}

	/**
	 * @return  Unique identifier of this packet
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return  The size of this packet in cubic meters
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return  Unique identifier of the discharge station where this packet needs to go
	 */
	public String getDischargeStationID() {
		return dischargeStation;
	}

	private final String id;
	private final int size;
	private final String dischargeStation;
}
