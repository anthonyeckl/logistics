package simulation;

import org.sqlite.core.DB;
import simulation.Data.DBHandler;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

public class DistributionCanvas extends Canvas {

    private ArrayList<Color> colorHues = new ArrayList<>();
    private DBHandler dbHandler;
    public DistributionCanvas(DBHandler dbHandler){
        this.dbHandler = dbHandler;
        colorHues.add(new Color(36, 248, 255));
        colorHues.add(new Color(0, 191, 30));
        colorHues.add(new Color(57, 144, 22));
        colorHues.add(new Color(114, 98, 15));
        colorHues.add(new Color(171, 52, 7));
        colorHues.add(new Color(229, 6, 0));
    }

    public void dcrender(int w, int h, int[] dis){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        // Gray background
        g.setColor(Color.lightGray);
        g.fillRect(0,0, w, h);

        // color distribution Tiles
        for (int i = 0; i < 6400; i++) {
            int[] c = Sim.calcTileCords(i);
            g.setColor(colorHues.get(dis[i]));
            g.fillRect(c[0],c[1], 10, 10);
        }

        g.dispose();
        bs.show();
    }
}
