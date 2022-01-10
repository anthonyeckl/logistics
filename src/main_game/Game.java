package main_game;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Game extends Canvas implements Runnable{

    public static final int WIDTH = 640, HEIGHT = WIDTH / 12 *9;

    private Thread thread;
    private boolean running = false;

    private Random r;
    private GameHandler gameHandler;
    private HUD hud;

    public Game(){
        gameHandler = new GameHandler();
        this.addKeyListener(new KeyInput(gameHandler));

        new Window(WIDTH, HEIGHT, "Lets build a Game", this);

        hud = new HUD();

        r = new Random();
        gameHandler.addObject(new Player(WIDTH/2-32, HEIGHT/2-32, ID.Player));

        //for (int i = 0; i < 20; i++)
            gameHandler.addObject(new BasicEnemy(r.nextInt(WIDTH), r.nextInt(HEIGHT), ID.Enemy));


        //gameHandler.addObject(new Player(WIDTH/2+64, HEIGHT/2+64, ID.Player2));


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

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS:" + frames);
                frames = 0;
            }
        }
        stop();

    }

    private void tick(){
        gameHandler.tick();
        hud.tick();
    }

    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0,0, WIDTH, HEIGHT);

        gameHandler.render(g);

        hud.render(g);

        g.dispose();
        bs.show();
    }

    public static int clamp(int var, int min, int max){
        if(var >= max)
            return var = max;
        else if(var <= min)
            return var = min;
        else
            return var;
    }

    public static void main(String[] args) {
        new Game();
    }

}
