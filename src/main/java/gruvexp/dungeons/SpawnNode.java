package gruvexp.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;

public class SpawnNode {
    Location location;
    Direction direction;
    int tries = 0;
    public SpawnNode(Location location, Direction direction) {
        this.location = location;
        this.direction = direction;
    }

    public void spawn(int spread) { // spread er hvor mye dungeonen sprer seg. 2= veien deler seg, 1=veien fortsetter, 0=blindvei
        ArrayList<DungeonStructure> rooms = DungeonManager.rooms.get(spread);
        int randomNumber = DungeonManager.RANDOM.nextInt(rooms.size());
        DungeonStructure dungeonStructure = rooms.get(randomNumber);
        if (dungeonStructure.availableSpace(location, direction)) {
            if (spread > 0 && tries < 5 && dungeonStructure.hasConflictingExits(location, direction)) {
                //Bukkit.broadcastMessage("Room " + randomNumber + " has conflixting exits, trying different room");
                tries++;
                if (tries < 6) {
                    spawn(spread);
                } else {
                    tries = 0;
                    spawn(spread - 1);
                }
                return;
            }
            //Bukkit.broadcastMessage(String.format("Spawning room %s:%s", spread, randomNumber));
            dungeonStructure.place(location, direction);
        } else {
            if (!DungeonManager.namedRooms.get("end").availableSpace(location, direction)) {
                //Bukkit.broadcastMessage("No space for a room to generate, spawning wall");
                switch (direction) {
                    case N, S -> direction = Direction.NS;
                    case E, W -> direction = Direction.EW;
                }
                DungeonManager.walls.put(location, new SpawnFeature(direction, location, DungeonManager.features.get("wall")));
                return;
            }
            if (tries > 10) {
                Bukkit.broadcastMessage(ChatColor.RED + "BUG! The code shouldnt reach this line");
                return;
            }
            //Bukkit.broadcastMessage("Not enough space, trying another room");
            tries++;
            spawn(spread);
        }
    }
}
