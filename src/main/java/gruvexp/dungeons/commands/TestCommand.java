package gruvexp.dungeons.commands;

import gruvexp.dungeons.location.Direction;
import gruvexp.dungeons.DungeonManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        Player p = (Player) sender;


        if (args.length < 5) {
            p.sendMessage("4 args som er struc file navnet som er lagra i dungeonplugin/<filnavn> og så xyz");
            return true;
        }
        try {
            String structureName = args[0];
            Location location = new Location(p.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
            String dir = args[4]; // retning
            DungeonManager.loadStructure(structureName, location, Direction.fromString(dir));
        } catch (IllegalArgumentException e) {
            p.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
