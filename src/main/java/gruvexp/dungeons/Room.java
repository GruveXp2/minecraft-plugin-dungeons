package gruvexp.dungeons;

public enum Room {
    // dungeon
    END(new DungeonStructure("dungeon", "end", RoomType.CATACOMB_WALKWAY), GrowRate.END),
    STRAIGHT(new DungeonStructure("dungeon", "straight", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    SKELETON_TRAP(new DungeonStructure("dungeon", "skeleton_trap", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC), // +1x pga må ha en blokk for å henge tripwire hook på
    ARROW_TRAP(new DungeonStructure("dungeon", "arrow_trap", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    TURN_R(new DungeonStructure("dungeon", "turn_r", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    TURN_L(new DungeonStructure("dungeon", "turn_l", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    T_SPLIT_R(new DungeonStructure("dungeon", "t_split_r", RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),
    T_SPLIT_M(new DungeonStructure("dungeon", "t_split_m", RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),
    T_SPLIT_L(new DungeonStructure("dungeon", "t_split_l", RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),

    // fortress
    FORTRESS_LAVAROOM(new DungeonStructure("fortress", "origin/lava_room", RoomType.ORIGIN), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_BOW_F(new DungeonStructure("fortress", "bridge/bow_f", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_F(new DungeonStructure("fortress", "bridge/f", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_END(new DungeonStructure("fortress", "bridge/end", RoomType.FORTRESS_BRIDGE), GrowRate.END),
    FORTRESS_BRIDGE_BOW_X(new DungeonStructure("fortress", "bridge/bow_x", RoomType.FORTRESS_BRIDGE), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_X(new DungeonStructure("fortress", "bridge/x", RoomType.FORTRESS_BRIDGE), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_BLAZE(new DungeonStructure("fortress", "bridge/blaze", RoomType.FORTRESS_BRIDGE), GrowRate.END),
    FORTRESS_BRIDGE_L(new DungeonStructure("fortress", "bridge/l", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_R(new DungeonStructure("fortress", "bridge/r", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_CORRIDOR_F(new DungeonStructure("fortress", "corridor/f", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_R(new DungeonStructure("fortress", "corridor/r", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_L(new DungeonStructure("fortress", "corridor/l", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_STAIRS(new DungeonStructure("fortress", "corridor/stairs", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_END_LAVA(new DungeonStructure("fortress", "corridor/end_lava", RoomType.FORTRESS_CORRIDOR), GrowRate.END),
    FORTRESS_CORRIDOR_END_BROKEN(new DungeonStructure("fortress", "corridor/end_broken", RoomType.FORTRESS_CORRIDOR), GrowRate.END),
    FORTRESS_CORRIDOR_X(new DungeonStructure("fortress", "corridor/x", RoomType.FORTRESS_CORRIDOR), GrowRate.EXPANDING);

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
