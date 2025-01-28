package gruvexp.dungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StructurePool {

    private final Map<GrowRate, Map<Room, Integer>> structures = new HashMap<>();
    private int totalWeight = 0;

    public void addStructure(Room room, int weight) {
        structures.get(room.growRate).put(room, weight);
        totalWeight += weight;
    }

    public void updateWeight(Room room, int newWeight) {
        if (structures.containsKey(room)) {
            totalWeight -= structures.get(room.growRate).get(room);
            structures.get(room.growRate).put(room, newWeight);
            totalWeight += newWeight;
        }
    }

    public Room getRandomStructure(GrowRate growRate) {
        int roll = new Random().nextInt(totalWeight); // random tall mellom 0 og totalWeight
        int currentWeight = 0;

        for (Map.Entry<Room, Integer> entry : structures.get(growRate).entrySet()) {
            currentWeight += entry.getValue();
            if (roll < currentWeight) { // hvis man setter alle rommene og weightsene etterhverandre, så velges den strukturen som er roll avstand fra start
                return entry.getKey();
            }
        }
        return null;
    }
}
