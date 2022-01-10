package main_game;

import java.awt.*;

public class Player extends GameObject{

    public Player(int x, int y, ID id){
        super(x, y, id);

        //velX = 1;
        //velY = 1;
    }

    public void tick() {
        x += velX;
        y += velY;

        x = Game.clamp(x, 0, Game.WIDTH - 50);
        y = Game.clamp(y, 0, Game.HEIGHT - 70);
    }

    public void render(Graphics g) {
        if (this.id == ID.Player)
            g.setColor(Color.WHITE);
        if (this.id == ID.Player2)
            g.setColor(Color.BLUE);
        g.fillRect(x, y, 32, 32);
    }
}
