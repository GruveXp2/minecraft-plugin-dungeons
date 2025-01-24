package gruvexp.dungeons.commands;

import gruvexp.dungeons.Direction;
import gruvexp.dungeons.DungeonManager;
import gruvexp.dungeons.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class DungeonCommand implements CommandExecutor { // /dungeon spawn 7945 4 3489 N 3
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { // /dungeon spawn xyz dir

        try {
            if (args.length == 0) {
                throw new IllegalArgumentException("not enough args");
            }
            String oper = args[0];
            switch (oper) {
                case "spawn" -> {
                    if (args.length < 5) {
                        throw new IllegalArgumentException("not enough args");
                    }
                    Location location = Utils.toLocation(args[1], args[2], args[3]);
                    String dir = args[4]; // retning
                    int size = 500; // default maks 500 romm
                    if (args.length == 6) {
                        size = Utils.toInt(args[5]);
                    }
                    // make the dungeon
                    DungeonManager.makeDungeon(location, Direction.fromString(dir), size);
                }
                case "manualspawn" -> {
                    if (args.length == 1) {
                        throw new IllegalArgumentException("not enough args");
                    }
                    String bool = args[1];
                    DungeonManager.manualSpawn = bool.equals("true");
                    sender.sendMessage("Changed manualspawn to " + DungeonManager.manualSpawn);
                }
                case "nextroom" -> DungeonManager.manualNextNode();
                case "printusedspaces" -> DungeonManager.usedSpaces.stream().sorted((loc1, loc2) -> { //WHAT!!!! DEN PRINTER UT LIKE USED SPACES FLER GANGER?! SÅ DA ER DE IKKE LIKE ALIKAVEL, FINN UT HVORFOR => => => (kanskje posisjonene som kommer fra shulkersane har forskjellig yaw & pitch?)
                    int compareByX = Double.compare(loc1.getX(), loc2.getX());
                    if (compareByX != 0) {
                        return compareByX; // Sort by X if not equal
                    } else {
                        // If X coordinates are the same, sort by Z
                        return Double.compare(loc1.getZ(), loc2.getZ());
                    }
                }).collect(Collectors.toList()).forEach(location -> sender.sendMessage(ChatColor.RED + Utils.printLocation(location)));
                default -> throw new IllegalArgumentException("wrong operation");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
