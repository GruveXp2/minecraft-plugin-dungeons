package gruvexp.dungeons;

public enum GrowRate {

    END(0), STATIC(1), EXPANDING(2);

    private final int spread;

    GrowRate(int spread) {
        this.spread = spread;
    }

    public int spread() {
        return spread;
    }
}
