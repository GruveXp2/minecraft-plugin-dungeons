package gruvexp.dungeons;

import org.bukkit.Location;
import org.bukkit.structure.StructureManager;

import java.util.*;

public final class DungeonManager {

    public static final StructureManager STRUCTURE_MANAGER = Main.getPlugin().getServer().getStructureManager();
    public static final Random RANDOM = new Random();
    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne
    public static final NetherFortress fortress = new NetherFortress();


    public static void init() {
    }

    public static void loadStructure(String structureName, Location location, Direction direction) {
        Room room = Room.valueOf(structureName.toUpperCase());
        /*if (room == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + structureName + "\" fins ikke!");
        }*/
        DungeonStructure dungeonStructure = room.structure();
        dungeonStructure.place(fortress, location, direction);
    }
}
