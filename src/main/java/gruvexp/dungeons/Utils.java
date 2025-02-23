package gruvexp.dungeons;

import gruvexp.dungeons.location.Coord;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class Utils {

    public static Location toLocation(String x, String y, String z) {
        try {
            return new Location(Main.WORLD, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ChatColor.RED + "" + x + " " + y + " " + z + " is not a valid position!");
        }
    }

    public static int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Must be a number");
        }
    }

    public static Coord getTargetBlock(Player player, int range) { // modified method by https://www.spigotmc.org/members/clip.1001/ that gets the block the player is looking at
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return new Coord(lastBlock.getX(), lastBlock.getY(), lastBlock.getZ());
    }

    public static String printLocation(Location loc) {
        return loc.getX() + " " + loc.getY() + " " + loc.getZ(); // + " - " + loc.getYaw() + " " + loc.getPitch();
    }
}
