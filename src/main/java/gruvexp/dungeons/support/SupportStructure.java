package gruvexp.dungeons.support;

import gruvexp.dungeons.dungeon.Dungeon;
import gruvexp.dungeons.DungeonManager;
import gruvexp.dungeons.location.Coord;
import gruvexp.dungeons.location.Direction;
import gruvexp.dungeons.location.RelativeDirection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;

public class SupportStructure extends gruvexp.dungeons.Structure {
    public final SupportType supportType;

    public SupportStructure(String structureGroup, String structureName, SupportType supportType) {
        super(structureGroup, structureName);
        this.supportType = supportType;
        Coord entry = null;
        for (Entity e : structure.getEntities()) {
            String[] name = ChatColor.stripColor(e.getName()).split(" ");
            String directionStr = name[0];
            if (directionStr.equals("Entry")) {
                Location vecEntry = e.getLocation();
                entry = new Coord(vecEntry.getBlockX(), vecEntry.getBlockY(), vecEntry.getBlockZ());
            }
        }
        if (entry == null) { // hvis man har glemt å lage en entry
            Bukkit.broadcast(Component.text("No entrypoint found for structure " + structureGroup + ":" + structureName, NamedTextColor.RED));
            entry = new Coord(3, 3, 3);
        }
        this.entry = entry;
    }

    public void place(Dungeon dungeon, Location loc, Direction dir) {
        dungeon.increaseStructureCount(this);
        //Bukkit.broadcast(Component.text("Placing support structure " + supportType + ": " + dir.name(), NamedTextColor.YELLOW));
        //Bukkit.broadcastMessage(String.format("Initial origin: %s, %s, %s", location.getX(), location.getY(), location.getZ()));
        //DungeonManager.spawnTextMarker(loc, ChatColor.AQUA + "start", "conflict");
        moveToOrigin(loc, dir);
        //DungeonManager.spawnTextMarker(loc, ChatColor.AQUA + "origin", "conflict");
        StructureRotation structureRotation = dir.toStructureRotation();
        //Bukkit.broadcastMessage(String.format("Origin moved to: %s, %s, %s (%s)", location.getX(), location.getY(), location.getZ(), dir));
        structure.place(loc, false, structureRotation, Mirror.NONE, 0, 1.0f, DungeonManager.RANDOM);
        for (Entity e : structure.getEntities()) {
            String[] name = ChatColor.stripColor(e.getName()).split(" ");
            String type = name[0];
            Location eLoc = e.getLocation();
            DungeonManager.rotateLocation(eLoc, dir);
            eLoc.setWorld(loc.getWorld());
            eLoc.add(loc); // relativ -> absolutt lokasjon
            switch (type) {
                case "Support" -> {
                    //spawnTextMarker(eLoc, e.getName());
                    String[] supportPart = name[1].split(":");
                    RelativeDirection supportRelDir = RelativeDirection.fromString(supportPart[1]);
                    Direction spawnNodeDir = dir.rotate(supportRelDir);
                    dungeon.addNode(new SupportNode(dungeon, eLoc, spawnNodeDir, SupportType.valueOf(supportPart[0])));
                }
                case "Entry" -> {}
                default -> {
                    Bukkit.broadcast(Component.text(type, NamedTextColor.GRAY));
                    DungeonManager.spawnTextMarker(eLoc, ChatColor.RED + e.getName(), "unknown_entity");}
            }
        }
    }
}
