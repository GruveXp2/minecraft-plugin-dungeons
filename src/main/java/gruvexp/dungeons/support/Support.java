package gruvexp.dungeons.support;

public enum Support {

    // Fortress
    BRIDGE_TOWER_FILLED(new SupportStructure("fortress", "support/tower_filled", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_STAIRS(new SupportStructure("fortress", "support/tower_stairs", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_F(new SupportStructure("fortress", "support/tower_f", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_BOW_F(new SupportStructure("fortress", "support/tower_bow_f", SupportType.BRIDGE_TOWER)),
    BRIDGE_TOWER_BOW_X(new SupportStructure("fortress", "support/tower_bow_x", SupportType.BRIDGE_TOWER)),
    PLACEHOLDER(new SupportStructure("fortress", "support/placeholder", SupportType.BRIDGE_TOWER)),
    BRIDGE_POLE(new SupportStructure("fortress", "support/pole_f", SupportType.BRIDGE_PILLAR));

    private final SupportStructure structure;

    Support(SupportStructure structure) {
        this.structure = structure;
    }

    public SupportStructure structure() {
        return structure;
    }

}
