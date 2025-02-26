package gruvexp.dungeons.dungeon;

import gruvexp.dungeons.*;
import gruvexp.dungeons.commands.DungeonCommand;
import gruvexp.dungeons.location.Direction;
import gruvexp.dungeons.room.Room;
import gruvexp.dungeons.room.RoomType;
import gruvexp.dungeons.room.RoomNode;
import gruvexp.dungeons.support.SupportNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

import static gruvexp.dungeons.DungeonManager.RANDOM;

public abstract class Dungeon {
    public final HashMap<RoomType, StructurePool> structurePools = new HashMap<>();
    public final HashMap<RoomType, Integer> roomCount = new HashMap<>();
    public final HashMap<RoomType, Integer> activeNodes = new HashMap<>();
    protected final Queue<RoomNode> roomNodeQue = new LinkedList<>(); // liste med SpawnNodes (en spawnnode er en posisjon og retning som det kan spånes et rom fra)
    protected final Queue<SupportNode> supportNodeQue = new LinkedList<>();
    public final HashSet<Location> usedSpaces = new HashSet<>();
    public final HashMap<Location, ReservedSpace> reservedSpaces = new HashMap<>();
    public final HashMap<Location, RoofSpace> roofSpaces = new HashMap<>();
    public final HashSet<Location> linkLocations = new HashSet<>();
    public final Room roomOrigin;

    public final HashMap<Structure, Integer> structureCount = new HashMap<>();

    protected int roomTickCount = 0;
    protected int supportTickCount = 0;
    protected int maxRooms = 0;
    public int visualizerPosY = 0;

    public Dungeon(Room roomOrigin) {
        this.roomOrigin = roomOrigin;
    }

    public void makeDungeon(Location location, Direction direction, int size) {
        Bukkit.broadcastMessage("spawning dungeon...");
        usedSpaces.clear();
        maxRooms = size;
        visualizerPosY = location.getBlockY() + 4;
        roomOrigin.structure().place(this, location, direction);
        nextNode();

    }

    public void increaseStructureCount(Structure structure) {
        if (!structureCount.containsKey(structure)) {
            structureCount.put(structure, 1);
            return;
        }
        structureCount.put(structure, structureCount.get(structure) + 1);
    }

    public void reserveSpace(Location loc, Direction dir) {
        reservedSpaces.putIfAbsent(loc, new ReservedSpace(loc));
        reservedSpaces.get(loc).connect(dir);
    }

