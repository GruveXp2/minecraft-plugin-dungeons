package gruvexp.dungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StructurePool {

    private final Map<GrowRate, Map<Room, Integer>> structures = new HashMap<>();
    private final Map<GrowRate, Integer> structureWeights = new HashMap<>();

    public StructurePool() {
        structures.put(GrowRate.END, new HashMap<>());
        structures.put(GrowRate.STATIC, new HashMap<>());
        structures.put(GrowRate.EXPANDING, new HashMap<>());
        structureWeights.put(GrowRate.END, 0);
        structureWeights.put(GrowRate.STATIC, 0);
        structureWeights.put(GrowRate.EXPANDING, 0);
    }

    public void addStructure(Room room, int weight) {
        GrowRate growRate = room.growRate;
        structures.get(growRate).put(room, weight);
        structureWeights.put(growRate, structureWeights.get(growRate) + weight);
    }

    public void updateWeight(Room room, int newWeight) {
        GrowRate growRate = room.growRate;
        if (structures.get(growRate).containsKey(room)) {
            structureWeights.put(growRate, structureWeights.get(growRate) + structures.get(growRate).get(room));
            structures.get(growRate).put(room, newWeight);
            structureWeights.put(growRate, structureWeights.get(growRate) + newWeight);
        }
    }

    public Room getRandomStructure(GrowRate growRate) {
        //System.out.println("growrate is " + growRate.name());
        int roll = new Random().nextInt(structureWeights.get(growRate)); // random tall mellom 0 og totalWeight
        //System.out.println("total weight: " + structureWeights.get(growRate) + ", roll=" + roll);
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
