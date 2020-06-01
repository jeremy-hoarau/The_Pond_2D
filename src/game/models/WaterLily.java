package game.models;

import java.awt.*;

public class WaterLily extends GameObject {

    private double deathTime;
    private final double respawnTime = 40000;

    public WaterLily(int x, int y) {
        super(x, y, Type.WaterLily);
        importImage("WaterLily.png");
        resizeImage(imgWidth/10, imgHeight/10);
        transform.translate(x ,y);
    }

    public void update() {
        if(health == 0 && System.currentTimeMillis() >= deathTime + respawnTime)
            health = 1;
    }

    public void render(Graphics2D g) {
        if(health == 1)
            g.drawImage(img, transform, null);
    }

    public boolean die() {
        if(health == 1) {
            health = 0;
            deathTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        return health != 0;
    }
}
