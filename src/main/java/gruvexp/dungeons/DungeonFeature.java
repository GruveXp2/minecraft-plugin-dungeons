package gruvexp.dungeons;

import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.TextDisplay;
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

    private static String rotateNode(String dir, StructureRotation structureRotation) {
        dir = toAbsoluteDirection(dir);
        switch (structureRotation) {
            case NONE -> {
                return dir;
            }
            case CLOCKWISE_90 -> {
                return switch (dir) {
                    case "N" -> "E";
                    case "E" -> "S";
                    case "S" -> "W";
                    case "W" -> "N";
                    case "NS" -> "EW";
                    case "EW" -> "NS";
                    default -> "ERROR";
                };
            }
            case CLOCKWISE_180 -> {
                return switch (dir) {
                    case "N" -> "S";
                    case "E" -> "W";
                    case "S" -> "N";
                    case "W" -> "E";
                    case "NS", "EW" -> dir;
                    default -> "ERROR";
                };
            }
            case COUNTERCLOCKWISE_90 -> {
                return switch (dir) {
                    case "N" -> "W";
                    case "E" -> "N";
                    case "S" -> "E";
                    case "W" -> "S";
                    case "NS" -> "EW";
                    case "EW" -> "NS";
                    default -> "ERROR";
                };
            }
            default -> {
                return "ERROR DungeonFeature:69";
            }
        }
    }

    private static Location rotateLocation(Location loc, String dir) {
        switch (dir) {
            case "S" -> {
                loc.setX(loc.getBlockX());
                loc.setZ(loc.getBlockZ());
            }
            case "N" -> {
                loc.setX(-loc.getBlockX());
                loc.setZ(-loc.getBlockZ());
            }
            case "E" -> {
                double oldX = loc.getBlockX();
                loc.setX(loc.getBlockZ());
                loc.setZ(-oldX);
            }
            case "W" -> {
                double oldX = loc.getBlockX();
                loc.setX(-loc.getBlockZ());
                loc.setZ(oldX);
            }
        }
        return loc;
    }

    private static void moveForward(Location loc, Direction dir, int blocks) {
        switch (dir) {
            case S -> loc.add(0, 0, blocks);
            case W -> loc.add(-blocks, 0, 0);
            case N -> loc.add(0, 0, -blocks);
            case E -> loc.add(blocks, 0, 0);
        }
    }

    private static String toAbsoluteDirection(String relDir) {
        return switch (relDir) {
            case "Forward" -> "S";
            case "Right" -> "W";
            case "Left" -> "E";
            case "Backward" -> "N";
            case "FB" -> "NS";
            case "RL" -> "EW";
            default -> "ERROR DungeonFeature:113";
        };
    }

    private static void spawnTextMarker(Location loc, String name) {
        Location spawnLoc = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5);
        TextDisplay textDisplay = Main.WORLD.spawn(spawnLoc, TextDisplay.class);
        textDisplay.setText(name);
        textDisplay.setBillboard(TextDisplay.Billboard.VERTICAL);
        textDisplay.addScoreboardTag("textmarker");
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
        } else if (dir == Direction.NSEW) {
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
