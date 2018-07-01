package com.github.ockl.truck_loading_algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Algo {

	public static final String NotAssignedToTruck = "NotAssignedToTruck";

	private List<Truck> trucks;

	private class Cargo {
		String truckId;
		List<Packet> packets;
		int space;
	}

	private List<Cargo> cargo;

	private class PacketsByStation {
		String stationId;
		List<Packet> packets;
		int size;
	}

	private List<PacketsByStation> packets; // sorted by size

	public Algo(List<Truck> trucks, List<Packet> packets) {
		this.trucks = trucks;
		this.cargo = null;
		this.packets = new ArrayList<>();

		Map<String, List<Packet>> packetsByStation = filterByStation(packets);
		Map<String, Integer> sortedSizesByStation = getSortedSizeByStation(packetsByStation);
		for (String stationId : sortedSizesByStation.keySet()) {

			this.packets.add()
		}
	}



	/**
	 * Assign each packet to a truck such that
	 *   - the number of trucks going to the same discharge station is minimal
	 *   - the number of discharge stations a truck needs to visit is minimal
	 *
	 * @return            A mapping from truck ID to a list of packet IDs
	 *                      truck ID -> [ packet ID, packet ID, ...]
	 *                    If the packets require more space than is available,
	 *                    the list of unassigned packets can be found in
	 *                    mapping {@code NotAssignedToTruck}.
	 * @throws Exception
	 */
	public Map<String, List<Packet>> run() throws Exception {

		// verify that the overall size to be shipped to a discharge station does
		// not exceed the size of all trucks.
		if (stationSize.entrySet().iterator().next().getValue() > truckSizesSum) {
			String stationID = stationSize.entrySet().iterator().next().getKey();
			throw new Exception("size of packets for discharge station " + stationID
					+ " exceed available space in trucks");
		}



		return trucks;
	}

	private static Map<String, List<Packet>> filterByStation(List<Packet> packets) {
		Map<String, List<Packet>> ret = new HashMap<>();
		for (Packet p : packets) {
			String stationID = p.getDischargeStationID();
			List<Packet> list;
			if (ret.containsKey(stationID)) {
				list = ret.get(stationID);
			} else {
				list = new ArrayList<>();
				ret.put(stationID, list);
			}
			list.add(p);
		}
		return ret;
	}

	private static Map<String, Integer> getSortedSizeByStation(Map<String, List<Packet>> packetsByDischargeStation) {
		Map<String, Integer> tmp = new HashMap<>();
		for (Entry<String, List<Packet>> s : packetsByDischargeStation.entrySet()) {
			String dischargeStation = s.getKey();
			Integer sum = new Integer(0);
			for (Packet p : s.getValue()) {
				sum += p.getSize();
			}
			tmp.put(dischargeStation, sum);
		}
		List<Entry<String, Integer>> sortedList = new ArrayList<>(tmp.entrySet());
		sortedList.sort(Entry.comparingByValue());
		Map<String, Integer> ret = new LinkedHashMap<>();
		for (Entry<String, Integer> entry : sortedList) {
			ret.put(entry.getKey(), entry.getValue());
		}
		return ret;
	}

	private static Map<String, List<Packet>> initializeLoadingList(List<Truck> trucks) {
		Map<String, List<Packet>> ret = new HashMap<>();
		for (Truck t : trucks) {
			ret.put(t.getID(), new ArrayList<>());
		}
		ret.put(NotAssignedToTruck, new ArrayList<>());
		return ret;
	}

	private static Map<String, Integer> initializeTruckLoadingSize(List<Truck> trucks) {
		Map<String, Integer> ret = new HashMap<>();
		for (Truck t : trucks) {
			ret.put(t.getID(), t.getSize());
		}
		return ret;
	}
}
