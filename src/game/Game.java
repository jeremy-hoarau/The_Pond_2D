package game;

import game.models.Duck;
import game.models.Rock;
import game.models.WaterLily;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 1280, HEIGHT = 720;
    public static Graphics2D g;

    // Pond borders
    public static final int xMin = 400;
    public static final int xMax = 1200;
    public static final int yMin = (HEIGHT-600)/2;
    public static final int yMax = (HEIGHT-600)/2 + 600;

    private Thread thread;
    private boolean running = false;

    private final Handler handler;

    public Game() {
        new Window(WIDTH, HEIGHT, "The Pond", this);
        handler = new Handler();
        init();
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1e9 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                update();
                delta--;
            }
            if(running)
                render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    private void update() {
        handler.update();
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        g = (Graphics2D) bs.getDrawGraphics();

        g.setColor(new Color(37, 123, 37));     //Background
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(new Color(62, 112, 215));    //Pond
        g.fillRoundRect(xMin, yMin, xMax-xMin, yMax-yMin, 50, 50);

        handler.render(g);

        g.dispose();
        bs.show();
    }

    private void init() {
        handler.addDuck(new Duck());

        handler.addRock(new Rock(550, 150));
        handler.addRock(new Rock(700, 500));
        handler.addRock(new Rock(1000, 250));

        handler.addWaterLily(new WaterLily(450, 550));
        handler.addWaterLily(new WaterLily(600, 230));
        handler.addWaterLily(new WaterLily(720, 350));
        handler.addWaterLily(new WaterLily(800, 150));
        handler.addWaterLily(new WaterLily(850, 600));
        handler.addWaterLily(new WaterLily(1000, 100));
        handler.addWaterLily(new WaterLily(1030, 425));

    }

    public static void main(String[] args) {
        new Game();
    }

}
