package gruvexp.dungeons;

public enum RoomType {

    ORIGIN(100),
    FORTRESS_BRIDGE(7),
    FORTRESS_CORRIDOR(5),
    CATACOMB_WALKWAY(5);

    public final int gridSize;


    RoomType(int gridSize) {
        this.gridSize = gridSize;
    }
}
