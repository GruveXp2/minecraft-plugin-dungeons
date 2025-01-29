package gruvexp.dungeons;

import org.bukkit.Location;

import java.util.*;

public class NetherFortress extends Dungeon {

    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne

    public NetherFortress() {
        StructurePool bridges = new StructurePool();
        bridges.addStructure(Room.FORTRESS_BRIDGE_BOW_F, 4);
        bridges.addStructure(Room.FORTRESS_BRIDGE_F, 4);
        bridges.addStructure(Room.FORTRESS_BRIDGE_END, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_R, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_L, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_BOW_INTER, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_INTER, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_BLAZE, 1);
        structurePools.put(RoomType.FORTRESS_BRIDGE, bridges);
    }
}
