package gruvexp.dungeons;

import gruvexp.dungeons.room.Room;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StructurePool {

    private final Map<GrowRate, Map<Room, Integer>> structuresMap = new HashMap<>();
    private final Map<Room, Integer> structures = new HashMap<>();
    private final Map<GrowRate, Integer> structureWeights = new HashMap<>();

    public int getTotalStructures() {
        return totalStructures;
    }

    private int totalStructures = 0;


    public StructurePool() {
        structuresMap.put(GrowRate.END, new HashMap<>());
        structuresMap.put(GrowRate.STATIC, new HashMap<>());
        structuresMap.put(GrowRate.EXPANDING, new HashMap<>());
        structureWeights.put(GrowRate.END, 0);
        structureWeights.put(GrowRate.STATIC, 0);
        structureWeights.put(GrowRate.EXPANDING, 0);
    }

    public void addStructure(Room room, int weight) {
        GrowRate growRate = room.growRate;
        structuresMap.get(growRate).put(room, weight);
        structures.put(room, weight);
        structureWeights.put(growRate, structureWeights.get(growRate) + weight);
        totalStructures++;
    }

    public void updateWeight(Room room, int newWeight) {
        GrowRate growRate = room.growRate;
        if (structuresMap.get(growRate).containsKey(room)) {
            structureWeights.put(growRate, structureWeights.get(growRate) + structuresMap.get(growRate).get(room));
            structuresMap.get(growRate).put(room, newWeight);
            structures.put(room, newWeight);
            structureWeights.put(growRate, structureWeights.get(growRate) + newWeight);
        }
    }

    public Room getRandomStructure(GrowRate growRate, HashSet<Room> bannedRooms) {
        // velger et random rom. growRate er gruppa man vil selecte rom fra, men det er mulig at hvilket som helst rom kan bli selecta
        // men rom fra growRate har 10x sjangs for å bli valgt
        // bannedRooms er rom som ikke kan bli brukt enten fordi de ikke passer med det forrige rommet eller hvis de ikke har plass/lov til å spawne
        int totalWeight = structureWeights.values().stream().mapToInt(Integer::intValue).sum();
        totalWeight += structureWeights.get(growRate) * 999; // den ene har 1000x sjangs
        for (Room bannedRoom : bannedRooms) {
            totalWeight -= structures.get(bannedRoom) * (bannedRoom.growRate == growRate ? 1000 : 1);
        }
        if (totalWeight <= 0) {
            Bukkit.broadcast(Component.text("Error! All rooms are banned, idk what rooms to spawn! Total weight = " + totalWeight));
            return null;
        }
        int roll = DungeonManager.RANDOM.nextInt(totalWeight);
        int currentWeight = 0;
        for (Map.Entry<Room, Integer> entry : structures.entrySet()) {
            Room room = entry.getKey();
            if (bannedRooms.contains(room)) continue;
            currentWeight += entry.getValue() * (room.growRate == growRate ? 1000 : 1);;
            if (roll < currentWeight) { // hvis man setter alle rommene og weightsene etterhverandre, så velges den strukturen som er roll avstand fra start
                return room;
            }
        }
        return null;
    }
}
