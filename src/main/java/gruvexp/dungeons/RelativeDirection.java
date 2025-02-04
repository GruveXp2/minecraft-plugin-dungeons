package gruvexp.dungeons;

public enum RelativeDirection {

    FORWARD, BACKWARD, RIGHT, LEFT, FB, RL;

    public static RelativeDirection fromString(String dirStr) {
        for (RelativeDirection direction : RelativeDirection.values()) {
            if (direction.name().equalsIgnoreCase(dirStr)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid direction: " + dirStr);
    }

    public Direction toAbsoluteDirection() {
        return switch (this) {
            case FORWARD -> Direction.S;
            case RIGHT -> Direction.W;
            case LEFT -> Direction.E;
            case BACKWARD -> Direction.N;
            case FB -> Direction.NS;
            case RL -> Direction.EW;
        };
    }
}
