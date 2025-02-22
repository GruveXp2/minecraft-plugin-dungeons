package gruvexp.dungeons;

import gruvexp.dungeons.location.Direction;
import org.bukkit.Location;

public class SpawnFeature {

    private final Direction dir;
    private final Location loc;
    private final DungeonFeature structure; // abcdefghijklmnopqrstuvwyzæøå

    public SpawnFeature(Direction dir, Location loc, Feature feature) {
        this.dir = dir;
        this.loc = loc;
        this.structure = feature.feature();
    }

    public void spawn(float integrity) {
        structure.place(loc, dir, integrity);
    }

    public void spawn() {
        spawn(1.0f);
    }

    public Direction getDirection() {
        return dir;
    }

    public Location getLocation() {
        return loc;
    }

}
