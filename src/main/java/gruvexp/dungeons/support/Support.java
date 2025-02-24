package gruvexp.dungeons.support;

public enum Support {

    // Fortress
    BRIDGE_TOWER_FILLED(new SupportStructure("fortress", "support/tower_filled", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_STAIRS(new SupportStructure("fortress", "support/tower_stairs", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_F(new SupportStructure("fortress", "support/tower_f", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_BOW_F(new SupportStructure("fortress", "support/tower_bow_f", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_BOW_X(new SupportStructure("fortress", "support/tower_bow_x", SupportType.BRIDGE_TOWER)),
    PLACEHOLDER(new SupportStructure("fortress", "support/placeholder", SupportType.BRIDGE_TOWER)),

    PILLAR_FILLED(new SupportStructure("fortress", "support/pillar_filled", SupportType.BRIDGE_PILLAR)),
    PILLAR_BOW_F_FB(new SupportStructure("fortress", "support/pillar_bow_f_fb", SupportType.BRIDGE_PILLAR)),
    PILLAR_BOW_F_RL(new SupportStructure("fortress", "support/pillar_bow_f_rl", SupportType.BRIDGE_PILLAR)),
    PILLAR_BOW_X(new SupportStructure("fortress", "support/pillar_bow_x", SupportType.BRIDGE_PILLAR)),
    PILLAR_TOWER_F_FB(new SupportStructure("fortress", "support/pillar_tower_f_fb", SupportType.BRIDGE_PILLAR)),
    PILLAR_TOWER_F_RL(new SupportStructure("fortress", "support/pillar_tower_f_rl", SupportType.BRIDGE_PILLAR)),
    PILLAR_TOWER_STAIRS_F(new SupportStructure("fortress", "support/pillar_tower_stairs_f", SupportType.BRIDGE_PILLAR)),
    PILLAR_TOWER_STAIRS_R(new SupportStructure("fortress", "support/pillar_tower_stairs_r", SupportType.BRIDGE_PILLAR)),

    CORRIDOR_FILLED(new SupportStructure("fortress", "support/corridor_filled", SupportType.CORRIDOR));

    private final SupportStructure structure;

    Support(SupportStructure structure) {
        this.structure = structure;
    }

    public SupportStructure structure() {
        return structure;
    }

}
