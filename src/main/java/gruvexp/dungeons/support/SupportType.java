package gruvexp.dungeons.support;

public enum SupportType {

    BRIDGE_PILLAR(6),
    BRIDGE_TOWER(6),
    CORRIDOR(8);

    public final int supportHeight;

    SupportType(int supportHeight) {
        this.supportHeight = supportHeight;
    }
}
