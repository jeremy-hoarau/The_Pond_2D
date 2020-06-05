package game.models;

import game.Game;
import game.Handler;
import game.Physics;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

import static java.lang.Math.*;

public class Duck extends GameObject {

    protected int id;
    protected int rotation = 0;

    private long lastLunchTime = System.currentTimeMillis();
    private final long timeBetweenLunches = 9000;
    private boolean inWater = false;
    private boolean isLastFollower = true;
    private HeadDuck leader = null;
    private final int leaderDetectionDistance = 150;
    protected double dx;
    protected double dy;

    protected final Random random = new Random();

    protected int srcImgWidth;
    protected int srcImgHeight;

    protected float angleToRotate;

    public Duck() {
        super(0, 0, Type.Duck);
        id = Handler.getNextId();
        health = 4;
        speed = 1;
        importImage("Duck_Ground.png");
        srcImgWidth = img.getWidth(null);
        srcImgHeight = img.getHeight(null);
        resizeDuckImage();
        transform.translate(-50,300);
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
        if(leader == null || (isLastFollower && leader.canLeave()))
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

    private void followLeader() {
        double[] followTarget = leader.getFollowTarget(id);
        float distX = (float) (followTarget[0] - x);
        float distY = (float) (followTarget[1] - y);
        if(x != followTarget[0] && y != followTarget[1])
            speed = (float) (leader.getSpeed()+0.1);
        else
            speed = leader.getSpeed();
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
        if(leader != null)
            closest = getDistanceToHeadDuck(leader);

        HeadDuck oldLeader = null;
        if(leader != null)
            oldLeader = leader;
        for (HeadDuck headDuck: Handler.getHeadDucks()) {
            if(headDuck == leader)
                continue;
            double distance = getDistanceToHeadDuck(headDuck);
            if((oldLeader == null && distance <= leaderDetectionDistance && distance < closest) || (oldLeader != null && distance < closest)){
                closest = distance;
                leader = headDuck;
            }
        }
        if(leader != null && (leader != oldLeader || oldLeader == null)){
            if(leader != oldLeader && oldLeader != null)
                oldLeader.removeFollower(this);
            leader.addFollower(this);
        }
    }

    private double getDistanceToHeadDuck(HeadDuck headDuck) {
        return Point2D.distance(x, y, headDuck.getX(), headDuck.getY());
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
        if(inWater) {
            if(type == Type.Duck)
                importImage("Duck_Water.png");  // import the image at each resize to preserve image quality
            else
                importImage("HeadDuck.png");
        }
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
        //transform.rotate(angle);
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
