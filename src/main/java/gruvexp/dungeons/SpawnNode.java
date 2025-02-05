package gruvexp.dungeons;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;

import java.util.HashSet;

public class SpawnNode {
    private final Location location;
    private final Direction direction;
    private final RoomType roomType;
    private final HashSet<Room> bannedRooms; // rommet som denne spawn noda kom fra
    private final TextDisplay dirMarker;
    private final TextDisplay typeMarker;

    public SpawnNode(Dungeon dungeon, Location loc, Direction dir, RoomType roomType, HashSet<Room> bannedRooms) {
        this.location = loc;
        this.direction = dir;
        this.roomType = roomType;
        this.bannedRooms = bannedRooms;
        String name = switch (dir) {
            case N -> ChatColor.BLUE + "North";
            case S -> ChatColor.RED + "South";
            case E -> ChatColor.GOLD + "East";
            case W -> ChatColor.AQUA + "West";
            default -> ChatColor.DARK_RED + "Error";
        };
        this.dirMarker = DungeonManager.spawnTextMarker(loc.clone().add(0, 0.5, 0), name, "dungeon_spawn_node");
        this.typeMarker = DungeonManager.spawnTextMarker(loc.clone().add(0, 0.25, 0), ChatColor.GRAY + roomType.name(), "dungeon_spawn_node");
        Location reservedLoc = loc.clone();
        DungeonStructure.moveForward(reservedLoc, dir, roomType.gridSize / 2 + 1);
        dungeon.reserveSpace(reservedLoc, dir.rotate(RelativeDirection.BACKWARD));
    }

    public void spawn(Dungeon dungeon) { // spread er hvor mye dungeonen sprer seg. 2= veien deler seg, 1=veien fortsetter, 0=blindvei
        GrowRate growRate = dungeon.getRandomExpansionRate();
        spawn(growRate, dungeon, bannedRooms);
        dirMarker.remove();
        typeMarker.remove();
    }

    private void spawn(GrowRate growRate, Dungeon dungeon, HashSet<Room> bannedRooms) {
        StructurePool pool = dungeon.structurePools.get(roomType);
        Room randomRoom = pool.getRandomStructure(growRate, bannedRooms);
        if (randomRoom == null) {
            Bukkit.broadcastMessage(ChatColor.RED + "Failed to spawn a room here, no space for it");
            return;
        }
        DungeonStructure dungeonStructure = randomRoom.structure();
        Bukkit.broadcast(Component.text("Trying room " + dungeonStructure.name, NamedTextColor.GRAY));
        if (dungeonStructure.availableSpace(dungeon, location, direction)) {
            Bukkit.broadcast(Component.text("space is available", NamedTextColor.GRAY));
            if (!dungeonStructure.hasConflictingExits(dungeon, location, direction)) {
                Bukkit.broadcastMessage(String.format("Spawning room %s:%s", growRate.name(), dungeonStructure.name));
                dungeonStructure.place(dungeon, location, direction);
                return;
            } else {
                Bukkit.broadcastMessage(ChatColor.GRAY + "Room " + dungeonStructure.name + " has conflixting exits, trying different room");
            }
        }
        bannedRooms.add(randomRoom);
        spawn(growRate, dungeon, bannedRooms);
    }
}
