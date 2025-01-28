package gruvexp.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.structure.StructureManager;

import java.util.*;

public final class DungeonManager {

    public static final StructureManager STRUCTURE_MANAGER = Main.getPlugin().getServer().getStructureManager();
    public static final Random RANDOM = new Random();
    public static final HashMap<String, DungeonStructure> namedRooms = new HashMap<>(); // for å hente ut rommene ved navn
    public static final ArrayList<Room> exits = new ArrayList<>(); // blindvei
    public static final ArrayList<Room> hallways = new ArrayList<>(); // kun 1 utgang
    public static final ArrayList<Room> splits = new ArrayList<>(); // splittes i fler retninger
    public static final ArrayList<ArrayList<Room>> rooms = new ArrayList<>(); // liste med rom. index 0 er blindveier, 1 er ganger og 2 er rom som deler seg
    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne
    private static final Queue<SpawnNode> spawnNodeQue = new LinkedList<>(); // liste med SpawnNodes (en spawnnode er en posisjon og retning som det kan spånes et rom fra)
    public static final HashSet<Location> usedSpaces = new HashSet<>();
    private static int roomCount = 0;

    private static int roomTickCount = 0;
    private static int maxRooms = 0;
    public static int visualizerPosY = 0;
    public static boolean manualSpawn = false;
    public static void init() {
        exits.add(Room.END);
        hallways.addAll(List.of(Room.STRAIGHT, Room.SKELETON_TRAP, Room.ARROW_TRAP, Room.TURN_R, Room.TURN_L));
        splits.addAll(List.of(Room.T_SPLIT_R, Room.T_SPLIT_M, Room.T_SPLIT_L));
        rooms.add(exits);
        rooms.add(hallways);
        rooms.add(splits);
    }

    public static void makeDungeon(Location location, Direction direction, int size) {
        Bukkit.broadcastMessage("spawning dungeon...");
        usedSpaces.clear();
        spawnNodeQue.add(new SpawnNode(location, direction));
        maxRooms = size;
        roomCount = 0;
        visualizerPosY = location.getBlockY() + 4;
        nextNode();
    }

    private static void nextNode() {
        if (roomCount == 50) {
            Bukkit.broadcastMessage(String.format("%sReached 50 rooms, active nodes: %s", ChatColor.GREEN, spawnNodeQue.size()));
        } else if (roomCount == 200) {
            Bukkit.broadcastMessage(String.format("%sReached 200 rooms, active nodes: %s", ChatColor.GREEN, spawnNodeQue.size()));
        }
        if (roomCount > maxRooms) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "max rooms generated");
            spawnNodeQue.clear();
            postGeneration();
            return;
        }
        if (spawnNodeQue.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "no more rooms to generate");
            postGeneration();
            return;
        }
        roomCount ++;
        spawnNodeQue.poll().spawn(getRandomExpansionRate());
        if (RANDOM.nextInt(5) == 0) {
            visualizerPosY++;
        }
        if (!manualSpawn) {
            roomTickCount --;
            if (roomTickCount > 0) {nextNode();}
            else {
                Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    roomTickCount = spawnNodeQue.size();
                    Bukkit.broadcastMessage("Spawning " + roomTickCount + " rooms...");
                    nextNode();
                }, 2L);
            }

        }
    }

    private static void postGeneration() { // genererer stuff som vegger, jernbuer, hindringer osv
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
    }

    private static GrowRate getRandomExpansionRate() {
        // Sånn fungerer det:
        //  0 -  50 rom: (i begynnelsen) her ekspanderes det veldig mye
        // 50 - 200 rom: her skjer det i gjennomsnitt ingen ekspansjon
        //      200+rom: her genereres det lange ganger ut fra kjernen, og det er dobbelt så stor sjangs for at en gang slutter enn at den deler seg.
        //               dette er for å gjøre det lettere å finne dungeonen, ved at man kan finne en lang sidegang som fører inn til midten

        int expansionRate;
        if (roomCount < 50) {
            expansionRate = RANDOM.nextInt(2) + 1; // 50% 1, 50% 2.
        } else if (roomCount < 200) {
            expansionRate = RANDOM.nextInt(4);
            if (expansionRate > 1) {
                expansionRate -= 1; // 25% 0, 50% 1, 25% 2.
            }
            if (spawnNodeQue.size() < 15 && expansionRate == 0) {
                return GrowRate.EXPANDING; // hvis det er for lite noder så ekspanderer vi
            } else if (spawnNodeQue.size() > 25 && expansionRate == 2) {
                return GrowRate.SHRINKING; // hvis det er for mange noder så reduserer vi
            }
        } else {
            expansionRate = RANDOM.nextInt(40);
            if (expansionRate < 2) {
                return GrowRate.SHRINKING; // 4% 0, 94% 1, 2% 2
            } else if (expansionRate == 39) {
                return GrowRate.EXPANDING;
            } else {
                return GrowRate.STATIC;
            }
        }
        return switch (expansionRate) {
            case 0: yield GrowRate.SHRINKING;
            case 1: yield GrowRate.STATIC;
            case 2: yield GrowRate.EXPANDING;
            default: throw new IllegalStateException("Unexpected value: " + expansionRate);
        };
    }

    public static void manualNextNode() {
        if (manualSpawn) {
            nextNode();
        }
    }

    public static void addNode(SpawnNode spawnNode) {
        spawnNodeQue.add(spawnNode);
    }

    public static void loadStructure(String structureName, Location location, Direction direction) {
        Room room = Room.valueOf(structureName.toUpperCase());
        /*if (room == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + structureName + "\" fins ikke!");
        }*/
        DungeonStructure dungeonStructure = room.structure();
        dungeonStructure.place(location, direction);
    }



}
