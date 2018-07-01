package com.github.ockl.truck_loading_algo;

public class Packet {
	public Packet(String id, int size, String station) {
		this.id = id;
		this.size = size;
		this.station = station;
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
	public String getStationID() {
		return station;
	}

	private final String id;
	private final int size;
	private final String station;
}
