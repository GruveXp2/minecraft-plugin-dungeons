package gruvexp.dungeons;

public enum Room {
    // dungeon
    END(new DungeonStructure("end", new Coord(2, 1, 0)), GrowRate.SHRINKING, RoomType.CATACOMB_WALKWAY),
    STRAIGHT(new DungeonStructure("straight", new Coord(2, 1, 0)), GrowRate.STATIC, RoomType.CATACOMB_WALKWAY),
    SKELETON_TRAP(new DungeonStructure("skeleton_trap", new Coord(3, 1, 0)), GrowRate.STATIC, RoomType.CATACOMB_WALKWAY), // +1x pga må ha en blokk for å henge tripwire hook på
    ARROW_TRAP(new DungeonStructure("arrow_trap", new Coord(3, 1, 0)), GrowRate.STATIC, RoomType.CATACOMB_WALKWAY),
    TURN_R(new DungeonStructure("turn_r", new Coord(2, 1, 0)), GrowRate.STATIC, RoomType.CATACOMB_WALKWAY),
    TURN_L(new DungeonStructure("turn_l", new Coord(2, 1, 0)), GrowRate.STATIC, RoomType.CATACOMB_WALKWAY),
    T_SPLIT_R(new DungeonStructure("t_split_r", new Coord(2, 1, 0)), GrowRate.EXPANDING, RoomType.CATACOMB_WALKWAY),
    T_SPLIT_M(new DungeonStructure("t_split_m", new Coord(2, 1, 0)), GrowRate.EXPANDING, RoomType.CATACOMB_WALKWAY),
    T_SPLIT_L(new DungeonStructure("t_split_l", new Coord(2, 1, 0)), GrowRate.EXPANDING, RoomType.CATACOMB_WALKWAY),

    // fortress
    FORTRESS_BRIDGE_STRAIGHT(new DungeonStructure("fortress/bridge_straight", new Coord(2, 1, 0)), GrowRate.STATIC, RoomType.FORTRESS_BRIDGE),
    FORTRESS_BRIDGE_STRAIGHT_POLE(new DungeonStructure("fortress/bridge_straight_pole", new Coord(2, 1, 0)), GrowRate.STATIC, RoomType.FORTRESS_BRIDGE),
    FORTRESS_BRIDGE_END(new DungeonStructure("fortress/bridge_end", new Coord(2, 1, 0)), GrowRate.SHRINKING, RoomType.FORTRESS_BRIDGE),
    FORTRESS_BRIDGE_INTER_OPEN(new DungeonStructure("fortress/bridge_t", new Coord(10, 1, 0)), GrowRate.EXPANDING, RoomType.FORTRESS_BRIDGE),
    FORTRESS_BRIDGE_INTER_CLOSED(new DungeonStructure("fortress/bridge_x", new Coord(2, 1, 0)), GrowRate.EXPANDING, RoomType.FORTRESS_BRIDGE),
    FORTRESS_BRIDGE_BLAZE(new DungeonStructure("fortress/bridge_blaze", new Coord(2, 6, 0)), GrowRate.STATIC, RoomType.FORTRESS_BRIDGE),
    FORTRESS_BRIDGE_L(new DungeonStructure("fortress/bridge_l", new Coord(2, 1, 0)), GrowRate.STATIC, RoomType.FORTRESS_BRIDGE),
    FORTRESS_BRIDGE_R(new DungeonStructure("fortress/bridge_r", new Coord(2, 1, 0)), GrowRate.STATIC, RoomType.FORTRESS_BRIDGE);

    private final DungeonStructure structure;
    public final GrowRate growRate;
    public final RoomType roomType;

    Room(DungeonStructure structure, GrowRate growRate, RoomType roomType) {
        this.structure = structure;
        this.growRate = growRate;
        this.roomType = roomType;
    }

    public DungeonStructure structure() {
        return structure;
    }
}
