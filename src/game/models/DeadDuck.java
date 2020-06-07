package game.models;

public class DeadDuck extends Duck{
    private final long deathTime;

    public DeadDuck(double x, double y, int rotation) {
        this.x = x;
        this.y = y;
        health = 5;
        deathTime = System.currentTimeMillis();
        importImage("Duck_Dead.png");
        srcImgWidth = img.getWidth(null);
        srcImgHeight = img.getHeight(null);
        resizeImage(srcImgWidth/20+(srcImgWidth/100)*health, srcImgHeight/20+(srcImgHeight/100)*health);
        dx = (srcImgWidth/20f+(srcImgWidth/100f)*health - srcImgWidth/20f+(srcImgWidth/100f))/2;
        dy = (srcImgHeight/20f+(srcImgHeight/100f)*health - srcImgHeight/20f+(srcImgHeight/100f))/2;
        transform.translate(x+50-dx ,y-300-dy);
        rotateByAngle(rotation);
    }

    public void update() {
    }

    public long getDeathTime() {
        return deathTime;
    }

    public long getTimeBeforeDelete() {
        return 3000;
    }
}
