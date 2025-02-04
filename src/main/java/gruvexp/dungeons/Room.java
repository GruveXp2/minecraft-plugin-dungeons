package gruvexp.dungeons;

public enum Room {
    // dungeon
    END(new DungeonStructure("dungeon", "end", new Coord(2, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.END),
    STRAIGHT(new DungeonStructure("dungeon", "straight", new Coord(2, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    SKELETON_TRAP(new DungeonStructure("dungeon", "skeleton_trap", new Coord(3, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.STATIC), // +1x pga må ha en blokk for å henge tripwire hook på
    ARROW_TRAP(new DungeonStructure("dungeon", "arrow_trap", new Coord(3, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    TURN_R(new DungeonStructure("dungeon", "turn_r", new Coord(2, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    TURN_L(new DungeonStructure("dungeon", "turn_l", new Coord(2, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    T_SPLIT_R(new DungeonStructure("dungeon", "t_split_r", new Coord(2, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),
    T_SPLIT_M(new DungeonStructure("dungeon", "t_split_m", new Coord(2, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),
    T_SPLIT_L(new DungeonStructure("dungeon", "t_split_l", new Coord(2, 1, 0), RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),

    // fortress
    FORTRESS_BRIDGE_BOW_F(new DungeonStructure("fortress", "bridge/bow_f", new Coord(2, 6, 0), RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_F(new DungeonStructure("fortress", "bridge/f", new Coord(2, 5, 0), RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_END(new DungeonStructure("fortress", "bridge/end", new Coord(2, 5, 0), RoomType.FORTRESS_BRIDGE), GrowRate.END),
    FORTRESS_BRIDGE_BOW_INTER(new DungeonStructure("fortress", "bridge/bow_x", new Coord(10, 6, 0), RoomType.FORTRESS_BRIDGE), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_INTER(new DungeonStructure("fortress", "bridge/x", new Coord(3, 1, 0), RoomType.FORTRESS_BRIDGE), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_BLAZE(new DungeonStructure("fortress", "bridge/blaze", new Coord(3, 3, 0), RoomType.FORTRESS_BRIDGE), GrowRate.END),
    FORTRESS_BRIDGE_L(new DungeonStructure("fortress", "bridge/l", new Coord(3, 1, 0), RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_R(new DungeonStructure("fortress", "bridge/r", new Coord(3, 1, 0), RoomType.FORTRESS_BRIDGE), GrowRate.STATIC);

    private final DungeonStructure structure;
    public final GrowRate growRate;

    public RoomType roomType() {
        return structure.roomType;
    };

    Room(DungeonStructure structure, GrowRate growRate) {
        this.structure = structure;
        this.growRate = growRate;
    }

    public DungeonStructure structure() {
        return structure;
    }
}
