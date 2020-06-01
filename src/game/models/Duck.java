package game.models;

import game.Game;
import game.Handler;
import game.Physics;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.*;

public class Duck extends GameObject {

    protected int id;
    protected int rotation = 0;

    private long lastLunchTime = System.currentTimeMillis();
    private final long timeBetweenLunches = 15000;
    private boolean inWater = false;
    private boolean isLastFollower = true;
    private HeadDuck leader = null;
    private final int leaderDetectionDistance = 10;
    protected double dx;
    protected double dy;

    protected final Random random = new Random();

    protected int srcImgWidth;
    protected int srcImgHeight;

    protected float angleToRotate;

    public Duck() {
        super(-50, 300, Type.Duck);
        id = Handler.getNextId();
        health = 2;
        speed = 1;
        importImage("Duck_Ground.png");
        srcImgWidth = img.getWidth(null);
        srcImgHeight = img.getHeight(null);
        resizeDuckImage();
        transform.translate(x,y);
    }

    public void update() {
        dx = 0;
        dy = 0;
        angleToRotate = 0;
        if(transform.getTranslateX() >= Game.xMin && !inWater){     // Enter in Water
            enterInWater();
        }
        else if(inWater && leader == null)
            checkCollision();
        if(leader == null && (lastLunchTime + timeBetweenLunches) < System.currentTimeMillis())
            loseWeight();
        detectCloseLeader();
        if(leader != null)
            followLeader();
        rotateImage(angleToRotate);
        move();
    }

    public void render(Graphics2D g) {
        g.drawImage(img, transform, null);
    }

    ///////////////////////////////////

    private void followLeader() {                   //TODO suivire l'historique de position plutot que d'utiliser l'intersects
        Duck followTarget = leader.getFollowTarget(id);
        if(getCollider().intersects(followTarget.getCollider()))
            speed -= 0.01;
        else if(speed <= 1.5)
            speed += 0.01;
        float distX = (float) (followTarget.getX() - x);
        float distY = (float) (followTarget.getY() - y);

        float angle = (float) Math.toDegrees(Math.atan2(distY, distX));
        if(angle < 0)
            angle += 360;
        if(rotation > angle)
            rotateByAngle(360-(rotation - angle));
        else if(rotation < angle)
            rotateByAngle(angle - rotation);
    }

    private void detectCloseLeader() {
        double closest = 999999;
        double duckX = x + imgWidth/2f;
        double duckY = y + imgHeight/2f;
        HeadDuck oldLeader = null;
        if(leader != null)
            oldLeader = leader;
        for (HeadDuck headDuck: Handler.getHeadDucks()) {
            if(headDuck == leader)
                continue;
            double distX, distY, distance;
            double headDuckX = headDuck.getX() + headDuck.getWidth()/2;
            double headDuckY = headDuck.getY() + headDuck.getHeight()/2;
            distX = headDuckX - duckX;
            distY = headDuckY - duckY;
            distance = Math.sqrt(distX*distX + distX*distY);
            if(distance <= leaderDetectionDistance && distance < closest){
                closest = distance;
                leader = headDuck;
            }
        }
        if(leader != null && (leader != oldLeader || oldLeader == null)){
            leader.addFollower(this);
        }
    }

    protected void checkCollision() {
        String direction = Physics.collisionPond(this); // Pond
        if(direction != null)
            rotateByDirection(direction);

        direction = Physics.collisionRock(this);        // Rock
        if(direction != null)
            rotateByDirection(direction);

//        direction = Physics.collisionDuck(this);             // Duck
//        if(direction != null)
//            rotateByDirection(direction);

        direction = Physics.collisionWaterLily(this);     // WaterLily
        if(direction != null)
            rotateByDirection(direction);
    }

    protected void move() {
        rotateImage(360-rotation);
        transform.translate(speed*Game.delta * cos(Math.toRadians(rotation))+dx, speed*Game.delta * sin(Math.toRadians(rotation))+dy);
        rotateImage(rotation);

        x = transform.getTranslateX() + imgWidth/2f;
        y = transform.getTranslateY() + imgHeight/2f;
    }

    private void enterInWater() {
        inWater = true;
        importImage("Duck_Water.png");
        resizeDuckImage();
        rotateByAngle(269 + random.nextInt(179));
        lastLunchTime = System.currentTimeMillis();//reset lastLaunch
        speed = 2;
    }

    protected void rotateByDirection(String direction) {
        switch (direction) {
            case "left":                                                // LEFT
                if(!(90 < rotation && rotation < 270))
                    rotateByAngle(269 - random.nextInt(179));
                break;
            case "left-top":
                if(!(180 < rotation && rotation < 270))
                    rotateByAngle(269 - random.nextInt(89));
                break;
            case "left-bot":
                if(!(90 < rotation && rotation < 180))
                    rotateByAngle(180 - random.nextInt(89));
                break;

            case "right":                                               // RIGHT
                if(!((270 < rotation && rotation < 360) || (0 < rotation && rotation < 90)))
                    rotateByAngle(271 + random.nextInt(179));
                break;
            case "right-top":
                if(!(270 < rotation && rotation < 360))
                    rotateByAngle(271 + random.nextInt(89));
                break;
            case "right-bot":
                if(!(0 < rotation && rotation < 90))
                    rotateByAngle(random.nextInt(89));
                break;

            case "top":                                                 // TOP
                if(!(180 < rotation && rotation < 360))
                    rotateByAngle(359 - random.nextInt(179));
                break;
            case "bot":                                                 // BOT
                if(!(0 < rotation && rotation < 180))
                    rotateByAngle(179 - random.nextInt(179));
                break;
        }
    }

    private void loseWeight() {
        health--;
        lastLunchTime = System.currentTimeMillis();

        resizeDuckImage();

        //move it to the center of the old image
        dx = (srcImgWidth/20f+(srcImgWidth/100f)*(health+1) - (srcImgWidth/20f+(srcImgWidth/100f)*health))/2;
        dy = (srcImgHeight/20f+(srcImgHeight/100f)*(health+1) - (srcImgHeight/20f+(srcImgHeight/100f)*health))/2;
    }

    protected void resizeDuckImage() {
        if(inWater)
            importImage("Duck_Water.png");  // import the image at each resize to preserve image quality
        int width = srcImgWidth/20+(srcImgWidth/100)*health;
        int height = srcImgHeight/20+(srcImgHeight/100)*health;

        resizeImage(width, height);
    }

    protected void rotateByAngle(float angle) {
        angleToRotate += angle;
        angleToRotate = angleToRotate%360;

        rotation += angle;
        rotation = rotation%360;
    }

    protected void rotateImage(float angle) {
        //transform.rotate(Math.toRadians(angle));
    }

    ///////////////////////////////////
    //Getters

    public int getId() {
        return id;
    }

    public int getRotation() {
        return rotation;
    }

    //Setters

    public void eat() {
        health ++;
        lastLunchTime = System.currentTimeMillis();

        resizeDuckImage();

        //move it to the center of the new image
        dx = -(srcImgWidth/20f+(srcImgWidth/100f)*(health) - (srcImgWidth/20f+(srcImgWidth/100f)*health-1))/2;
        dy = -(srcImgHeight/20f+(srcImgHeight/100f)*(health) - (srcImgHeight/20f+(srcImgHeight/100f)*health-1))/2;
    }

    public void setIsLastFollower(boolean bool){
        isLastFollower = bool;
    }

}
