package gruvexp.dungeons;

import gruvexp.dungeons.commands.DungeonCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashSet;

public class ReservedSpace {
    private final HashSet<Direction> connections; // bruker bitmask
    private final Location loc;

    public ReservedSpace(Location loc) {
        this.loc = loc;
        connections = new HashSet<>();
    }

    public void connect(Direction direction) {
        connections.add(direction);
        if (!DungeonCommand.showreserved) return;
        switch (direction) {
            case N -> DungeonManager.spawnTextMarker(loc.clone().add(0, 0.25, 0), ChatColor.BLUE + "Reserved N", "reserved");
            case S -> DungeonManager.spawnTextMarker(loc.clone().add(0, 0.5, 0), ChatColor.RED + "Reserved S", "reserved");
            case E -> DungeonManager.spawnTextMarker(loc.clone().add(0, 0.75, 0), ChatColor.GOLD + "Reserved E", "reserved");
            case W -> DungeonManager.spawnTextMarker(loc.clone().add(0, 1, 0), ChatColor.AQUA + "Reserved W", "reserved");
            default -> DungeonManager.spawnTextMarker(loc.clone().add(0, 0.33, 0), ChatColor.DARK_RED + "Reserved bugs", "reserved");
        }
    }

    public boolean canConnect(Direction dir) { return connections.contains(dir); }

    public HashSet<Direction> getConnections() {
        return connections;
    }

    @Override
    public String toString() {
        return (canConnect(Direction.N) ? "N" : "") +
        (canConnect(Direction.S) ? "S" : "") +
        (canConnect(Direction.E) ? "E" : "") +
        (canConnect(Direction.W) ? "W" : "");
    }
}