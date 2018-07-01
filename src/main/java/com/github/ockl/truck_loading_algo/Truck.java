package com.github.ockl.truck_loading_algo;

public class Truck {
	public Truck(String id, int capacity) {
		this.id = id;
		this.capacity = capacity;
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
	public int getCapacity() {
		return capacity;
	}

	private final String id;
	private final int capacity;
}
