package simulation;

import simulation.Window;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Random;

public class Sim extends Canvas implements Runnable {
    //public static final int WIDTH = 640, HEIGHT = WIDTH / 12 *9;
    public static final int WIDTH = 800, HEIGHT = 800;

    private Thread thread;
    private boolean running = false;

    private Random r;
    private SimHandler simulationhandler;
    ControlWindow cWindow;
    Organiser organiser;

    private long startTime = System.currentTimeMillis();

    public Sim(){
        // backend
        simulationhandler = new SimHandler();
        organiser = new Organiser();

        // window setup
        new Window(WIDTH+16, HEIGHT+38, "Logistics Simulation", this);
        cWindow = new ControlWindow(this, organiser);
        setupTiles();

        // HELPER generating random job array
        for (int i = 0; i < 10; i++) {
            organiser.addJobPending(generateJobArray());
        }

        // TESTING PURPOSE manual robot initiation
        Robot r = new Robot(10, 10, ID.Robot, 4, organiser);
        Robot r2 = new Robot(100, 100, ID.Robot, 5, organiser);
        Robot r3 = new Robot(130, 80, ID.Robot, 6 , organiser);
        Robot r4 = new Robot(700, 0, ID.Robot, 7 , organiser);
        Robot r5 = new Robot(520, 0, ID.Robot, 8 , organiser);
        simulationhandler.addRobot(r);
        simulationhandler.addRobot(r2);
        simulationhandler.addRobot(r3);
        simulationhandler.addRobot(r4);
        simulationhandler.addRobot(r5);

        organiser.addInUse(r);
        organiser.addInUse(r2);
        organiser.addInUse(r3);
        organiser.addInUse(r4);
        organiser.addInIdle(r5);

        simulationhandler.getRobot().get(0).newRoute(700, 700, simulationhandler.getRobot().get(0).getX(), simulationhandler.getRobot().get(0).getY());
        simulationhandler.getRobot().get(1).newRoute(700, 300, simulationhandler.getRobot().get(1).getX(), simulationhandler.getRobot().get(1).getY());
        simulationhandler.getRobot().get(2).newRoute(200, 700, simulationhandler.getRobot().get(2).getX(), simulationhandler.getRobot().get(2).getY());
        simulationhandler.getRobot().get(3).newRoute(300, 0, simulationhandler.getRobot().get(3).getX(), simulationhandler.getRobot().get(3).getY());

        organiser.parseIdleRobots();
        cWindow.setupList();
        /*
        LinkedList<Tile> tiles = (LinkedList<Tile>) simulationhandler.getTiles().clone();

        for(int i = 0; i < simulationhandler.getTiles().size(); i++) {
            System.out.println(tiles.get(i).getNumber()+ "," + tiles.get(i).x+ "," + tiles.get(i).y);
        }
        */



    }

    // Initiating all Tile objects
    public void setupTiles(){
        for (int i = 0; i < HEIGHT/10; i++) {
            for (int j = 0; j < WIDTH/10; j++) {
                int num = i * 80 + j;
                Tile t = new Tile(i*10,j*10, ID.Tile, num);
                simulationhandler.addTile(t);
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
        n = (x/10) * 80 + (y/10);
        return n;
    }

    // HELPER FUNCTION generates random jobs
    public static int[] generateJobArray(){
        int[] arr = new int[4];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (random.nextInt(80))*10;
        }
        return arr;
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