    private void nextNode() {
        if (roomNodeQue.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "no more rooms to generate");
            DungeonCommand.quickspawn = false;
            nextSupportNode();
            return;
        }
        roomNodeQue.poll().spawn(this);
        if (RANDOM.nextInt(5) == 0) {
            visualizerPosY++;
        }
        if (!DungeonManager.manualSpawn || DungeonCommand.quickspawn) {
            roomTickCount--;
            if (roomTickCount > 0) {
                nextNode();
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    roomTickCount = roomNodeQue.size();
                    Bukkit.broadcast(Component.text("Spawning " + roomTickCount + " rooms..."));
                    Bukkit.broadcast(Component.text("- Bridges: " + roomCount.get(RoomType.FORTRESS_BRIDGE) + "T, " + activeNodes.get(RoomType.FORTRESS_BRIDGE) + "A, "));
                    Bukkit.broadcast(Component.text("- Corridors: " + roomCount.get(RoomType.FORTRESS_CORRIDOR) + "T, " + activeNodes.get(RoomType.FORTRESS_CORRIDOR) + "A, "));
                    nextNode();
                }, 10L);
            }

        }
    }

    private void nextSupportNode() {
        if (supportNodeQue.isEmpty()) {
            Bukkit.broadcast(Component.text("no more support structs to generate", NamedTextColor.YELLOW));
            structureCount.keySet().stream().sorted(Comparator.comparing(s -> s.name))
                    .forEach(s -> Bukkit.broadcast(Component.text(s.name + ": " + structureCount.get(s))));
            return;
        }
        supportNodeQue.poll().spawn(this);
        if (!DungeonManager.manualSpawn) {
            supportTickCount--;
            if (supportTickCount > 0) {
                nextSupportNode();}
            else {
                Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    supportTickCount = supportNodeQue.size();
                    Bukkit.broadcast(Component.text("Spawning " + supportTickCount + " support structures..."));
                    nextSupportNode();
                }, 10L);
            }

        } else if (!DungeonManager.spawnedSpecialSupport) {
            nextSupportNode();
        }
        DungeonManager.spawnedSpecialSupport = false;
    }

    /*private void postGeneration() { // genererer stuff som vegger, jernbuer, hindringer osv
        // vegger
        Bukkit.broadcastMessage("This dungeon has a total of " + roomCount + " rooms");
        int wallCount = 0;
        for (Map.Entry<Location, SpawnFeature> wallEntry : walls.entrySet()) { // skjekk om bøggfix funker
            SpawnFeature wall = wallEntry.getValue();
            if (wall == null) {continue;} // hvis veggen er fjerna
            // når man flytter han framover så vil keyen i hashmappet være uendra, det er viktig fordi man skal skjekke om
            // posisjonen foran er en key i hashmappet, og hvis vi ikke kopierer så ville keyen også blitt endra og vi ville fått true uansett,
            // noe som bare vil spawne jernbuer \/
            Location loc = wallEntry.getKey().clone();
            Direction dir = wall.getDirection();
            if (walls.containsKey(DungeonStructure.moveForward(loc, dir, 1)) || walls.containsKey(DungeonStructure.moveForward(loc, dir, -2))) {
                // flytter først fram 1 blocc, og så flytter tilbake 2 bloccer,
                // først til midten og så til andre sida (så movelocation endrer locationen du putter inn)
                walls.put(loc, null); // den andre veggen fjernes, så basically den ene veggen blir jernbue istedet og den andre blir fjerna
                if (RANDOM.nextInt(4) == 0) { // 25% sjangs for at veggen forblir alikavel
                    wall.spawn();
                    wallCount++;
                    continue;
                }
                //Bukkit.broadcastMessage("2 adjacent walls was removed");
                loc.add(0, 2, 0); // jernbuer sitt startpkt er 2 blokker over bakken, i motsetning til vegger, der er shulkerboksene og dermed også startpktene på bakken
                ironArches.add(new SpawnFeature(dir, loc, Feature.IRON_ARCH));
            } else {
                wall.spawn();
                wallCount++;
            }
        }
        Bukkit.broadcastMessage("Spawned in " + wallCount + " walls");
        walls.clear();

        int archCount = 0;
        for (SpawnFeature arch : ironArches) {
            arch.spawn(RANDOM.nextFloat());
            archCount++;
        }
        Bukkit.broadcastMessage("Spawned in " + archCount + " iron arches");
        ironArches.clear();
        // hindringer
        // ha liste med hindringer, velg ut random og spawn (rarity på de?)
    }*/

    public abstract GrowRate getRandomExpansionRate(RoomType roomType);
        // Sånn funker det:
        //  0 -  50 rom: (i begynnelsen) her ekspanderes det veldig mye
        // 50 - 200 rom: her skjer det i gjennomsnitt ingen ekspansjon
        //      200+rom: her genereres det lange ganger ut fra kjernen, og det er dobbelt så stor sjangs for at en gang slutter enn at den deler seg.
        //               dette er for å gjøre det lettere å finne dungeonen, ved at man kan finne en lang sidegang som fører inn til midten

    public void manualNextNode() {
        if (DungeonManager.manualSpawn) {
            Bukkit.broadcast(Component.text("========Next Node========", NamedTextColor.YELLOW));
            nextNode();
        }
    }

    public void addNode(RoomNode roomNode) {
        roomNodeQue.add(roomNode);
    }

    public void addNode(SupportNode supportNode) {
        supportNodeQue.add(supportNode);
    }
}
