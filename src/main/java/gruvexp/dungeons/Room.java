package gruvexp.dungeons;

public enum Room {
    // dungeon
    END(new DungeonStructure("end", new Coord(2, 1, 0))),
    STRAIGHT(new DungeonStructure("straight", new Coord(2, 1, 0))),
    SKELETON_TRAP(new DungeonStructure("skeleton_trap", new Coord(3, 1, 0))), // +1x pga må ha en blokk for å henge tripwire hook på
    ARROW_TRAP(new DungeonStructure("arrow_trap", new Coord(3, 1, 0))),
    TURN_R(new DungeonStructure("turn_r", new Coord(2, 1, 0))),
    TURN_L(new DungeonStructure("turn_l", new Coord(2, 1, 0))),
    T_SPLIT_R(new DungeonStructure("t_split_r", new Coord(2, 1, 0))),
    T_SPLIT_M(new DungeonStructure("t_split_m", new Coord(2, 1, 0))),
    T_SPLIT_L(new DungeonStructure("t_split_l", new Coord(2, 1, 0)));

    // fortress

    private final DungeonStructure structure;

    Room(DungeonStructure structure) {
        this.structure = structure;
    }

    public DungeonStructure structure() {
        return structure;
    }
}
