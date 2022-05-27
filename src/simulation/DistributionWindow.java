package simulation;

import simulation.Data.DBHandler;

import javax.swing.*;
import java.awt.*;

public class DistributionWindow extends JFrame {

    public static final int WINWIDTH = 800+16, WINHEIGHT = 800+38;
    public static final int CWIDTH = 800, CHEIGHT = 800;
    private DBHandler dbhandler;
    private DistributionCanvas canvas;

    public DistributionWindow(DBHandler dbHandler){
        super("Distribution Window");
        this.dbhandler = dbHandler;
        this.setPreferredSize(new Dimension(WINWIDTH, WINHEIGHT));
        this.setMaximumSize(new Dimension(WINWIDTH, WINHEIGHT));
        this.setMinimumSize(new Dimension(WINWIDTH, WINHEIGHT));

        this.setLocation(1920, 0);

        canvas = new DistributionCanvas(dbHandler);
        this.add(canvas);
        this.setVisible(true);


    }

    public int[] getDistribution(){
        return dbhandler.getOccupationfromTiles();
    }

    public void dWindowRender(){
        int[] tar = getDistribution();
        canvas.dcrender(CWIDTH, CHEIGHT, tar);
    }

}
