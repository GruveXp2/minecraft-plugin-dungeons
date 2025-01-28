package gruvexp.dungeons;

import java.util.Set;

public enum Feature {
    // dungeon
    WALL(new DungeonFeature(Set.of("wall_1", "wall_2", "wall_3"), new Coord(1, 0, 0))),
    IRON_ARCH(new DungeonFeature(Set.of("iron_arch"), new Coord(1, 2, 0)));  // coords: plasserte shulkeren i taket så det blir 2 blokker opp i y retning i forhold til feks vegger

    private final DungeonFeature feature;

    Feature(DungeonFeature feature) {
        this.feature = feature;
    }

    public DungeonFeature feature() {
        return feature;
    }

}
