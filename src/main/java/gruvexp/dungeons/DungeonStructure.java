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
    private final Structure structure;
    private final Structure structureVisualization; //Fun
    private final Coord entry;
    public final HashMap<Location, RelativeDirection> exitLocations = new HashMap<>(); // steder som denne strukturen fører til
    public final RoomType roomType;

    public DungeonStructure(String structureGroup, String structureName, Coord entry, RoomType roomType) {
        this.roomType = roomType;
        structure = DungeonManager.STRUCTURE_MANAGER.loadStructure(new NamespacedKey(structureGroup, structureName));
        structureVisualization = DungeonManager.STRUCTURE_MANAGER.loadStructure(new NamespacedKey(structureGroup, "_" + structureName)); //Fun
        if (structure == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + new NamespacedKey(structureGroup, structureName).namespace() + "\" fins ikke!");
        }
        this.entry = entry;
        for (Entity e : structure.getEntities()) {
            String[] name = ChatColor.stripColor(e.getName()).split(" ");
            String directionStr = name[0];
            switch (directionStr) {
                case "Forward", "Right", "Left", "Backward" -> {
                    Location eLoc = e.getLocation();
                    eLoc.setX(eLoc.getBlockX());
                    eLoc.setZ(eLoc.getBlockZ());
                    RelativeDirection dir = RelativeDirection.fromString(directionStr);
                    exitLocations.put(eLoc, dir);
                }
            }
        }
    }

    private static Direction rotateNode(RelativeDirection relDir, StructureRotation structureRotation) {
        Direction dir = relDir.toAbsoluteDirection();
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

    public static void moveForward(Location loc, Direction dir, int blocks) {
        switch (dir) {
            case S, NS -> loc.add(0, 0, blocks);
            case W -> loc.add(-blocks, 0, 0);
            case N -> loc.add(0, 0, -blocks);
            case E, EW -> loc.add(blocks, 0, 0);
            default -> throw new IllegalArgumentException("Illegal direction: " + dir + " (Dungeonstructure:118)");
        }
    }

    public static void spawnTextMarker(Location loc, String name, String tag) {
        Location spawnLoc = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5);
        TextDisplay textDisplay = Main.WORLD.spawn(spawnLoc, TextDisplay.class);
        textDisplay.setText(name);
        textDisplay.setBillboard(TextDisplay.Billboard.VERTICAL);
        textDisplay.addScoreboardTag(tag);
    }

    private void moveToOrigin(Location loc, Direction dir) {
        // lokasjonen må justeres basert på hvor innganga er i neste rom, sånn at det neste rommet spawner på en plass sånn at innganga akkurat passer med utanga til den forrige strukturen
        // så den flyttes til ett av hjørnene av strukturen som er det hjørnet med lavest xyz verdi i library verdenen
        switch (dir) {
            case N -> loc.add( entry.x, -entry.y,  entry.z);
            case S -> loc.add(-entry.x, -entry.y, -entry.z);
            case E -> loc.add(-entry.z, -entry.y,  entry.x);
            case W -> loc.add( entry.z, -entry.y, -entry.x);
            default -> throw new IllegalArgumentException("Illegal direction: \"" + dir + "\" (DungeonStructure:148)");
        }
    }

    public boolean hasConflictingExits(Dungeon dungeon, Location loc, Direction dir) {
        // sjekker om noen av endepunktene kommer til å kræsje inn i en vegg eller om de passer sammen
        // input absolutt lokasjon: i endepunktet på det forrige rommet. input retning: retninga til exiten i det forrige rommet.
        // gå her for mer info: /tp -471.31 101.00 -364.36
        loc = loc.clone();
        DungeonManager.spawnTextMarker(loc, ChatColor.LIGHT_PURPLE + "start1", "conflict");
        moveForward(loc, dir, 1);
        moveToOrigin(loc, dir);
        DungeonManager.spawnTextMarker(loc, ChatColor.LIGHT_PURPLE + "origin2", "conflict");
        for (Map.Entry<Location, RelativeDirection> entry : exitLocations.entrySet()) { // i framtida add sånn at det fins unntak, feks i t kryss så kan den ene veggen bli sett på som inngang, sånn at et rom kan spawne der uten problemer, men blir en vegg hvis ingen rom spawner.
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Raw: " + Utils.printLocation(exitLoc));
            Location rotatedExitSpace = rotateLocation(entry.getKey().clone(), dir);
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Rotated: " + Utils.printLocation(rotatedExitSpace));
            rotatedExitSpace.add(loc); // gjør om til absolutt lokasjon
            DungeonManager.spawnTextMarker(rotatedExitSpace, ChatColor.LIGHT_PURPLE + "exit3", "conflict");
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Inworld: " + Utils.printLocation(rotatedExitSpace)); // flytter 1 blocc fram akkuratt som når man skal spawne inn så flytter man 1 fram fra exit pktet i det forrige rommet til entry pktet i dette rommet.
            rotatedExitSpace.setX(rotatedExitSpace.getBlockX());
            rotatedExitSpace.setZ(rotatedExitSpace.getBlockZ());
            rotatedExitSpace.setYaw(0);
            rotatedExitSpace.setPitch(0);
            //Bukkit.broadcastMessage(ChatColor.GREEN + "Moved: " + Utils.printLocation(rotatedExitSpace));
            if (dungeon.linkLocations.contains(rotatedExitSpace)) { // hvis en av veiene fører rett inn i en ann vei, så connectes de og det går bra
                DungeonManager.spawnTextMarker(rotatedExitSpace.add(0, 1, 0), ChatColor.YELLOW + "Link", "conflict");
                return false;
            } else {
                RelativeDirection relExitDir = entry.getValue();
                moveForward(rotatedExitSpace, dir.rotate(relExitDir), roomType.gridSize / 2 + 1);
                if (dungeon.usedSpaces.contains(rotatedExitSpace)) {
                    DungeonManager.spawnTextMarker(rotatedExitSpace.add(0, 1, 0), ChatColor.RED + "Conflict", "conflict");
                    return true; // ellers hvis spacet der er tatt, så er det en vegg der og det går ikke
                }
                DungeonManager.spawnTextMarker(rotatedExitSpace.add(0, 1, 0), ChatColor.GREEN + "No conflict", "conflict");
            }
            //Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + Utils.printLocation(rotatedExitSpace) + " is free, spawing in structure");
            //spawnTextMarker(rotatedExitSpace, ChatColor.LIGHT_PURPLE + "free space");
        }
        return false;
    }

    public boolean availableSpace(Dungeon dungeon, Location loc, Direction dir) {
        // sjekker om det er plass til å spawne denne structuren eller om det er spaces som er opptatt av et annet rom i dungenen.
        // input lokasjon (absolutt) og retning: exitpkt i forrige rom.
        for (Entity e : structure.getEntities()) {
            if (!ChatColor.stripColor(e.getName()).equals("Space")) {continue;}
            Location spaceLoc = loc.clone();
            moveForward(spaceLoc, dir, 1);
            moveToOrigin(spaceLoc, dir);
            spaceLoc.add(rotateLocation(e.getLocation(), dir));
            spaceLoc.setYaw(0);
            spaceLoc.setPitch(0);

            if (dungeon.usedSpaces.contains(spaceLoc)) {
                return false;
            }
        }
        return true;
    }

    public void place(Dungeon dungeon, Location loc, Direction dir) {
        //Bukkit.broadcastMessage(String.format("Initial origin: %s, %s, %s", location.getX(), location.getY(), location.getZ()));
        StructureRotation structureRotation;
        switch (dir) {
            case N -> {
                loc.add(entry.x, -entry.y, entry.z - 1);
                structureRotation = StructureRotation.CLOCKWISE_180;
            }
            case S -> {
                loc.add(-entry.x, -entry.y, -entry.z + 1);
                structureRotation = StructureRotation.NONE;
            }
            case E -> {
                loc.add(-entry.z + 1, -entry.y, entry.x);
                structureRotation = StructureRotation.COUNTERCLOCKWISE_90;
            }
            case W -> {
                loc.add(entry.z - 1, -entry.y, -entry.x);
                structureRotation = StructureRotation.CLOCKWISE_90;
            }
            default -> throw new IllegalArgumentException("Illegal direction: \"" + dir + "\" (DungeonStructure:212)");
        }
        //Bukkit.broadcastMessage(String.format("Origin moved to: %s, %s, %s (%s)", location.getX(), location.getY(), location.getZ(), dir));
        structure.place(loc, false, structureRotation, Mirror.NONE, 0, 1.0f, DungeonManager.RANDOM);
        //Fun
        if (structureVisualization != null) {
            structureVisualization.place(new Location(loc.getWorld(), loc.getX(), loc.getY() + 10, loc.getZ()), false,
                    structureRotation, Mirror.NONE, 0, 1.0f, DungeonManager.RANDOM); // previous: funvalue i y aksen
        }
        for (Entity e : structure.getEntities()) {
            String[] name = ChatColor.stripColor(e.getName()).split(" ");
            String type = name[0];
            switch (type) {
                case "Forward", "Right", "Left", "Backward" -> {
                    Location eLoc = e.getLocation();
                    //Bukkit.broadcastMessage(String.format(ChatColor.AQUA + "Entity rel loc: %s, %s, %s", eLoc.getBlockX(), eLoc.getBlockY(), eLoc.getBlockZ()));
                    rotateLocation(eLoc, dir);
                    //Bukkit.broadcastMessage(String.format(ChatColor.AQUA + "Rotated: %s, %s, %s (%s) and adding node", eLoc.getX(), eLoc.getY(), eLoc.getZ(), dir));
                    eLoc.add(loc); // relativ -> absolutt lokasjon
                    //spawnTextMarker(eLoc, e.getName());
                    RelativeDirection relDir = RelativeDirection.fromString(type);
                    Direction spawnNodeDir = dir.rotate(relDir);
                    dungeon.addNode(new SpawnNode(dungeon, eLoc, spawnNodeDir, RoomType.valueOf(name[1])));
                }
                case "Space" -> {
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    eLoc.setYaw(0);
                    eLoc.setPitch(0);
                    dungeon.usedSpaces.add(eLoc);
                    //spawnTextMarker(eLoc, e.getName());
                    //Bukkit.broadcastMessage(ChatColor.AQUA + "Space used up at " + Utils.printLocation(eLoc));
                }
                case "Wall FB", "Wall RL" -> {
                    String wallDirStr = type.substring(type.length() - 2);
                    RelativeDirection wallRelDir = RelativeDirection.fromString(wallDirStr);
                    Direction wallDir = rotateNode(wallRelDir, structureRotation); // inkluderer absolutt rotasjon
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    DungeonManager.walls.put(eLoc, new SpawnFeature(wallDir, eLoc, Feature.WALL));
                }
                case "Iron Arch FB", "Iron Arch RL" -> {
                    String archDirStr = type.substring(type.length() - 2);
                    RelativeDirection archRelDir = RelativeDirection.fromString(archDirStr);
                    Direction archDir = rotateNode(archRelDir, structureRotation);
                    Location eLoc = e.getLocation();
                    rotateLocation(eLoc, dir);
                    eLoc.add(loc);
                    DungeonManager.ironArches.add(new SpawnFeature(archDir, eLoc, Feature.IRON_ARCH));
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
                    Bukkit.broadcastMessage(ChatColor.GRAY + type);
                    spawnTextMarker(eLoc, ChatColor.RED + e.getName(), "unknown_entity");}
            }
        }
    }
}
