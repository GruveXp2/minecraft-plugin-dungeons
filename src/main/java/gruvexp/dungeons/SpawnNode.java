package gruvexp.dungeons;

import gruvexp.dungeons.commands.DungeonCommand;
import gruvexp.dungeons.location.Direction;
import gruvexp.dungeons.location.RelativeDirection;
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
    public static boolean forceRoom = false;

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
        DungeonManager.moveForward(reservedLoc, dir, roomType.gridSize / 2 + 1);
        dungeon.reserveSpace(reservedLoc, dir.rotate(RelativeDirection.BACKWARD));
        dungeon.linkLocations.add(loc);
        changeActiveNodesCount(dungeon, true);
    }

    public void spawn(Dungeon dungeon) { // spread er hvor mye dungeonen sprer seg. 2= veien deler seg, 1=veien fortsetter, 0=blindvei
        GrowRate growRate = dungeon.getRandomExpansionRate(roomType);
        spawn(growRate, dungeon, bannedRooms);
        dirMarker.remove();
        typeMarker.remove();
    }

    public void place(Dungeon dungeon, Room room) { // forces that room to be placed
        room.structure().place(dungeon, location, direction);
        forceRoom = false;
        Bukkit.broadcast(Component.text("Room was placed by force", NamedTextColor.LIGHT_PURPLE));
    }

    private void spawn(GrowRate growRate, Dungeon dungeon, HashSet<Room> bannedRooms) {
        Location locPossibleConnection = location.clone();
        DungeonManager.moveForward(locPossibleConnection, direction, 1);
        if (dungeon.linkLocations.contains(locPossibleConnection)) {
            changeActiveNodesCount(dungeon, false);
            Bukkit.broadcast(Component.text("- Link", NamedTextColor.YELLOW));
            return; // hvis det er en annen spawnnode rett foran, altså at 2 stykker peker inn i hverandre
            // da er de allerede kobla sammen og trengs ikke å generere noe hvis det allerede er et rom der
        }

        StructurePool pool = dungeon.structurePools.get(roomType);
        Room randomRoom = pool.getRandomStructure(growRate, bannedRooms);
        if (randomRoom == null) {
            changeActiveNodesCount(dungeon, false);
            DungeonManager.spawnTextMarker(location, ChatColor.DARK_RED + "ALL CANDIDATES BANNED!", "all_banned");
            return;
        }
        if (DungeonCommand.forcedRoom != null) {
            randomRoom = DungeonCommand.forcedRoom;
            DungeonCommand.forcedRoom = null;
            Bukkit.broadcast(Component.text("overriding room to " + randomRoom.name(), NamedTextColor.LIGHT_PURPLE));
        }
        if (randomRoom == null) {
            Bukkit.broadcastMessage(ChatColor.RED + "Failed to spawn a room here, no space for it");
            return;
        }
        DungeonStructure dungeonStructure = randomRoom.structure();
        //Bukkit.broadcast(Component.text("Trying room " + dungeonStructure.name, NamedTextColor.GRAY));
        if (dungeonStructure.availableSpace(dungeon, location, direction)) {
            //Bukkit.broadcast(Component.text("space is available", NamedTextColor.GRAY));
            if (!dungeonStructure.hasConflictingExits(dungeon, location, direction)) {
                Bukkit.broadcastMessage(String.format("Spawning room %s:%s", growRate.name(), dungeonStructure.name));
                changeActiveNodesCount(dungeon, false);
                dungeon.roomCount  .put(roomType, dungeon.roomCount  .getOrDefault(roomType, 0) + 1);
                dungeonStructure.place(dungeon, location, direction);
                return;
            } else {
                //Bukkit.broadcastMessage(ChatColor.GRAY + "Room " + dungeonStructure.name + " has conflixting exits, trying different room");
            }
        }
        if (DungeonCommand.forcedRoom != null) {
            Bukkit.broadcastMessage(ChatColor.RED + "Failed to spawn that room, fix the code");
            return;
        }
        bannedRooms.add(randomRoom);
        spawn(growRate, dungeon, bannedRooms);
    }

    private void changeActiveNodesCount(Dungeon dungeon, boolean increase) { // activenodes += eller -= 1
        dungeon.activeNodes.put(roomType, dungeon.activeNodes.getOrDefault(roomType, 0) + (increase ? 1 : -1));
    }
}
