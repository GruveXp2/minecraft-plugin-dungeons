package gruvexp.dungeons;

import gruvexp.dungeons.location.Direction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

import static gruvexp.dungeons.DungeonManager.RANDOM;

public abstract class Dungeon {
    protected final HashMap<RoomType, StructurePool> structurePools = new HashMap<>();
    protected final HashMap<RoomType, Integer> roomCount = new HashMap<>();
    protected final HashMap<RoomType, Integer> activeNodes = new HashMap<>();
    protected final Queue<SpawnNode> spawnNodeQue = new LinkedList<>(); // liste med SpawnNodes (en spawnnode er en posisjon og retning som det kan spånes et rom fra)
    public final HashSet<Location> usedSpaces = new HashSet<>();
    public final HashMap<Location, ReservedSpace> reservedSpaces = new HashMap<>();
    public final HashSet<Location> linkLocations = new HashSet<>();
    public final Room roomOrigin;

    protected int roomTickCount = 0;
    protected int maxRooms = 0;
    public int visualizerPosY = 0;
    public boolean manualSpawn = false;

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

    public void reserveSpace(Location loc, Direction dir) {
        reservedSpaces.putIfAbsent(loc, new ReservedSpace(loc));
        reservedSpaces.get(loc).connect(dir);
    }

    private void nextNode() {
        if (spawnNodeQue.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "no more rooms to generate");
            //postGeneration();
            return;
        }
        spawnNodeQue.poll().spawn(this);
        if (RANDOM.nextInt(5) == 0) {
            visualizerPosY++;
        }
        if (!manualSpawn) {
            roomTickCount --;
            if (roomTickCount > 0) {nextNode();}
            else {
                Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    roomTickCount = spawnNodeQue.size();
                    Bukkit.broadcast(Component.text("Spawning " + roomTickCount + " rooms..."));
                    Bukkit.broadcast(Component.text("- Bridges: " + roomCount.get(RoomType.FORTRESS_BRIDGE) + "T, " + activeNodes.get(RoomType.FORTRESS_BRIDGE) + "A, "));
                    Bukkit.broadcast(Component.text("- Corridors: " + roomCount.get(RoomType.FORTRESS_CORRIDOR) + "T, " + activeNodes.get(RoomType.FORTRESS_CORRIDOR) + "A, "));
                    nextNode();
                }, 10L);
            }

        }
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
        if (manualSpawn) {
            Bukkit.broadcast(Component.text("========Next Node========", NamedTextColor.YELLOW));
            nextNode();
        }
    }

    public void addNode(SpawnNode spawnNode) {
        spawnNodeQue.add(spawnNode);
    }
}
