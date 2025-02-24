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
            case FORWARD -> this;
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
                default -> this;
            };
            case LEFT -> switch (this) {
                case N -> W;
                case E -> N;
                case S -> E;
                case W -> S;
                case NS -> EW;
                case EW -> NS;
                default -> this;
            };
            case FB -> switch (this) {
                case N, S -> NS;
                case E, W -> EW;
                default -> this;
            };
            case RL -> switch (this) {
                case N, S, NS -> EW;
                case E, W, EW -> NS;
                default -> this;
            };
            case ANY -> ANY;
        };
    }

    public boolean isCompatible(Direction other) { // strukturen passer, men må kanskje roteres
        if (other == this || other == ANY) return true;
        return switch (this) {
            case N, S -> other == NS;
            case E, W -> other == EW;
            case NS -> other ==  N || other == S;
            case EW -> other ==  E || other == W;
            case ANY -> true;
        };
    }

    public boolean isFullyCompatible(Direction other) { // strukturen passer og trenger ikke å roteres
        if (other == this || other == ANY) return true;
        return switch (this) {
            case N, S -> other == NS;
            case E, W -> other == EW;
            case NS -> other ==  N;
            case EW -> other ==  E;
            case ANY -> true;
        };
    }

    public Direction getSpecifiedDirection(boolean flipped) { // hvis det er riktig struktur men må roteres, finn ut åssen vei
        return switch (this) {
            case N, S, E, W, ANY -> this;
            case NS -> flipped ? S : N;
            case EW -> flipped ? W : E;
        };
    }
}
