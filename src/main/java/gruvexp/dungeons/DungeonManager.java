package gruvexp.dungeons;

import gruvexp.dungeons.dungeon.NetherFortress;
import gruvexp.dungeons.location.Direction;
import gruvexp.dungeons.room.RoomStructure;
import gruvexp.dungeons.room.Room;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.structure.StructureManager;

import java.util.*;

public final class DungeonManager {

    public static final StructureManager STRUCTURE_MANAGER = Main.getPlugin().getServer().getStructureManager();
    public static final Random RANDOM = new Random();
    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne
    public static NetherFortress fortress = new NetherFortress();


    public static void init() {
    }

    public static void loadStructure(String structureName, Location location, Direction direction) {
        Room room = Room.valueOf(structureName.toUpperCase());
        /*if (room == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + structureName + "\" fins ikke!");
        }*/
        RoomStructure roomStructure = room.structure();
        roomStructure.place(fortress, location, direction);
    }

    public static TextDisplay spawnTextMarker(Location loc, String name, String tag) {
        Location spawnLoc = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5);
        TextDisplay textDisplay = Main.WORLD.spawn(spawnLoc, TextDisplay.class);
        textDisplay.setText(name);
        textDisplay.setBillboard(TextDisplay.Billboard.VERTICAL);
        textDisplay.addScoreboardTag(tag);
        return textDisplay;
    }

    public static void moveForward(Location loc, Direction dir, int blocks) {
        switch (dir) {
            case S, NS -> loc.add(0, 0, blocks);
            case W -> loc.add(-blocks, 0, 0);
            case N -> loc.add(0, 0, -blocks);
            case E, EW -> loc.add(blocks, 0, 0);
            default -> throw new IllegalArgumentException("Illegal direction: " + dir + " (Dungeonstructure:118)");
        }
    }

    public static Location rotateLocation(Location loc, Direction dir) {
        switch (dir) {
            case S -> {
                loc.setX(loc.getBlockX());
                loc.setZ(loc.getBlockZ());
            }
            case N -> {
                loc.setX(-loc.getBlockX());
                loc.setZ(-loc.getBlockZ());
            }
            case E -> {
                double oldX = loc.getBlockX();
                loc.setX(loc.getBlockZ());
                loc.setZ(-oldX);
            }
            case W -> {
                double oldX = loc.getBlockX();
                loc.setX(-loc.getBlockZ());
                loc.setZ(oldX);
            }
        }
        return loc;
    }
}
