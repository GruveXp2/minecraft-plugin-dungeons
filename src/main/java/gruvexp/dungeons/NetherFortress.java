package gruvexp.dungeons;

import org.bukkit.Location;

import java.util.*;

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

        corridors.addStructure(Room.FORTRESS_CORRIDOR_X, 3);


        structurePools.put(RoomType.FORTRESS_BRIDGE, bridges);
        structurePools.put(RoomType.FORTRESS_CORRIDOR, corridors);
    }
}
