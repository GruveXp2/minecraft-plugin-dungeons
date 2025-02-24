package gruvexp.dungeons;

import gruvexp.dungeons.location.Coord;
import gruvexp.dungeons.location.Direction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

public class Structure {
    protected final org.bukkit.structure.Structure structure;
    protected Coord entry;
    public final String name;

    public Structure(String structureGroup, String structureName) {
        this.name = structureName;
        structure = DungeonManager.STRUCTURE_MANAGER.loadStructure(new NamespacedKey(structureGroup, structureName));
        if (structure == null) {
            throw new IllegalArgumentException(ChatColor.RED + "strukturen \"" + structureGroup + ":" + structureName + "\" fins ikke!");
        }
    }

    protected void moveToOrigin(Location loc, Direction dir) {
        // lokasjonen må justeres basert på hvor innganga er i neste rom, sånn at det neste rommet spawner på en plass sånn at innganga akkurat passer med utanga til den forrige strukturen
        // så den flyttes til ett av hjørnene av strukturen som er det hjørnet med lavest xyz verdi i library verdenen
        switch (dir) {
            case N -> loc.add( entry.x, -entry.y,  entry.z);
            case S, NS, ANY -> loc.add(-entry.x, -entry.y, -entry.z);
            case E, EW -> loc.add(-entry.z, -entry.y,  entry.x);
            case W -> loc.add( entry.z, -entry.y, -entry.x);
        }
    }
}
