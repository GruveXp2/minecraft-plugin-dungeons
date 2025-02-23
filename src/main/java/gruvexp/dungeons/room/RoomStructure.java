package gruvexp.dungeons.room;

import gruvexp.dungeons.*;
import gruvexp.dungeons.commands.DungeonCommand;
import gruvexp.dungeons.dungeon.Dungeon;
import gruvexp.dungeons.location.Coord;
import gruvexp.dungeons.location.Direction;
import gruvexp.dungeons.location.RelativeDirection;
import gruvexp.dungeons.support.RoofType;
import gruvexp.dungeons.support.SupportNode;
import gruvexp.dungeons.support.SupportType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.structure.Structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class RoomStructure extends gruvexp.dungeons.Structure {
    private final Structure structureVisualization; //Fun
    public final HashMap<Location, RelativeDirection> exitLocations = new HashMap<>(); // steder som denne strukturen fører til
    public final RoomType roomType;

    public RoomStructure(String structureGroup, String structureName, RoomType roomType) {
        super(structureGroup, structureName);
        this.roomType = roomType;
        structureVisualization = DungeonManager.STRUCTURE_MANAGER.loadStructure(new NamespacedKey(structureGroup, "_" + structureName)); //Fun
        Coord entry = null;
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
                case "Entry" -> {
                    Location vecEntry = e.getLocation();
                    entry = new Coord(vecEntry.getBlockX(), vecEntry.getBlockY(), vecEntry.getBlockZ());
                }
            }
        }
        if (entry == null) { // hvis man har glemt å lage en entry
            entry = new Coord(3, 3, 3);
        }
        this.entry = entry;
    }

    public boolean hasConflictingExits(Dungeon dungeon, Location loc, Direction dir) {
        // sjekker om noen av endepunktene kommer til å kræsje inn i en vegg eller om de passer sammen
        // input absolutt lokasjon: i endepunktet på det forrige rommet. input retning: retninga til exiten i det forrige rommet.
        // gå her for mer info: /tp -471.31 101.00 -364.36
        //Bukkit.broadcast(Component.text("---Checking exits---"));
        Location locOrigin = loc.clone();
        //DungeonManager.spawnTextMarker(locOrigin, ChatColor.LIGHT_PURPLE + "start1", "conflict");
        DungeonManager.moveForward(locOrigin, dir, 1);
        moveToOrigin(locOrigin, dir);
        for (Map.Entry<Location, RelativeDirection> entry : exitLocations.entrySet()) {
            // i framtida add sånn at det fins unntak, feks i t kryss så kan den ene veggen bli sett på som inngang, sånn at et rom kan spawne der uten problemer, men blir en vegg hvis ingen rom spawner.
            Location rotatedExitSpace = DungeonManager.rotateLocation(entry.getKey().clone(), dir);
            rotatedExitSpace.add(locOrigin); // gjør om til absolutt lokasjon
            rotatedExitSpace.setX(rotatedExitSpace.getBlockX());
            rotatedExitSpace.setZ(rotatedExitSpace.getBlockZ());
            rotatedExitSpace.setYaw(0);
            rotatedExitSpace.setPitch(0);

            RelativeDirection relExitDir = entry.getValue();
            Direction exitDir = dir.rotate(relExitDir);
            DungeonManager.moveForward(rotatedExitSpace, exitDir, 1);
            //Bukkit.broadcast(Component.text(" - Exitloc at: " + Utils.printLocation(rotatedExitSpace), NamedTextColor.GRAY));
            if (DungeonCommand.extnodchk) {
                DungeonManager.spawnTextMarker(rotatedExitSpace, ChatColor.LIGHT_PURPLE + "extnodchk", "conflict");
            }
            if (dungeon.linkLocations.contains(rotatedExitSpace)) { // hvis en av veiene fører rett inn i en ann vei, så connectes de og det går bra
                //Bukkit.broadcast(Component.text(" - Connected", NamedTextColor.GREEN));
                DungeonManager.spawnTextMarker(rotatedExitSpace.add(0, 1.25, 0), ChatColor.YELLOW + "Link", "conflict");
                return false;
            } else {
                //Bukkit.broadcast(Component.text(" - - Not connected, checking for space", NamedTextColor.GRAY));
                DungeonManager.moveForward(rotatedExitSpace, dir.rotate(relExitDir), roomType.gridSize / 2);
                if (dungeon.usedSpaces.contains(rotatedExitSpace)) {
                    //Bukkit.broadcast(Component.text(" - - Used space", NamedTextColor.RED));
                    if (DungeonCommand.usedspace) {
                        DungeonManager.spawnTextMarker(rotatedExitSpace.add(0, 1.25, 0), ChatColor.RED + "Conflict", "conflict");
                    }
                    return true; // ellers hvis spacet der er tatt, så er det en vegg der og det går ikke
                }
                if (DungeonCommand.usedspace) {
                    DungeonManager.spawnTextMarker(rotatedExitSpace.add(0, 1.25, 0), ChatColor.GREEN + "No conflict", "conflict");
                }
                //Bukkit.broadcast(Component.text(" - - Free space", NamedTextColor.GREEN));
            }
        }
        return false;
    }

    public boolean availableSpace(Dungeon dungeon, Location loc, Direction dir) {

        // 2 DO!!! GJØR SÅNN AT MAN KAN STOPPE PÅ FORSKJELLIGE STEPS UTEN Å SPAWNE, FEKS VED Å TESTE AVAILABLE SPACE PÅ BRIDGES_X
        // og da spawnes det feks, man kan selecte hvem som skal testes på og da kommer det opp tekst og man kan gå til neste osv

        // sjekker om det er plass til å spawne denne structuren eller om det er spaces som er opptatt av et annet rom i dungenen.
        // input lokasjon (absolutt) og retning: exitpkt i forrige rom.
        Location locOrigin = loc.clone();
        DungeonManager.moveForward(locOrigin, dir, 1);

        Location locSpaceEntry = locOrigin.clone();
        DungeonManager.moveForward(locSpaceEntry, dir, roomType.gridSize / 2); // vil være plassert der space blokken er foran entrypointet

        moveToOrigin(locOrigin, dir); // origin point for structen i verdenen
        for (Entity e : structure.getEntities()) { // looper gjennom alle space shulkers
            if (!ChatColor.stripColor(e.getName()).startsWith("Space")) {continue;}
            Location locSpace = locOrigin.clone();
            locSpace.add(DungeonManager.rotateLocation(e.getLocation(), dir));
            locSpace.setYaw(0);
            locSpace.setPitch(0); // abs pos til shulkerboxen

            if (dungeon.usedSpaces.contains(locSpace)) {
                //Bukkit.broadcast(Component.text("No space for this room, another room already generated there", NamedTextColor.GRAY));
                return false;
            } else if (dungeon.reservedSpaces.containsKey(locSpace)) {
                ReservedSpace space = dungeon.reservedSpaces.get(locSpace);
                if (DungeonCommand.showreserved) {
                    Bukkit.broadcast(Component.text("---Checking space---"));
                    Bukkit.broadcast(Component.text("Reserved loc: " + Utils.printLocation(locSpace), NamedTextColor.GRAY));
                    DungeonManager.spawnTextMarker(locSpace, "Reserved space", "showreserved"); // reserved space found while trying to spawn
                }
                for (Direction connectionDir : space.getConnections()) {
                    if (connectionDir.rotate(RelativeDirection.BACKWARD) == dir && locSpace.equals(locSpaceEntry)) continue; // strukturens entrypoint håndterer allerede den retninga, ikke exit pointsene

                    Location connectionLoc = locSpace.clone(); // hvor det skjekkes om er et exit point
                    DungeonManager.moveForward(connectionLoc, connectionDir, roomType.gridSize/2);
                    if (DungeonCommand.showdircheck) {
                        Bukkit.broadcast(Component.text(" - Reserved direction: " + connectionDir, NamedTextColor.GRAY));
                        Bukkit.broadcast(Component.text(" \\ Reserved connection: " + Utils.printLocation(connectionLoc), NamedTextColor.GRAY));
                        DungeonManager.spawnTextMarker(connectionLoc.clone().add(0, 0.1, 0), ChatColor.GRAY + "rescon", "showdircheck");
                    }
                    boolean hasConnection = false; // hvis det er en reservert plass, så forteller denne om det går ut en vei som connecter med den som reserverte
                    for (Location exitVec : exitLocations.keySet()) {
                        Location exitLoc = DungeonManager.rotateLocation(exitVec.clone(), dir);
                        exitLoc.add(locOrigin);
                        //Bukkit.broadcast(Component.text(" - - Struc connection: " + Utils.printLocation(exitLoc), NamedTextColor.GRAY));
                        if (exitLoc.equals(connectionLoc)) {
                            hasConnection = true;
                            if (DungeonCommand.strucon) {
                                DungeonManager.spawnTextMarker(connectionLoc.clone().add(0, 0.1, 0), ChatColor.GREEN + "strucon", "check");
                            }
                            break;
                        } else if (DungeonCommand.strucon) {
                            DungeonManager.spawnTextMarker(connectionLoc.clone().add(0, 0.1, 0), ChatColor.RED + "!strucon", "check");
                        }
                    }
                    if (!hasConnection) {
                        //Bukkit.broadcast(Component.text("This room is missing exit points that matches all reserved connections", NamedTextColor.GRAY));
                        return false; // hvis det ikke er en node i retninga som er reservert, så blir det ikke connection
                    }
                }
            }
        }
        return true;
    }

    public void place(Dungeon dungeon, Location loc, Direction dir) {
        //Bukkit.broadcastMessage(String.format("Initial origin: %s, %s, %s", location.getX(), location.getY(), location.getZ()));
        //DungeonManager.spawnTextMarker(loc, ChatColor.AQUA + "start", "conflict");
        DungeonManager.moveForward(loc, dir, 1);
        moveToOrigin(loc, dir);
        //DungeonManager.spawnTextMarker(loc, ChatColor.AQUA + "origin", "conflict");
        StructureRotation structureRotation = dir.toStructureRotation();
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
            Location eLoc = e.getLocation();
            DungeonManager.rotateLocation(eLoc, dir);
            eLoc.add(loc); // relativ -> absolutt lokasjon
            switch (type) {
                case "Forward", "Right", "Left", "Backward" -> {
                    //spawnTextMarker(eLoc, e.getName());
                    RelativeDirection relDir = RelativeDirection.fromString(type);
                    Direction spawnNodeDir = dir.rotate(relDir);
                    HashSet<Room> bannedRooms = new HashSet<>();
                    for (int i = 2; i < name.length; i++) {
                        String attr = name[i];
                        if (attr.charAt(0) == '!') {
                            String room = attr.substring(1).toUpperCase();
                            bannedRooms.add(Room.valueOf(roomType.name() + "_" + room));
                        }
                    }
                    dungeon.addNode(new RoomNode(dungeon, eLoc, spawnNodeDir, RoomType.valueOf(name[1]), bannedRooms));
                }
                case "Support" -> {
                    //spawnTextMarker(eLoc, e.getName());
                    RelativeDirection relDir = RelativeDirection.FORWARD;
                    Direction spawnNodeDir = dir.rotate(relDir);
                    dungeon.addNode(new SupportNode(dungeon, eLoc, spawnNodeDir, SupportType.valueOf(name[1])));
                }
                case "Space" -> {
                    eLoc.setYaw(0);
                    eLoc.setPitch(0);
                    dungeon.usedSpaces.add(eLoc);
                    if (name.length == 2) {
                        String[] roofInfo = name[1].split(":");
                        RoofType roofType = RoofType.valueOf(roomType.name() + "_" + roofInfo[0]);
                        RelativeDirection roofRelDir = RelativeDirection.fromString(roofInfo[1]);
                        Direction roofDir = dir.rotate(roofRelDir);
                        dungeon.roofSpaces.put(eLoc, new RoofSpace(roofType, roofDir));
                    }
                    //spawnTextMarker(eLoc, e.getName());
                }
                case "Skeleton" -> {
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
                case "Entry" -> {}
                default -> {
                    Bukkit.broadcast(Component.text(type, NamedTextColor.GRAY));
                    DungeonManager.spawnTextMarker(eLoc, ChatColor.RED + e.getName(), "unknown_entity");}
            }
        }
    }
}
