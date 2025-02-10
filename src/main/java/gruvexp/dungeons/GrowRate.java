package gruvexp.dungeons;

import java.util.Random;

public enum GrowRate {

    END(0), STATIC(1), EXPANDING(2);

    private final int spread;

    GrowRate(int spread) {
        this.spread = spread;
    }

    public int spread() {
        return spread;
    }

    public static GrowRate getWeighted(Random random, int endChance, int staticChance, int expandingChance) {
        int total = endChance + staticChance + expandingChance;
        int roll = random.nextInt(total);
        if (roll < endChance) return END;
        if (roll < endChance + staticChance) return STATIC;
        return EXPANDING;
    }
}
