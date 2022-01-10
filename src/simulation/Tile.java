package simulation;

import java.awt.*;

public class Tile extends SimObject{
    int number;
    public Tile(int x, int y, ID id, int number) {
        super(x, y, id);
        this.number = number;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
    }

    public int getNumber() {
        return number;
    }

}
