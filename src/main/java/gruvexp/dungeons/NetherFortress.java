package gruvexp.dungeons;

import org.bukkit.Location;

import java.util.*;

public class NetherFortress extends Dungeon {

    private static final StructurePool bridges = new StructurePool();
    public static final StructurePool exits = new StructurePool(); // blindvei
    public static final StructurePool hallways = new StructurePool(); // kun 1 utgang
    public static final StructurePool splits = new StructurePool(); // splittes i fler retninger
    public static final ArrayList<ArrayList<Room>> rooms = new ArrayList<>(); // liste med rom. index 0 er blindveier, 1 er ganger og 2 er rom som deler seg
    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne

    public NetherFortress() {
        StructurePool bridges = new StructurePool();
        bridges.addStructure(Room.FORTRESS_BRIDGE_STRAIGHT, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_STRAIGHT_POLE, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_END, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_R, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_L, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_INTER_OPEN, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_INTER_CLOSED, 1);
        bridges.addStructure(Room.FORTRESS_BRIDGE_BLAZE, 1);
        structurePools.put(RoomType.FORTRESS_BRIDGE, bridges);
    }
}
