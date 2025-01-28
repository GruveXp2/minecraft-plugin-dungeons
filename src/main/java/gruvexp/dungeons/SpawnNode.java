package gruvexp.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;

public class SpawnNode {
    private final Location location;
    private Direction direction;
    private final RoomType roomType;
    int tries = 0;
    public SpawnNode(Location location, Direction direction, RoomType roomType) {
        this.location = location;
        this.direction = direction;
        this.roomType = roomType;
    }

    public void spawn(Dungeon dungeon) { // spread er hvor mye dungeonen sprer seg. 2= veien deler seg, 1=veien fortsetter, 0=blindvei
        GrowRate growRate = dungeon.getRandomExpansionRate();
        spawn(growRate, dungeon);
    }

    private void spawn(GrowRate growRate, Dungeon dungeon) {

        StructurePool pool = dungeon.structurePools.get(roomType);
        DungeonStructure dungeonStructure = pool.getRandomStructure(growRate).structure();
        if (dungeonStructure.availableSpace(location, direction)) {
            if (growRate != GrowRate.SHRINKING && tries < 5 && dungeonStructure.hasConflictingExits(location, direction)) {
                //Bukkit.broadcastMessage("Room " + randomNumber + " has conflixting exits, trying different room");
                tries++;
                if (tries < 6) {
                    spawn(growRate, dungeon);
                } else {
                    tries = 0;
                    if (growRate == GrowRate.EXPANDING) {
                        spawn(GrowRate.STATIC, dungeon);
                    } else if (growRate == GrowRate.STATIC) {
                        spawn(GrowRate.SHRINKING, dungeon);
                    }
                }
                return;
            }
            //Bukkit.broadcastMessage(String.format("Spawning room %s:%s", spread, randomNumber));
            dungeonStructure.place(dungeon, location, direction);
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
            spawn(growRate, dungeon);
        }
    }
}
