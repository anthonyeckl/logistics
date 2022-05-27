package simulation;

public class Box {
    private int id;
    private int level;
    private int tileId;

    public Box(int id, int level, int tileId){
        this.id = id;
        this.level = level;
        this.tileId = tileId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTileId() {
        return tileId;
    }

    public void setTileId(int tileId) {
        this.tileId = tileId;
    }
}
