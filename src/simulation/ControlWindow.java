package simulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ControlWindow extends JFrame {

    public static final int WINWIDTH = 1100, WINHEIGHT = 800;
    private Sim simulation;
    private Organiser organiser;

    JList<Object> listTest;
    JList<Object> listTest2;
    JList<Object> listJobs;

    public ControlWindow(Sim simulation, Organiser organiser){
        // main window setup
        super("Control Window");
        this.simulation = simulation;
        this.organiser = organiser;
        this.setPreferredSize(new Dimension(WINWIDTH, WINHEIGHT));
        this.setMaximumSize(new Dimension(WINWIDTH, WINHEIGHT));
        this.setMinimumSize(new Dimension(WINWIDTH, WINHEIGHT));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(820, 0);
        this.setResizable(false);

        GridLayout winLayout = new GridLayout(1,5);
        this.setLayout(winLayout);

        // widgets setup
        //String list[] ={"one", "two", "three","one", "two", "three","one", "two", "three","one", "two", "three","one", "two", "three",};
        listTest = new JList<>();
        listTest.setBackground(Color.RED);
        listTest.setSize(new Dimension(100, WINHEIGHT));
        this.add(listTest);

        listTest2 = new JList<>();
        listTest2.setBackground(Color.BLUE);
        listTest2.setSize(new Dimension(100, WINHEIGHT));
        listTest2.setFixedCellWidth(100);
        listTest2.setLocation(100,0);
        this.add(listTest2);

        listJobs = new JList<>();
        listJobs.setBackground(Color.GREEN);
        listJobs.setSize(new Dimension(100, WINHEIGHT));
        listJobs.setFixedCellWidth(100);
        listJobs.setLocation(200,0);
        this.add(listJobs);

        this.setVisible(true);
    }

    // generating/updating list based on correspondig arrays
    public void setupList(){
        ArrayList<String> robotsInUseStringArrayList = new ArrayList<>();
        for (int i = 0; i < organiser.getInUse().size(); i++) {
            Robot tempR = organiser.getInUse().get(i);
            String robotString = "Num: " + tempR.getNumber() + ", x: " + tempR.getX() + ", y: " + tempR.getY() + ", T: " + tempR.getTask();
            robotsInUseStringArrayList.add(robotString);
        }
        listTest.setListData(robotsInUseStringArrayList.toArray());

        ArrayList<String> robotsInIdleStringArrayList = new ArrayList<>();
        for (int i = 0; i < organiser.getInIdle().size(); i++) {
            Robot tempR = organiser.getInIdle().get(i);
            String robotString = "Num: " + tempR.getNumber() + ", x: " + tempR.getX() + ", y: " + tempR.getY() + ", T: " + tempR.getTask();
            robotsInIdleStringArrayList.add(robotString);
        }
        listTest2.setListData(robotsInIdleStringArrayList.toArray());

        ArrayList<String> jobsPendingArrayList = new ArrayList<>();
        for (int i = 0; i < organiser.getJobsPending().size(); i++) {
            int[] tempJobPending = organiser.getJobsPending().get(i);
            String jobsPendingString = "FromX: " + tempJobPending[0] + ", FromY: " + tempJobPending[1] + ", ToX: " + tempJobPending[2] + ", ToY: " + tempJobPending[3];
            jobsPendingArrayList.add(jobsPendingString);
        }
        listJobs.setListData(jobsPendingArrayList.toArray());
    }
}
