package simulation;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    JTextPane log;

    ArrayList<Color> logColors;

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

        logColors = new ArrayList<>();
        logColors.add(Color.RED);
        logColors.add(Color.BLACK);
        logColors.add(Color.GREEN);
        logColors.add(Color.GREEN);
        logColors.add(Color.BLACK);
        logColors.add(Color.BLUE);
        logColors.add(Color.BLACK);
        logColors.add(Color.ORANGE);

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

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        // cu 1
        JPanel controlUnit1 = new JPanel();
        controlUnit1.setBackground(new Color(255, 105, 105));
        JLabel cuLabel1 = new JLabel("Insert Item (ID):");
        controlUnit1.add(cuLabel1);
        JTextField cuText1 = new JTextField();
        cuText1.setPreferredSize(new Dimension(80,20));
        controlUnit1.add(cuText1);
        JButton cuButton1 = new JButton("Insert");
        controlUnit1.add(cuButton1);
        controlPanel.add(controlUnit1);

        cuButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = cuText1.getText();
                if(text.length() != 0 ){
                    organiser.insertItem(Integer.parseInt(text));
                }
                cuText1.setText("");
            }
        });

        // cu 2
        JPanel controlUnit2 = new JPanel();
        controlUnit2.setBackground(new Color(127, 255, 105));
        JLabel cuLabel2 = new JLabel("Order Items (1,45,3):");
        controlUnit2.add(cuLabel2);
        JTextField cuText2 = new JTextField();
        cuText2.setPreferredSize(new Dimension(80,20));
        controlUnit2.add(cuText2);
        JButton cuButton2 = new JButton("Order");
        controlUnit2.add(cuButton2);
        controlPanel.add(controlUnit2);

        cuButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = cuText2.getText();
                int[] items = Arrays.stream(text.split(",")).mapToInt(Integer::parseInt).toArray();
                if(text.length() != 0 ){
                    for (int i = 0; i < items.length; i++) {
                        System.out.println(items[i]);
                    }
                    organiser.addOrder(items);
                }
                cuText2.setText("");
            }
        });

        // cu 3
        JPanel controlUnit3 = new JPanel();
        controlUnit3.setBackground(new Color(105, 255, 185));
        JLabel cuLabel3 = new JLabel("Move top Box from (TileID):");
        controlUnit3.add(cuLabel3);
        JTextField cuText3 = new JTextField();
        cuText3.setPreferredSize(new Dimension(80,20));
        controlUnit3.add(cuText3);
        JLabel cuLabel3_2 = new JLabel("to");
        controlUnit3.add(cuLabel3_2);
        JTextField cuText3_2 = new JTextField();
        cuText3_2.setPreferredSize(new Dimension(80,20));
        controlUnit3.add(cuText3_2);
        JButton cuButton3 = new JButton("Move");
        controlUnit3.add(cuButton3);
        controlPanel.add(controlUnit3);

        this.add(controlPanel);

        // Level	Value	Used for
        //SEVERE	1000	Indicates some serious failure
        //WARNING	900	    Potential Problem
        //INFO	    800	    General Info
        //CONFIG	700	    Configuration Info
        //FINE	    500	    General developer info
        //FINER	    400	    Detailed developer info
        //FINEST	300	    Specialized Developer Info

        log = new JTextPane();
        JScrollPane pane = new JScrollPane(log);
        pane.setPreferredSize(new Dimension(400,200));
        this.add(pane);

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
        ArrayList<String> logMessagesList = new ArrayList<>(organiser.getLogMessages());
        for (int i = 0; i < logMessagesList.size(); i++) {
            appendToPane(log, logMessagesList.get(i) + "\n", logColors.get(Integer.parseInt(logMessagesList.get(i).charAt(0)+"")-1));
            organiser.removeFirstLogMessage();
        }
    }

    private static void appendToPane( JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        //int len = tp.getDocument().getLength();
        tp.setCaretPosition(0);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);

    }
}
