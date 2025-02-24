package gruvexp.dungeons;

import gruvexp.dungeons.location.Coord;
import gruvexp.dungeons.location.Direction;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;

import java.util.ArrayList;
import java.util.Set;

public class DungeonFeature {
    private final ArrayList<Structure> STRUCTURES = new ArrayList<>(3);
    private final Coord ENTRY;

    public DungeonFeature(Set<String> structureNames, Coord entry) { // structureNames: det skal være fler varianter av strukturen
        ENTRY = entry;
        for (String structureName : structureNames) {
            Structure structure = DungeonManager.STRUCTURE_MANAGER.loadStructure(new NamespacedKey(Main.getPlugin(), structureName));
            if (structure == null) {
                throw new NullPointerException("The structure \"" + structureName + "\" doesnt exist!");
            }
            STRUCTURES.add(structure);
        }
    }

    private void moveToOrigin(Location loc, Direction dir) {

        switch (dir) {
            case N -> loc.add(ENTRY.x, -ENTRY.y, ENTRY.z);
            case S -> loc.add(-ENTRY.x, -ENTRY.y, -ENTRY.z);
            case E -> loc.add(-ENTRY.z, -ENTRY.y, ENTRY.x);
            case W -> loc.add(ENTRY.z, -ENTRY.y, -ENTRY.x);
            default -> throw new IllegalArgumentException("Illegal direction!");
        }
    }

    public void place(Location loc, Direction dir, float integrity) { // location = der shulkeren ble plassert (absolutte kordinater)
        if (dir == Direction.NS) {
            dir = DungeonManager.RANDOM.nextInt(2) == 1 ? Direction.N : Direction.S;
        } else if (dir == Direction.EW) {
            dir = DungeonManager.RANDOM.nextInt(2) == 1 ? Direction.E : Direction.W;
        } else if (dir == Direction.ANY) {
            int randNum = DungeonManager.RANDOM.nextInt(4);
            dir = switch (randNum) {
                case 0 -> Direction.N;
                case 1 -> Direction.S;
                case 2 -> Direction.E;
                case 3 -> Direction.W;
                default -> throw new IllegalArgumentException("ERROR DungeonFeature:148");
            };
        }
        //Bukkit.broadcastMessage(String.format("Initial origin: %s, %s, %s", location.getX(), location.getY(), location.getZ()));
        StructureRotation structureRotation = switch (dir) {
            case N -> StructureRotation.CLOCKWISE_180;
            case S -> StructureRotation.NONE;
            case E -> StructureRotation.COUNTERCLOCKWISE_90;
            case W -> StructureRotation.CLOCKWISE_90;
            default -> throw new IllegalArgumentException("Illegal direction \"" + dir + "\"");
        };
        moveToOrigin(loc, dir);
        //Bukkit.broadcastMessage(String.format("Origin moved to: %s, %s, %s (%s)", location.getX(), location.getY(), location.getZ(), dir));
        STRUCTURES.get(DungeonManager.RANDOM.nextInt(STRUCTURES.size())).place(loc, false, structureRotation, Mirror.NONE, 0, integrity, DungeonManager.RANDOM);
        //Bukkit.broadcastMessage("Wall spawned at " + Utils.printLocation(loc));
    }
}
