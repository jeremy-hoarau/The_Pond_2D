package game.models;

import java.awt.*;

public class DeadDuck extends Duck{
    private long deathTime;
    private long timeBeforeDelete = 3000;

    public DeadDuck(double x, double y, int rotation) {
        this.x = x;
        this.y = y;
        deathTime = System.currentTimeMillis();
        importImage("Duck_Dead.png");
        srcImgWidth = img.getWidth(null);
        srcImgHeight = img.getHeight(null);
        resizeImage(srcImgWidth/20+(srcImgWidth/100)*health+2, srcImgHeight/20+(srcImgHeight/100)*health+2);
        transform.translate(x+50-imgWidth-2f ,y-300-imgHeight/2f);
        rotateByAngle(rotation);
        rotateImage(angleToRotate);
    }

    public void update() {
    }

    public void render(Graphics2D g) {
        g.drawImage(img, transform, null);
    }

    public long getDeathTime() {
        return deathTime;
    }

    public long getTimeBeforeDelete() {
        return timeBeforeDelete;
    }
}
