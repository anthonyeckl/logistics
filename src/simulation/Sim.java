package simulation;

import simulation.Data.DBHandler;
import simulation.Window;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Random;
/*
IDEAS:
- fill with items (mabye boxes)
- add additional inserters and collectors
    - items of one order head to the same collector
- distribution? equally sread? near collectors?
 */

public class Sim extends Canvas implements Runnable {
    //public static final int WIDTH = 640, HEIGHT = WIDTH / 12 *9;
    public static final int WIDTH = 800, HEIGHT = 800;

    private Thread thread;
    private boolean running = false;

    private Random r;
    private SimHandler simulationhandler;
    ControlWindow cWindow;
    DistributionWindow dWindow;
    Organiser organiser;
    DBHandler dbHandler;
    Thread dWinThread;

    private long startTime = System.currentTimeMillis();

    public Sim(){
        // backend
        simulationhandler = new SimHandler();
        dbHandler = new DBHandler();
        organiser = new Organiser(dbHandler);

        // HELPER generatiung random orders
        /*
        for (int i = 0; i < 10; i++) {
            organiser.addOrder(orderGenerator());
        }

         */
        //organiser.addOrder(new int[]{5});

        // window setup
        new Window(WIDTH+16, HEIGHT+38, "Logistics Simulation", this);
        cWindow = new ControlWindow(this, organiser);

        dWinThread = new DistributionThread().setDBhandler(dbHandler);
        dWinThread.start();

        setupTiles();

        //organiser.addOrder(new int[]{6});
        //organiser.addJobPending(new int[]{70,0,20,0,9});

        // TESTING PURPOSE manual robot initiation
        Robot r = new Robot(100, 200, ID.Robot, 4, organiser, dbHandler);
        Robot r2 = new Robot(100, 100, ID.Robot, 5, organiser, dbHandler);
        Robot r3 = new Robot(130, 80, ID.Robot, 6 , organiser, dbHandler);
        Robot r4 = new Robot(700, 0, ID.Robot, 7 , organiser, dbHandler);
        Robot r5 = new Robot(520, 0, ID.Robot, 8 , organiser, dbHandler);
        simulationhandler.addRobot(r);
        simulationhandler.addRobot(r2);
        simulationhandler.addRobot(r3);
        simulationhandler.addRobot(r4);
        simulationhandler.addRobot(r5);

        organiser.addInIdle(r);
        organiser.addInIdle(r2);
        organiser.addInIdle(r3);
        organiser.addInIdle(r4);
        organiser.addInIdle(r5);

        //simulationhandler.getRobot().get(0).newRoute(700, 700, simulationhandler.getRobot().get(0).getX(), simulationhandler.getRobot().get(0).getY());
        //simulationhandler.getRobot().get(1).newRoute(700, 300, simulationhandler.getRobot().get(1).getX(), simulationhandler.getRobot().get(1).getY());
        //simulationhandler.getRobot().get(2).newRoute(200, 700, simulationhandler.getRobot().get(2).getX(), simulationhandler.getRobot().get(2).getY());
        //simulationhandler.getRobot().get(3).newRoute(300, 0, simulationhandler.getRobot().get(3).getX(), simulationhandler.getRobot().get(3).getY());

        organiser.parseIdleRobots();
        organiser.clearInserterAndCollectorTiles();
        cWindow.setupList();

        //organiser.insertItem(44);

    }

    // Initiating all Tile objects
    public void setupTiles(){
        for (int i = 0; i < HEIGHT/10; i++) {
            for (int j = 0; j < WIDTH/10; j++) {
                int num = i * 80 + j;
                Tile t = new Tile(i*10,j*10, ID.Tile, num);
                //simulationhandler.addTile(t);
                // ONLY SETUP PURPOSE !!! dbHandler.addTile(num);
                //for (int k = 0; k < 3; k++) {
                    //inventoryManager.newBox(k, num);
                //}
            }

        }
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop(){
        try{
            thread.join();
            dWinThread.join();
            running = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running)
                render();
            frames++;
            // gets executed every SECOND
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS:" + frames);
                frames = 0;
                // update Tasks and Control Window
                organiser.parseIdleRobots();
                organiser.processOrders();
                organiser.clearInserterAndCollectorTiles();
                cWindow.setupList();
            }
        }
        stop();
    }

    private void tick(){
        simulationhandler.tick();
    }

    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        // Gray background
        g.setColor(Color.lightGray);
        g.fillRect(0,0, WIDTH, HEIGHT);
        simulationhandler.render(g);
        // Draw the grid
        g.setColor(Color.black);
        for (int i = 0; i < HEIGHT/10; i++) {
            g.drawLine(0,i*10, WIDTH, i*10);
        }
        for (int i = 0; i < WIDTH/10; i++) {
            g.drawLine(i*10,0, i*10, HEIGHT);
        }

        g.dispose();
        bs.show();
    }
    // NOT CHECKED HELPER FUNCTION returns Tile number based on coordinates
    public static int calcTileNum(int x, int y){
        int n;
        n = (y/10) * 80 + (x/10);
        return n;
    }

    public static int[] calcTileCords(int tileID){
        int y = (tileID/80) * 10;
        int x = (tileID%80) * 10;
        return new int[]{x,y};
    }
    /*
    public static int calcDirOfTwoCords(int[] c1, int[] c2){
        if(c1[0] < c2[0] && c1[1] == c2[1]){
            return 3;
        }else if(c1[0] > c2[0] && c1[1] == c2[1]){
            return 1;
        }else if(c1[0] == c2[0] && c1[1] < c2[1]){
            return 0;
        }else if(c1[0] == c2[0] && c1[1] > c2[1]){
            return 2;
        }
    }

     */

    // HELPER FUNCTION generates random jobs
    public static int[] generateJobArray(){
        int[] arr = new int[4];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (random.nextInt(80))*10;
        }
        return arr;
    }
    // HELPER FUNCTION generates a random order
    public int[] orderGenerator(){
        Random random = new Random();
        int[] orderArray = new int[random.nextInt(4)+1];
        for (int i = 0; i < orderArray.length; i++) {
            orderArray[i] = random.nextInt(261) + 1;
        }
        return orderArray;
    }

    // HELPER FUNCTION keeps var from exceeding a certain range
    public static int clamp(int var, int min, int max){
        if(var >= max)
            return var = max;
        else if(var <= min)
            return var = min;
        else
            return var;
    }

    public SimHandler getSimulationhandler() {
        return simulationhandler;
    }

    public static void main(String[] args) {
        new Sim();
    }
}
