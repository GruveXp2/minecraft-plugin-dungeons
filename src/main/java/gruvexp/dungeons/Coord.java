package gruvexp.dungeons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class Coord {

    public final int x;
    public final int y;
    public final int z;


    public Coord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coord(String x, String y, String z) {
        try {
            this.x = Integer.parseInt(x);
            this.y = Integer.parseInt(y);
            this.z = Integer.parseInt(z);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ChatColor.RED + x + " " + y + " " + z + " is not a valid position!");
        }
    }

    public Coord(double x, double y, double z) {
        try {
            this.x = (int) x;
            this.y = (int) y;
            this.z = (int) z;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ChatColor.RED + "" + x + " " + y + " " + z + " is not a valid position!");
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;

        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;

        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", x, y, z);
    }

    public Location toLocation(World world) {
        return new Location(world, x + 0.5, y, z + 0.5);
    }
}