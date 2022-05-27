package simulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ControlWindow extends JFrame {

    public static final int WINWIDTH = 1100, WINHEIGHT = 800;
    private Sim simulation;
    private Organiser organiser;

    JList<Object> listUsedRobots;
    JList<Object> listIdleRobots;
    JList<Object> listJobs;
    JList<Object> listOrdersOpen;
    JList<Object> listOrdersPending;

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

        //GridLayout winLayout = new GridLayout(2,1);
        this.setLayout( new FlowLayout(FlowLayout.CENTER, 10, 10));

        // widgets setup
        int textFieldWidth = 200;

        //String list[] ={"one", "two", "three","one", "two", "three","one", "two", "three","one", "two", "three","one", "two", "three",};
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        JLabel title1 = new JLabel();
        title1.setText("Used Robots");
        panel1.add(title1, BorderLayout.NORTH);

        listUsedRobots = new JList<>();
        listUsedRobots.setBackground(Color.RED);
        listUsedRobots.setPreferredSize(new Dimension(textFieldWidth,400));
        panel1.add(listUsedRobots);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        JLabel title2 = new JLabel();
        title2.setText("Idle Robots");
        panel2.add(title2, BorderLayout.NORTH);

        listIdleRobots = new JList<>();
        listIdleRobots.setBackground(Color.BLUE);
        listIdleRobots.setPreferredSize(new Dimension(textFieldWidth,400));
        panel2.add(listIdleRobots);

        JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());
        JLabel title3 = new JLabel();
        title3.setText("Jobs");
        panel3.add(title3, BorderLayout.NORTH);

        listJobs = new JList<>();
        listJobs.setBackground(Color.GREEN);
        listJobs.setPreferredSize(new Dimension(textFieldWidth,400));
        panel3.add(listJobs);

        JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout());
        JLabel title4 = new JLabel();
        title4.setText("Open Orders");
        panel4.add(title4, BorderLayout.NORTH);

        listOrdersOpen = new JList<>();
        listOrdersOpen.setBackground(Color.YELLOW);
        listOrdersOpen.setPreferredSize(new Dimension(textFieldWidth,400));
        panel4.add(listOrdersOpen);

        JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout());
        JLabel title5 = new JLabel();
        title5.setText("Pending Open");
        panel5.add(title5, BorderLayout.NORTH);

        listOrdersPending = new JList<>();
        listOrdersPending.setBackground(Color.MAGENTA);
        listOrdersPending.setPreferredSize(new Dimension(textFieldWidth,400));
        panel5.add(listOrdersPending);

        this.add(panel1);
        this.add(panel2);
        this.add(panel3);
        this.add(panel4);
        this.add(panel5);

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
        listUsedRobots.setListData(robotsInUseStringArrayList.toArray());

        ArrayList<String> robotsInIdleStringArrayList = new ArrayList<>();
        for (int i = 0; i < organiser.getInIdle().size(); i++) {
            Robot tempR = organiser.getInIdle().get(i);
            String robotString = "Num: " + tempR.getNumber() + ", x: " + tempR.getX() + ", y: " + tempR.getY() + ", T: " + tempR.getTask();
            robotsInIdleStringArrayList.add(robotString);
        }
        listIdleRobots.setListData(robotsInIdleStringArrayList.toArray());

        ArrayList<String> jobsPendingArrayList = new ArrayList<>();
        for (int i = 0; i < organiser.getJobsPending().size(); i++) {
            int[] tempJobPending = organiser.getJobsPending().get(i);
            String jobsPendingString = "FromX: " + tempJobPending[0] + ", FromY: " + tempJobPending[1] + ", ToX: " + tempJobPending[2] + ", ToY: " + tempJobPending[3];
            jobsPendingArrayList.add(jobsPendingString);
        }
        listJobs.setListData(jobsPendingArrayList.toArray());


        ArrayList<String> ordersOpenArrayList = new ArrayList<>();
        for (int i = 0; i < organiser.getOrderList().size(); i++) {
            int[] tempOrderArray = organiser.getOrderList().get(i);
            ordersOpenArrayList.add(Arrays.toString(tempOrderArray));
        }
        listOrdersOpen.setListData(ordersOpenArrayList.toArray());
        /*
        ArrayList<String> ordersPendingArrayList = new ArrayList<>();
        for (int i = 0; i < inventoryManager.getOrderListPending().size(); i++) {
            int[] tempOrderPendingArray = inventoryManager.getOrderListPending().get(i);
            ordersPendingArrayList.add(Arrays.toString(tempOrderPendingArray));
        }
        listOrdersPending.setListData(ordersPendingArrayList.toArray());

         */
    }
}
