package com.github.ockl.truck_loading_algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Algo {

	public static final String NotAssignedToTruck = "NotAssignedToTruck";

	private List<Truck> trucks;

	private static class Cargo {
		Cargo(String truckId, int space) {
			this.truckId = truckId;
			packets = new ArrayList<>();
			curSpace = 0;
			maxSpace = space;
			dischargeStations = new HashSet<>();
		}
		String truckId;
		List<Packet> packets;
		int curSpace;
		int maxSpace;
		Set<String> dischargeStations;
	}

	private List<Cargo> cargoList;
	private int cargoRemainingSpace;

	private static class PacketsByStation {
		PacketsByStation(String stationId, List<Packet> packets, int size) {
			this.stationId = stationId;
			this.packets = packets;
			this.size = size;
		}
		String stationId;
		List<Packet> packets; // sorted by size
		int size;
	}

	private List<PacketsByStation> packetsList; // sorted by size

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
	public Map<String, List<Packet>> run(List<Truck> trucks, List<Packet> packets) throws Exception {
		this.trucks = trucks;
		cargoList = initializeCargoList(trucks);
		cargoRemainingSpace = initializeCargoRemainingSpace(trucks);
		packetsList = initializePacketsList(packets);
		return run2();
	}

	private Map<String, List<Packet>> run2() {
		List<PacketsByStation> unassigned = new ArrayList<>();
		while (!packetsList.isEmpty()) {
			PacketsByStation packets = packetsList.get(0);
			if (packets.size > cargoRemainingSpace) {
				unassigned.add(packets);
				packetsList.remove(0);
				continue;
			}
			int requiredSize = packets.size;
			while (requiredSize > 0) {
				Cargo cargo = getSuitableTruck(packets.size);
				if (cargo.maxSpace - cargo.curSpace >= requiredSize) {
					// if everything fits, put it all in
					cargo.packets.addAll(packets.packets);
					cargo.curSpace += requiredSize;
					cargoRemainingSpace -= requiredSize;
					requiredSize = 0;
				} else {
					// otherwise, add as much as possible
					while (!packets.packets.isEmpty()) {
						Packet currentPacket = packets.packets.get(0);
						if (cargo.curSpace + currentPacket.getSize() > cargo.maxSpace) {
							break;
						}
						cargo.packets.add(currentPacket);
						cargo.curSpace += currentPacket.getSize();
						requiredSize -= currentPacket.getSize();
						cargoRemainingSpace -= currentPacket.getSize();
						packets.packets.remove(currentPacket);
					}
				}
				cargo.dischargeStations.add(packets.stationId);
			}
			packetsList.remove(0);
		}
		Map<String, List<Packet>> ret = initializeLoadingList(trucks);
		for (Cargo cargo : cargoList) {
			ret.get(cargo.truckId).addAll(cargo.packets);
		}
		for (PacketsByStation p : unassigned) {
			ret.get(NotAssignedToTruck).addAll(p.packets);
		}
		return ret;
	}

	private Cargo getSuitableTruck(int requiredSize) {
		// pick a truck with minimum discharge stations and where it fits completely
		Cargo cargoMin = null;
		int stationsMin = Integer.MAX_VALUE;
		for (Cargo cargo : cargoList) {
			if (cargo.maxSpace - cargo.curSpace >= requiredSize) {
				if (cargo.dischargeStations.size() < stationsMin) {
					stationsMin = cargo.dischargeStations.size();
					cargoMin = cargo;
				}
			}
		}
		if (cargoMin != null) {
			return cargoMin;
		}
		// otherwise simply pick the one with the minimum discharge stations
		for (Cargo cargo : cargoList) {
			if (cargo.dischargeStations.size() < stationsMin) {
				stationsMin = cargo.dischargeStations.size();
				cargoMin = cargo;
			}
		}
		return cargoMin;
	}

	private static List<Cargo> initializeCargoList(List<Truck> trucks) {
		List<Cargo> ret = new ArrayList<>();
		for (Truck t : trucks) {
			ret.add(new Cargo(t.getID(), t.getCapacity()));
		}
		return ret;
	}

	private static int initializeCargoRemainingSpace(List<Truck> trucks) {
		int ret = 0;
		for (Truck t : trucks) {
			ret += t.getCapacity();
		}
		return ret;
	}

	private static List<PacketsByStation> initializePacketsList(List<Packet> packets) {
		List<PacketsByStation> ret = new ArrayList<>();
		Map<String, List<Packet>> packetsByStation = filterByStation(packets);
		Map<String, Integer> sortedSizesByStation = getSortedSizeByStation(packetsByStation);
		for (String stationId : sortedSizesByStation.keySet()) {
			ret.add(new PacketsByStation(
					stationId,
					packetsByStation.get(stationId),
					sortedSizesByStation.get(stationId)));
		}
		return ret;
	}

	private static Map<String, List<Packet>> filterByStation(List<Packet> packets) {
		Map<String, List<Packet>> ret = new HashMap<>();
		for (Packet p : packets) {
			String stationID = p.getStationID();
			List<Packet> list;
			if (ret.containsKey(stationID)) {
				list = ret.get(stationID);
			} else {
				list = new ArrayList<>();
				ret.put(stationID, list);
			}
			list.add(p);
		}
		for (Entry<String, List<Packet>> packetsByStation : ret.entrySet()) {
			packetsByStation.getValue().sort(Comparator.comparing(a -> a.getSize()));
			Collections.reverse(packetsByStation.getValue());
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
}
