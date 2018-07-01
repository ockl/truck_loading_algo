package com.github.ockl.truck_loading_algo;

public class Truck {
	public Truck(String id, int size) {
		this.id = id;
		this.size = size;
	}

	/**
	 * @return  Unique identifier of this truck
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return  The loading capacity of this truck in cubic meters
	 */
	public int getSize() {
		return size;
	}

	private final String id;
	private final int size;
}
