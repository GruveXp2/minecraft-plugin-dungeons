package gruvexp.dungeons.dungeon;

import gruvexp.dungeons.GrowRate;
import gruvexp.dungeons.SpawnFeature;
import gruvexp.dungeons.StructurePool;
import gruvexp.dungeons.room.Room;
import gruvexp.dungeons.room.RoomType;
import org.bukkit.Location;

import java.util.*;

import static gruvexp.dungeons.DungeonManager.RANDOM;

public class NetherFortress extends Dungeon {

    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne

    public NetherFortress() {
        super(Room.FORTRESS_LAVAROOM);
        StructurePool bridges = new StructurePool();
        bridges.addStructure(Room.FORTRESS_BRIDGE_BOW_F,   160);
        bridges.addStructure(Room.FORTRESS_BRIDGE_PILLAR_F,160);
        bridges.addStructure(Room.FORTRESS_BRIDGE_TOWER_R, 10);
        bridges.addStructure(Room.FORTRESS_BRIDGE_TOWER_L, 10);
        bridges.addStructure(Room.FORTRESS_BRIDGE_TOWER_F, 1);

        bridges.addStructure(Room.FORTRESS_BRIDGE_BOW_X,  3);
        bridges.addStructure(Room.FORTRESS_BRIDGE_TOWER_X,1);

        bridges.addStructure(Room.FORTRESS_BRIDGE_BOW_END,   10);
        bridges.addStructure(Room.FORTRESS_BRIDGE_PILLAR_END,3);
        bridges.addStructure(Room.FORTRESS_BRIDGE_BLAZE,     1);

        StructurePool corridors = new StructurePool();
        corridors.addStructure(Room.FORTRESS_CORRIDOR_F, 5);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_R, 1);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_L, 1);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_STAIRS,1);

        corridors.addStructure(Room.FORTRESS_CORRIDOR_X,   6);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_T,   3);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_WART,2);

        corridors.addStructure(Room.FORTRESS_CORRIDOR_END_BROKEN,20);
        corridors.addStructure(Room.FORTRESS_CORRIDOR_END_LAVA,  1);


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
                    if (activeNodesCount < 5) {
                        return GrowRate.EXPANDING; // hvis det er for lite noder så ekspanderer vi
                    } else if (activeNodesCount > 12) {
                        return GrowRate.getWeighted(RANDOM, 1, 1, 0);
                    } else {
                        return GrowRate.getWeighted(RANDOM, 1, 3, 1);
                    }
                } else {
                    if (activeNodesCount > 5) {
                        return GrowRate.getWeighted(RANDOM, 5, 1, 0);
                    }
                    return GrowRate.getWeighted(RANDOM, 10, 50, 1);
                }
            }
            case FORTRESS_CORRIDOR -> {
                if (roomTypeCount < 15) {
                    return GrowRate.getWeighted(RANDOM, 0, 2, 1);
                } else if (roomTypeCount < 50) {
                    if (activeNodesCount < 5) {
                        return GrowRate.EXPANDING; // hvis det er for lite noder så ekspanderer vi
                    } else if (activeNodesCount > 10) {
                        return GrowRate.getWeighted(RANDOM, 1, 2, 0);
                    } else {
                        return GrowRate.getWeighted(RANDOM, 1, 3, 1);
                    }
                }  else if (roomTypeCount < 75) {
                    if (activeNodesCount < 3) {
                        return GrowRate.EXPANDING; // hvis det er for lite noder så ekspanderer vi
                    } else if (activeNodesCount > 5) {
                        return GrowRate.getWeighted(RANDOM, 1, 2, 0);
                    } else {
                        return GrowRate.getWeighted(RANDOM, 1, 3, 1);
                    }
                } else {
                    if (activeNodesCount > 5) {
                        return GrowRate.getWeighted(RANDOM, 5, 1, 0);
                    }
                    return GrowRate.getWeighted(RANDOM, 3, 10, 1);
                }
            }
            default -> {
                return GrowRate.END;
            }
        }
    }
}
