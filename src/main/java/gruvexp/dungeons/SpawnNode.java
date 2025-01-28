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

    public void spawn(GrowRate growRate) { // spread er hvor mye dungeonen sprer seg. 2= veien deler seg, 1=veien fortsetter, 0=blindvei
        ArrayList<Room> rooms = DungeonManager.rooms.get(growRate.spread());
        int randomNumber = DungeonManager.RANDOM.nextInt(rooms.size());
        DungeonStructure dungeonStructure = rooms.get(randomNumber).structure();
        if (dungeonStructure.availableSpace(location, direction)) {
            if (growRate != GrowRate.SHRINKING && tries < 5 && dungeonStructure.hasConflictingExits(location, direction)) {
                //Bukkit.broadcastMessage("Room " + randomNumber + " has conflixting exits, trying different room");
                tries++;
                if (tries < 6) {
                    spawn(growRate);
                } else {
                    tries = 0;
                    if (growRate == GrowRate.EXPANDING) {
                        spawn(GrowRate.STATIC);
                    } else if (growRate == GrowRate.STATIC) {
                        spawn(GrowRate.SHRINKING);
                    }
                }
                return;
            }
            //Bukkit.broadcastMessage(String.format("Spawning room %s:%s", spread, randomNumber));
            dungeonStructure.place(location, direction);
        } else {
            if (!Room.END.structure().availableSpace(location, direction)) {
                //Bukkit.broadcastMessage("No space for a room to generate, spawning wall");
                switch (direction) {
                    case N, S -> direction = Direction.NS;
                    case E, W -> direction = Direction.EW;
                }
                DungeonManager.walls.put(location, new SpawnFeature(direction, location, Feature.WALL));
                return;
            }
            if (tries > 10) {
                Bukkit.broadcastMessage(ChatColor.RED + "BUG! The code shouldnt reach this line");
                return;
            }
            //Bukkit.broadcastMessage("Not enough space, trying another room");
            tries++;
            spawn(growRate);
        }
    }
}
