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
    public static final ArrayList<DungeonStructure> exits = new ArrayList<>(); // blindvei
    public static final ArrayList<DungeonStructure> hallways = new ArrayList<>(); // kun 1 utgang
    public static final ArrayList<DungeonStructure> splits = new ArrayList<>(); // splittes i fler retninger
    public static final ArrayList<ArrayList<DungeonStructure>> rooms = new ArrayList<>(); // liste med rom. index 0 er blindveier, 1 er ganger og 2 er rom som deler seg
    public static final HashMap<String, DungeonFeature> features = new HashMap<>();
    public static final HashMap<Location, SpawnFeature> walls = new HashMap<>(); // liste med steder der vegger kan spawne
    public static final HashSet<SpawnFeature> ironArches = new HashSet<>(); // liste med steder som gitterbuer kan spawne
    private static final Queue<SpawnNode> spawnNodeQ = new LinkedList<>(); // liste med SpawnNodes (en spawnnode er en posisjon og retning som det kan spånes et rom fra)
    public static final HashSet<Location> usedSpaces = new HashSet<>();
    private static int roomCount = 0;

    private static int roomTickCount = 0;
    private static int maxRooms = 0;
    public static int funValue = 0;
    public static boolean manualSpawn = false;
    public static void init() {
        namedRooms.put("end", new DungeonStructure("end", new Coord(2, 1, 0)));
        namedRooms.put("straight", new DungeonStructure("straight", new Coord(2, 1, 0)));
        namedRooms.put("skeleton_trap", new DungeonStructure("skeleton_trap", new Coord(3, 1, 0))); // +1x pga må ha en blokk for å henge tripwire hook på
        namedRooms.put("arrow_trap", new DungeonStructure("arrow_trap", new Coord(3, 1, 0)));
        namedRooms.put("turn_r", new DungeonStructure("turn_r", new Coord(2, 1, 0)));
        namedRooms.put("turn_l", new DungeonStructure("turn_l", new Coord(2, 1, 0)));
        namedRooms.put("t_split_r", new DungeonStructure("t_split_r", new Coord(2, 1, 0)));
        namedRooms.put("t_split_m", new DungeonStructure("t_split_m", new Coord(2, 1, 0)));
        namedRooms.put("t_split_l", new DungeonStructure("t_split_l", new Coord(2, 1, 0)));
        exits.add(namedRooms.get("end"));
        hallways.add(namedRooms.get("straight"));
        hallways.add(namedRooms.get("skeleton_trap"));
        hallways.add(namedRooms.get("arrow_trap"));
        hallways.add(namedRooms.get("turn_r"));
        hallways.add(namedRooms.get("turn_l"));
        splits.add(namedRooms.get("t_split_r"));
        splits.add(namedRooms.get("t_split_m"));
        splits.add(namedRooms.get("t_split_l"));
        rooms.add(exits);
        rooms.add(hallways);
        rooms.add(splits);
        features.put("wall", new DungeonFeature(Set.of("wall_1", "wall_2", "wall_3"), new Coord(1, 0, 0)));
        features.put("iron_arch", new DungeonFeature(Set.of("iron_arch"), new Coord(1, 2, 0))); // coords: plasserte shulkeren i taket så det blir 2 blokker opp i y retning i forhold til feks vegger
    }

    public static void makeDungeon(Location location, Direction direction, int size) {
        Bukkit.broadcastMessage("spawning dungeon...");
        usedSpaces.clear();
        spawnNodeQ.add(new SpawnNode(location, direction));
        maxRooms = size;
        roomCount = 0;
        funValue = location.getBlockY() + 4;
        nextNode();
    }

    private static void nextNode() {
        if (roomCount == 50) {
            Bukkit.broadcastMessage(String.format("%sReached 50 rooms, active nodes: %s", ChatColor.GREEN, spawnNodeQ.size()));
        } else if (roomCount == 200) {
            Bukkit.broadcastMessage(String.format("%sReached 200 rooms, active nodes: %s", ChatColor.GREEN, spawnNodeQ.size()));
        }
        if (roomCount > maxRooms) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "max rooms generated");
            spawnNodeQ.clear();
            postGeneration();
            return;
        }
        if (spawnNodeQ.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "no more rooms to generate");
            postGeneration();
            return;
        }
        roomCount ++;
        spawnNodeQ.poll().spawn(getRandomExpansionRate());
        if (RANDOM.nextInt(5) == 0) {
            funValue ++;
        }
        if (!manualSpawn) {
            roomTickCount --;
            if (roomTickCount > 0) {nextNode();}
            else {
                Bukkit.getServer().getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    roomTickCount = spawnNodeQ.size();
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
            if (walls.containsKey(DungeonStructure.moveForward(loc, dir, 1)) || walls.containsKey(DungeonStructure.moveForward(loc, dir, -2))) { // flytter først fram 1 blocc, og så flytter tilbake 2 bloccer, først til midten og så til andre sida (så movelocation endrer locationen du putter inn)
                walls.put(loc, null); // den andre veggen fjernes, så basically den ene veggen blir jernbue istedet og den andre blir fjerna
                if (RANDOM.nextInt(4) == 0) { // 25% sjangs for at veggen forblir alikavel
                    wall.spawn();
                    wallCount++;
                    continue;
                }
                //Bukkit.broadcastMessage("2 adjacent walls was removed");
                loc.add(0, 2, 0); // jernbuer sitt startpkt er 2 blokker over bakken, i motsetning til vegger, der er shulkerboksene og dermed også startpktene på bakken
                ironArches.add(new SpawnFeature(dir, loc, features.get("iron_arch")));
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

    private static int getRandomExpansionRate() {
        // Sånn fungerer det:
        //  0 -  50 rom: (i begynnelsen) her ekspanderes det veldig mye
        // 50 - 200 rom: her skjer det i gjennomsnitt ingen ekspansjon
        //      200+rom: her genereres det lange ganger ut fra kjernen, og det er dobbelt så stor sjangs for at en gang slutter enn at den deler seg.
        //               dette er for å gjøre det lettere å finne dungeonen, ved at man kan finne en lang sidegang som fører inn til midten

        int expansionRate;
        if (roomCount < 50) {
            return RANDOM.nextInt(2) + 1; // 50% 1, 50% 2.
        } else if (roomCount < 200) {
            expansionRate = RANDOM.nextInt(4);
            if (expansionRate > 1) {
                expansionRate -= 1; // 25% 0, 50% 1, 25% 2.
            }
            if (spawnNodeQ.size() < 15 && expansionRate == 0) {
                return 2; // hvis det er for lite noder så ekspanderer vi
            } else if (spawnNodeQ.size() > 25 && expansionRate == 2) {
                return 0; // hvis det er for mange noder så reduserer vi
            }
        } else {
            expansionRate = RANDOM.nextInt(40);
            if (expansionRate < 2) {
                return 0; // 4% 0, 94% 1, 2% 2
            } else if (expansionRate == 39) {
                return 2;
            } else {
                return 1;
            }
        }
        return expansionRate;
    }

    public static void manualNextNode() {
        if (manualSpawn) {
            nextNode();
        }
    }

    public static void addNode(SpawnNode spawnNode) {
        spawnNodeQ.add(spawnNode);
    }

    public static void loadStructure(String structureName, Location location, Direction direction) {
        DungeonStructure dungeonStructure = namedRooms.get(structureName);

        if (dungeonStructure == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + structureName + "\" fins ikke!");
        }
        dungeonStructure.place(location, direction);
    }



}
