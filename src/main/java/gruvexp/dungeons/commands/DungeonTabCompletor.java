package gruvexp.dungeons.commands;

import gruvexp.dungeons.Coord;
import gruvexp.dungeons.Room;
import gruvexp.dungeons.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DungeonTabCompletor implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        Player p = (Player) sender;

        if (args.length == 1) {
            return List.of("spawn", "info", "manualspawn", "nextroom", "printusedspaces");
        }
        String oper = args[0];
        try {
            switch (oper) {
                case "spawn" -> {
                    if (args.length == 2) {
                        return List.of("fortress");
                    }
                    if (args.length <= 5) {
                        Coord loc = Utils.getTargetBlock(p, 10);
                        return List.of(loc.toString());
                    }
                    if (args.length == 6) {
                        return List.of("N", "E", "S", "W");
                    }
                    if (args.length == 7) {
                        return List.of("<max rooms>");
                    }
                    return new ArrayList<>(0);
                }
                case "manualspawn" -> {
                    if (args.length == 2) {
                        return List.of("true", "false");
                    }
                    return new ArrayList<>(0);
                }
                case "info" -> {
                    return List.of("showreserved", "showdircheck", "strucon", "exitnodchk", "usedspace");
                }
                case "nextroom" -> {
                    return Arrays.stream(Room.values()).map(room -> room.name().toLowerCase()).collect(Collectors.toCollection(ArrayList::new));
                }
                case "printusedspaces" -> {
                    return new ArrayList<>(0);
                }
                default -> throw new IllegalArgumentException("invalid operation");
            }
        } catch (IllegalArgumentException e) {
            return List.of(ChatColor.RED + e.getMessage());
        }
    }
}
