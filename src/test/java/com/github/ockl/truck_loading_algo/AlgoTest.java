package com.github.ockl.truck_loading_algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class AlgoTest {

	Algo algo = new Algo();

	@Test
	public void noTrucksNoPackets() throws Exception {
		Map<String, List<Packet>> res = algo.run(
				new ArrayList<Truck>(),
				new ArrayList<Packet>());
		assertEquals(1, res.size());
		assertTrue(res.containsKey(Algo.NotAssignedToTruck));
		assertTrue(res.get(Algo.NotAssignedToTruck).isEmpty());
	}

	@Test
	public void oneTruckNoPackets() throws Exception {
		Map<String, List<Packet>> res = algo.run(
				createTrucks("T1", 10),
				new ArrayList<Packet>());
		assertEquals(2, res.size());
		assertTrue(res.containsKey(Algo.NotAssignedToTruck));
		assertTrue(res.get(Algo.NotAssignedToTruck).isEmpty());
		assertTrue(res.containsKey("T1"));
		assertTrue(res.get("T1").isEmpty());
	}

	@Test
	public void oneTruckOneSmallPacket() throws Exception {
		Map<String, List<Packet>> res = algo.run(
				createTrucks("T1", 10),
				createPackets("P1", 5, "S1"));
		assertEquals(2, res.size());
		assertTrue(res.containsKey(Algo.NotAssignedToTruck));
		assertTrue(res.get(Algo.NotAssignedToTruck).isEmpty());
		assertTrue(res.containsKey("T1"));
		assertEquals(1, res.get("T1").size());
		verifyPacket("P1", 5, "S1", res.get("T1").get(0));
	}

	@Test
	public void oneTruckOneLargePacket() throws Exception {
		Map<String, List<Packet>> res = algo.run(
				createTrucks("T1", 10),
				createPackets("P1", 15, "S1"));
		assertEquals(2, res.size());
		assertTrue(res.containsKey(Algo.NotAssignedToTruck));
		assertEquals(1, res.get(Algo.NotAssignedToTruck).size());
		verifyPacket("P1", 15, "S1", res.get(Algo.NotAssignedToTruck).get(0));
		assertTrue(res.containsKey("T1"));
		assertTrue(res.get("T1").isEmpty());
	}

	@Test
	public void oneTruckTwoSmallPackets() throws Exception {
		Map<String, List<Packet>> res = algo.run(
				createTrucks(
						"T1", 9),
				createPackets(
						"P1", 4, "S1",
						"P2", 5, "S1"));
		assertEquals(2, res.size());
		assertTrue(res.containsKey(Algo.NotAssignedToTruck));
		assertTrue(res.get(Algo.NotAssignedToTruck).isEmpty());
		assertTrue(res.containsKey("T1"));
		assertEquals(2, res.get("T1").size());
		verifyPacket("P2", 5, "S1", res.get("T1").get(0));
		verifyPacket("P1", 4, "S1", res.get("T1").get(1));
	}

	private void verifyPacket(String id, int size, String station, Packet packet) {
		assertEquals(id, packet.getID());
		assertEquals(size, packet.getSize());
		assertEquals(station, packet.getStationID());
	}

	private List<Truck> createTrucks(String id, int capacity) {
		List<Truck> ret = new ArrayList<>();
		ret.add(new Truck(id, capacity));
		return ret;
	}

	private List<Packet> createPackets(String id, int size, String station) {
		List<Packet> ret = new ArrayList<>();
		ret.add(new Packet(id, size, station));
		return ret;
	}

	private List<Packet> createPackets(String id1, int size1, String station1,
			String id2, int size2, String station2) {
		List<Packet> ret = new ArrayList<>();
		ret.add(new Packet(id1, size1, station1));
		ret.add(new Packet(id2, size2, station2));
		return ret;
	}
}
