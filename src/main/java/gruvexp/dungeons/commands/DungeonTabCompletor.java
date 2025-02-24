package gruvexp.dungeons.commands;

import gruvexp.dungeons.location.Coord;
import gruvexp.dungeons.room.Room;
import gruvexp.dungeons.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DungeonTabCompletor implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        Player p = (Player) sender;

        if (args.length == 1) {
            return List.of("spawn", "info", "manualspawn", "quickspawn", "nextroom", "printusedspaces");
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
                    Set<String> suggestions = getRoomSuggestions(args);

                    return new ArrayList<>(suggestions);
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

    private static @NotNull Set<String> getRoomSuggestions(String[] args) {
        String current = args[args.length - 1].toLowerCase();

        Set<String> suggestions = new HashSet<>();

        for (Room room : Room.values()) {
            String name = room.name().toLowerCase();
            String[] parts = name.split("_");

            if (parts.length > 1) {
                String prefix = parts[0];

                if (current.isEmpty() || prefix.startsWith(current)) {
                    suggestions.add(prefix); // Foreslå bare det første nivået
                } else if (name.startsWith(current)) {
                    if (current.contains(parts[1])) {
                        suggestions.add(name);
                    } else {
                        suggestions.add(prefix + "_" + parts[1]); // Foreslå neste nivå
                    }
                }
            } else {
                suggestions.add(name); // Foreslå de som ikke har "_"
            }
        }
        return suggestions;
    }
}
