package gruvexp.dungeons.location;

import org.bukkit.block.structure.StructureRotation;

public enum Direction {
    N, S, E, W, NS, EW, ANY;

    public static Direction fromString(String dirStr) {
        for (Direction direction : Direction.values()) {
            if (direction.name().equalsIgnoreCase(dirStr)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid direction: " + dirStr);
    }

    public StructureRotation toStructureRotation() {
        return switch (this) {
            case N -> StructureRotation.CLOCKWISE_180;
            case S, NS, ANY -> StructureRotation.NONE;
            case E -> StructureRotation.COUNTERCLOCKWISE_90;
            case W, EW -> StructureRotation.CLOCKWISE_90;
        };
    }

    public Direction rotate(RelativeDirection relDir) {
        return switch (relDir) {
            case FORWARD, ANY -> this;
            case BACKWARD -> switch (this) {
                case N -> S;
                case S -> N;
                case E -> W;
                case W -> E;
                default -> this;
            };
            case RIGHT -> switch (this) {
                case N -> E;
                case E -> S;
                case S -> W;
                case W -> N;
                case NS -> EW;
                case EW -> NS;
                case ANY -> this;
            };
            case LEFT -> switch (this) {
                case N -> W;
                case E -> N;
                case S -> E;
                case W -> S;
                case NS -> EW;
                case EW -> NS;
                case ANY -> this;
            };
            case FB -> switch (this) {
                case N -> S;
                case S -> N;
                default -> this;
            };
            case RL -> switch (this) {
                case E -> W;
                case W -> E;
                default -> this;
            };
        };
    }
}
