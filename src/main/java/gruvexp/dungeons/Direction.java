package gruvexp.dungeons;

public enum Direction {
    N, S, E, W, NS, EW, NSEW,
    FORWARD, BACKWARD, RIGHT, LEFT, FB, RL;

    public static Direction fromString(String dirStr) {
        for (Direction direction : Direction.values()) {
            if (direction.name().equalsIgnoreCase(dirStr)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid direction: " + dirStr);
    }
}
