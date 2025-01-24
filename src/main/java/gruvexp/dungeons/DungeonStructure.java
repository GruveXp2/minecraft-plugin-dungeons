package gruvexp.dungeons;

import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.structure.Structure;

import java.util.HashMap;
import java.util.Objects;

public class DungeonStructure {
    private final Structure STRUCTURE;
    private final Structure STRUCTURE_FUN; //Fun
    private final Coord ENTRY;
    public final HashMap<Location, Direction> exitLocations = new HashMap<>(); // steder som denne strukturen fører til

    public DungeonStructure(String structureName, Coord entry) {
        STRUCTURE = DungeonManager.STRUCTURE_MANAGER.loadStructure(new NamespacedKey(Main.getPlugin(), structureName));
        STRUCTURE_FUN = DungeonManager.STRUCTURE_MANAGER.loadStructure(new NamespacedKey(Main.getPlugin(), "_" + structureName)); //Fun
        if (STRUCTURE == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + structureName + "\" fins ikke!");
        }
        ENTRY = entry;
        for (Entity e : STRUCTURE.getEntities()) {
            String name = ChatColor.stripColor(e.getName());
            switch (name) {
                case "Forward", "Right", "Left", "Backward" -> {
                    Location eLoc = e.getLocation();
                    eLoc.setX(eLoc.getBlockX());
                    eLoc.setZ(eLoc.getBlockZ());
                    Direction dir = Direction.fromString(name);
                    moveForward(eLoc, toAbsoluteDirection(dir), 3);
                    exitLocations.put(eLoc, dir);
                }
            }
        }
    }

    private static Direction rotateNode(Direction dir, StructureRotation structureRotation) {
        dir = toAbsoluteDirection(dir);
        switch (structureRotation) {
            case NONE -> {
                return dir;
            }
            case CLOCKWISE_90 -> {
                return switch (dir) {
                    case N -> Direction.E;
                    case E -> Direction.S;
                    case S -> Direction.W;
                    case W -> Direction.N;
                    case NS -> Direction.EW;
                    case EW -> Direction.NS;
                    default -> throw new IllegalArgumentException("Illegal direction: \"" + dir + "\" (DungeonStructure:58)");
                };
            }
            case CLOCKWISE_180 -> {
                return switch (dir) {
                    case N -> Direction.S;
                    case E -> Direction.W;
                    case S -> Direction.N;
                    case W -> Direction.E;
                    case NS, EW -> dir;
                    default -> throw new IllegalArgumentException("Illegal direction: \"" + dir + "\" (DungeonStructure:68)");
                };
            }
            case COUNTERCLOCKWISE_90 -> {
                return switch (dir) {
                    case N -> Direction.W;
                    case E -> Direction.N;
                    case S -> Direction.E;
                    case W -> Direction.S;
                    case NS -> Direction.EW;
                    case EW -> Direction.NS;
                    default -> throw new IllegalArgumentException("Illegal direction: \"" + dir + "\" (DungeonStructure:79)");
                };
            }
            default -> throw new IllegalArgumentException("ERROR DungeonStructure:83");
        }
    }

    private static Location rotateLocation(Location loc, Direction dir) {
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

    public static Location moveForward(Location loc, Direction dir, int blocks) {
        switch (dir) {
            case S, NS -> loc.add(0, 0, blocks);
            case W -> loc.add(-blocks, 0, 0);
            case N -> loc.add(0, 0, -blocks);
            case E, EW -> loc.add(blocks, 0, 0);
            default -> throw new IllegalArgumentException("Illegal direction: " + dir + " (Dungeonstructure:118)");
        }
        return loc;
    }

    private static Direction toAbsoluteDirection(Direction relDir) {
        return switch (relDir) {
            case FORWARD -> Direction.S;
            case RIGHT -> Direction.W;
            case LEFT -> Direction.E;
            case BACKWARD -> Direction.N;
            case FB -> Direction.NS;
            case RL -> Direction.EW;
            default -> throw new IllegalArgumentException("Illegal direction: \"" + relDir + "\" (DungeonStructure:129)");
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
            default -> throw new IllegalArgumentException("Illegal direction: \"" + dir + "\" (DungeonStructure:148)");
        }
    }

    public boolean hasConflictingExits(Location loc, Direction dir) { // input lokasjon: i endepunktet på det forrige rommet. input retning: retninga til exiten i det forrige rommet.
        for (Location exitLoc : exitLocations.keySet()) { // i framtida add sånn at det fins unntak, feks i t kryss så kan den ene veggen bli sett på som inngang, sånn at et rom kan spawne der uten problemer, men blir en vegg hvis ingen rom spawner.
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Raw: " + Utils.printLocation(exitLoc));
            Location rotatedExitSpace = rotateLocation(exitLoc.clone(), dir);
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Rotated: " + Utils.printLocation(rotatedExitSpace));
            rotatedExitSpace.add(loc);
            moveForward(rotatedExitSpace, dir, 1); // flytter 1 blocc fram akkuratt som når man skal spawne inn så flytter man 1 fram fra exit pktet i det forrige rommet til entry pktet i dette rommet.
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Inworld: " + Utils.printLocation(rotatedExitSpace));
            moveToOrigin(rotatedExitSpace, dir);
            rotatedExitSpace.setX(rotatedExitSpace.getBlockX());
            rotatedExitSpace.setZ(rotatedExitSpace.getBlockZ());
            rotatedExitSpace.setYaw(0);
            rotatedExitSpace.setPitch(0);
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Moved: " + Utils.printLocation(rotatedExitSpace));
            if (DungeonManager.usedSpaces.contains(rotatedExitSpace)) {
                return true;
            }
            //Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + Utils.printLocation(rotatedExitSpace) + " is free, spawing in structure");
            //spawnTextMarker(rotatedExitSpace, ChatColor.LIGHT_PURPLE + "free space");
        }
        return false;
    }

    public boolean availableSpace(Location loc, Direction dir) { // sjekker om det er plass til å spawne denne structuren eller om det er spaces som er opptatt av et annet rom i dungenen. input lokasjon og retning: exitpkt i forrige rom.
        for (Entity e : STRUCTURE.getEntities()) {
            if (!ChatColor.stripColor(e.getName()).equals("Space")) {continue;}
            Location loc_ = loc.clone();
            moveForward(loc_, dir, 1);
            moveToOrigin(loc_, dir);
            loc_.add(rotateLocation(e.getLocation(), dir));
            loc_.setYaw(0);
            loc_.setPitch(0);

            if (DungeonManager.usedSpaces.contains(loc_)) {
                return false;
            }
        }
        return true;
    }

    public void place(Location loc, Direction dir) {
        //Bukkit.broadcastMessage(String.format("Initial origin: %s, %s, %s", location.getX(), location.getY(), location.getZ()));
        StructureRotation structureRotation;
        switch (dir) {
            case N -> {
                loc.add(ENTRY.x, -ENTRY.y, ENTRY.z - 1);
                structureRotation = StructureRotation.CLOCKWISE_180;
            }
            case S -> {
                loc.add(-ENTRY.x, -ENTRY.y, -ENTRY.z + 1);
                structureRotation = StructureRotation.NONE;
            }
            case E -> {
                loc.add(-ENTRY.z + 1, -ENTRY.y, ENTRY.x);
                structureRotation = StructureRotation.COUNTERCLOCKWISE_90;
            }
            case W -> {
                loc.add(ENTRY.z - 1, -ENTRY.y, -ENTRY.x);
                structureRotation = StructureRotation.CLOCKWISE_90;
            }
            default -> throw new IllegalArgumentException("Illegal direction: \"" + dir + "\" (DungeonStructure:212)");
        }
        //Bukkit.broadcastMessage(String.format("Origin moved to: %s, %s, %s (%s)", location.getX(), location.getY(), location.getZ(), dir));
        STRUCTURE.place(loc, false, structureRotation, Mirror.NONE, 0, 1.0f, DungeonManager.RANDOM);
        //Fun
        STRUCTURE_FUN.place(new Location(loc.getWorld(), loc.getX(), loc.getY() + 10, loc.getZ()), false, structureRotation, Mirror.NONE, 0, 1.0f, DungeonManager.RANDOM); // previous: funvalue i y aksen
        for (Entity e : STRUCTURE.getEntities()) {
            String name = ChatColor.stripColor(e.getName());
            switch (name) {
                case "Forward", "Right", "Left", "Backward" -> {
                    Location eLoc = e.getLocation();
                    //Bukkit.broadcastMessage(String.format(ChatColor.AQUA + "Entity rel loc: %s, %s, %s", eLoc.getBlockX(), eLoc.getBlockY(), eLoc.getBlockZ()));
                    rotateLocation(eLoc, dir);
                    //Bukkit.broadcastMessage(String.format(ChatColor.AQUA + "Rotated: %s, %s, %s (%s) and adding node", eLoc.getX(), eLoc.getY(), eLoc.getZ(), dir));
                    eLoc.add(loc);
                    //spawnTextMarker(eLoc, e.getName());
                    DungeonManager.addNode(new SpawnNode(eLoc, rotateNode(Direction.fromString(name), structureRotation)));
                }
                case "Space" -> {
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    eLoc.setYaw(0);
                    eLoc.setPitch(0);
                    DungeonManager.usedSpaces.add(eLoc);
                    //spawnTextMarker(eLoc, e.getName());
                    //Bukkit.broadcastMessage(ChatColor.AQUA + "Space used up at " + Utils.printLocation(eLoc));
                }
                case "Wall FB", "Wall RL" -> {
                    String wallDirStr = name.substring(name.length() - 2);
                    Direction wallDir = Direction.fromString(wallDirStr);
                    wallDir = rotateNode(wallDir, structureRotation); // inkluderer absolutt rotasjon
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    DungeonManager.walls.put(eLoc, new SpawnFeature(wallDir, eLoc, DungeonManager.features.get("wall")));
                }
                case "Iron Arch FB", "Iron Arch RL" -> {
                    String archDirStr = name.substring(name.length() - 2);
                    Direction archDir = Direction.fromString(archDirStr);
                    archDir = rotateNode(archDir, structureRotation);
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    DungeonManager.ironArches.add(new SpawnFeature(archDir, eLoc, DungeonManager.features.get("iron_arch")));
                }
                case "Skeleton" -> {
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    eLoc.add(0.5, 0, 0.5);
                    Skeleton skeleton = (Skeleton) Main.WORLD.spawnEntity(eLoc, EntityType.SKELETON);
                    Objects.requireNonNull(skeleton.getEquipment()).setHelmet(new ItemStack(Material.IRON_HELMET));
                    skeleton.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                    skeleton.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                    skeleton.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
                    skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
                    skeleton.setPersistent(true); // sånn at han ikke despawner
                }
                default -> {
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    Bukkit.broadcastMessage(ChatColor.GRAY + name);
                    spawnTextMarker(eLoc, ChatColor.RED + e.getName());}
            }
        }
    }
}
