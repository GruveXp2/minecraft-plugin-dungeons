package gruvexp.dungeons.location;

public enum RelativeDirection {

    FORWARD, BACKWARD, RIGHT, LEFT, FB, RL, ANY;

    public static RelativeDirection fromString(String dirStr) {
        for (RelativeDirection direction : RelativeDirection.values()) {
            if (direction.name().equalsIgnoreCase(dirStr)) {
                return direction;
            }
        }
        return switch (dirStr.toUpperCase()) {
            case "F" -> FORWARD;
            case "R" -> RIGHT;
            case "L" -> LEFT;
            case "B" -> BACKWARD;
            default -> throw new IllegalArgumentException("Invalid direction: " + dirStr);
        };
    }

    public Direction toAbsoluteDirection() {
        return switch (this) {
            case FORWARD -> Direction.S;
            case RIGHT -> Direction.W;
            case LEFT -> Direction.E;
            case BACKWARD -> Direction.N;
            case FB -> Direction.NS;
            case RL -> Direction.EW;
            case ANY -> Direction.ANY;
        };
    }
}
