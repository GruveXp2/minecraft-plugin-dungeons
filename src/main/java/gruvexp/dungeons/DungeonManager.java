package gruvexp.dungeons;

import org.bukkit.Location;
import org.bukkit.structure.StructureManager;

import java.util.*;

public final class DungeonManager {

    public static final StructureManager STRUCTURE_MANAGER = Main.getPlugin().getServer().getStructureManager();
    public static final Random RANDOM = new Random();
    public static final ArrayList<Room> exits = new ArrayList<>(); // blindvei
    public static final ArrayList<Room> hallways = new ArrayList<>(); // kun 1 utgang
    public static final ArrayList<Room> splits = new ArrayList<>(); // splittes i fler retninger
    public static final ArrayList<ArrayList<Room>> rooms = new ArrayList<>(); // liste med rom. index 0 er blindveier, 1 er ganger og 2 er rom som deler seg
    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne
    // liste med SpawnNodes (en spawnnode er en posisjon og retning som det kan spånes et rom fra)
    public static final HashSet<Location> usedSpaces = new HashSet<>();
    public static final NetherFortress fortress = new NetherFortress();


    public static void init() {
        exits.add(Room.END);
        hallways.addAll(List.of(Room.STRAIGHT, Room.SKELETON_TRAP, Room.ARROW_TRAP, Room.TURN_R, Room.TURN_L));
        splits.addAll(List.of(Room.T_SPLIT_R, Room.T_SPLIT_M, Room.T_SPLIT_L));
        rooms.add(exits);
        rooms.add(hallways);
        rooms.add(splits);
    }

    public static void loadStructure(String structureName, Location location, Direction direction) {
        Room room = Room.valueOf(structureName.toUpperCase());
        /*if (room == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + structureName + "\" fins ikke!");
        }*/
        DungeonStructure dungeonStructure = room.structure();
        dungeonStructure.place(location, direction);
    }
}
