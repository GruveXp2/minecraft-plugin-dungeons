package gruvexp.dungeons;

import org.bukkit.Location;

import java.util.*;

import static gruvexp.dungeons.DungeonManager.RANDOM;

public class NetherFortress extends Dungeon {

    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne

    public NetherFortress() {
        super(Room.FORTRESS_LAVAROOM);
        StructurePool bridges = new StructurePool();
        bridges.addStructure(Room.FORTRESS_BRIDGE_BOW_F, 16);
        bridges.addStructure(Room.FORTRESS_BRIDGE_F, 16);
        bridges.addStructure(Room.FORTRESS_BRIDGE_R, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_L, 1);

        bridges.addStructure(Room.FORTRESS_BRIDGE_BOW_X, 3);
        bridges.addStructure(Room.FORTRESS_BRIDGE_X, 1);

        bridges.addStructure(Room.FORTRESS_BRIDGE_END, 3);
        bridges.addStructure(Room.FORTRESS_BRIDGE_BLAZE, 1);

        StructurePool corridors = new StructurePool();
        corridors.addStructure(Room.FORTRESS_CORRIDOR_F, 5);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_R, 1);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_L, 1);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_STAIRS, 1);

        corridors.addStructure(Room.FORTRESS_CORRIDOR_X, 3);

        corridors.addStructure(Room.FORTRESS_CORRIDOR_END_BROKEN, 10);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_END_LAVA, 1);


        structurePools.put(RoomType.FORTRESS_BRIDGE, bridges);
        structurePools.put(RoomType.FORTRESS_CORRIDOR, corridors);
    }

    @Override
    public GrowRate getRandomExpansionRate(RoomType roomType) {
        int roomTypeCount = roomCount.getOrDefault(roomType, 0);
        int activeNodesCount = activeNodes.getOrDefault(roomType, 0);
        switch (roomType) {
            case FORTRESS_BRIDGE -> {
                if (roomTypeCount < 12) {
                    return GrowRate.getWeighted(RANDOM, 0, 2, 1);
                } else if (roomTypeCount < 32) {
                    if (activeNodesCount < 15) {
                        return GrowRate.EXPANDING; // hvis det er for lite noder så ekspanderer vi
                    } else if (activeNodesCount > 25) {
                        return GrowRate.END; // hvis det er for mange noder så reduserer vi
                    } else {
                        return GrowRate.getWeighted(RANDOM, 1, 3, 1);
                    }
                } else {
                    return GrowRate.getWeighted(RANDOM, 2, 20, 1);
                }
            }
            case FORTRESS_CORRIDOR -> {
                if (roomTypeCount < 15) {
                    return GrowRate.getWeighted(RANDOM, 0, 2, 1);
                } else if (roomTypeCount < 50) {
                    if (activeNodesCount < 15) {
                        return GrowRate.EXPANDING; // hvis det er for lite noder så ekspanderer vi
                    } else if (activeNodesCount > 25) {
                        return GrowRate.END; // hvis det er for mange noder så reduserer vi
                    } else {
                        return GrowRate.getWeighted(RANDOM, 1, 3, 1);
                    }
                } else {
                    return GrowRate.getWeighted(RANDOM, 3, 10, 1);
                }
            }
        }
        return GrowRate.END;
    }
}
