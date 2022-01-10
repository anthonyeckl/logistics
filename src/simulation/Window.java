package simulation;

import javax.swing.*;
import java.awt.*;

public class Window {

    public Window(int width, int height, String title, Sim simulation){
        JFrame frame = new JFrame(title);

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        //frame.setLocationRelativeTo(null);
        frame.setLocation(0, 0);
        frame.add(simulation);
        frame.setVisible(true);
        simulation.start();

    }

}
