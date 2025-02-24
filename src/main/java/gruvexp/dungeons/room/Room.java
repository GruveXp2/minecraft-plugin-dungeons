package gruvexp.dungeons.room;

import gruvexp.dungeons.GrowRate;

public enum Room {
    // dungeon
    DUNGEON_END(new RoomStructure("dungeon", "end", RoomType.CATACOMB_WALKWAY), GrowRate.END),
    DUNGEON_STRAIGHT(new RoomStructure("dungeon", "straight", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    DUNGEON_SKELETON_TRAP(new RoomStructure("dungeon", "skeleton_trap", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC), // +1x pga må ha en blokk for å henge tripwire hook på
    DUNGEON_ARROW_TRAP(new RoomStructure("dungeon", "arrow_trap", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    DUNGEON_TURN_R(new RoomStructure("dungeon", "turn_r", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    DUNGEON_TURN_L(new RoomStructure("dungeon", "turn_l", RoomType.CATACOMB_WALKWAY), GrowRate.STATIC),
    DUNGEON_T_SPLIT_R(new RoomStructure("dungeon", "t_split_r", RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),
    DUNGEON_T_SPLIT_M(new RoomStructure("dungeon", "t_split_m", RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),
    DUNGEON_T_SPLIT_L(new RoomStructure("dungeon", "t_split_l", RoomType.CATACOMB_WALKWAY), GrowRate.EXPANDING),

    // fortress
    FORTRESS_LAVAROOM(new RoomStructure("fortress", "origin/lava_room", RoomType.ORIGIN), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_BOW_F(new RoomStructure("fortress", "bridge/bow_f", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_BOW_X(new RoomStructure("fortress", "bridge/bow_x", RoomType.FORTRESS_BRIDGE), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_BOW_END(new RoomStructure("fortress", "bridge/bow_end", RoomType.FORTRESS_BRIDGE), GrowRate.END),
    FORTRESS_BRIDGE_PILLAR_F(new RoomStructure("fortress", "bridge/pillar_f", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_PILLAR_END(new RoomStructure("fortress", "bridge/pillar_end", RoomType.FORTRESS_BRIDGE), GrowRate.END),
    FORTRESS_BRIDGE_TOWER_F(new RoomStructure("fortress", "bridge/tower_f", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_TOWER_L(new RoomStructure("fortress", "bridge/tower_l", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_TOWER_R(new RoomStructure("fortress", "bridge/tower_r", RoomType.FORTRESS_BRIDGE), GrowRate.STATIC),
    FORTRESS_BRIDGE_TOWER_X(new RoomStructure("fortress", "bridge/tower_x", RoomType.FORTRESS_BRIDGE), GrowRate.EXPANDING),
    FORTRESS_BRIDGE_BLAZE(new RoomStructure("fortress", "bridge/blaze", RoomType.FORTRESS_BRIDGE), GrowRate.END),

    TEST_F(new RoomStructure("fortress", "test/support_f", RoomType.FORTRESS_BRIDGE), GrowRate.END),
    TEST_R(new RoomStructure("fortress", "test/support_r", RoomType.FORTRESS_BRIDGE), GrowRate.END),
    TEST_A(new RoomStructure("fortress", "test/support_a", RoomType.FORTRESS_BRIDGE), GrowRate.END),

    FORTRESS_CORRIDOR_F(new RoomStructure("fortress", "corridor/f", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_R(new RoomStructure("fortress", "corridor/r", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_L(new RoomStructure("fortress", "corridor/l", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_STAIRS(new RoomStructure("fortress", "corridor/stairs", RoomType.FORTRESS_CORRIDOR), GrowRate.STATIC),
    FORTRESS_CORRIDOR_END_LAVA(new RoomStructure("fortress", "corridor/end_lava", RoomType.FORTRESS_CORRIDOR), GrowRate.END),
    FORTRESS_CORRIDOR_END_BROKEN(new RoomStructure("fortress", "corridor/end_broken", RoomType.FORTRESS_CORRIDOR), GrowRate.END),
    FORTRESS_CORRIDOR_T(new RoomStructure("fortress", "corridor/t", RoomType.FORTRESS_CORRIDOR), GrowRate.EXPANDING),
    FORTRESS_CORRIDOR_X(new RoomStructure("fortress", "corridor/x", RoomType.FORTRESS_CORRIDOR), GrowRate.EXPANDING),
    FORTRESS_CORRIDOR_WART(new RoomStructure("fortress", "corridor/wart", RoomType.FORTRESS_CORRIDOR), GrowRate.EXPANDING);

    private final RoomStructure structure;
    public final GrowRate growRate;

    Room(RoomStructure structure, GrowRate growRate) {
        this.structure = structure;
        this.growRate = growRate;
    }

    public RoomStructure structure() {
        return structure;
    }
}
