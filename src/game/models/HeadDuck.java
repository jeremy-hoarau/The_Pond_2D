package game.models;

import game.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HeadDuck extends Duck {

    LinkedList<Duck> followers = new LinkedList<>();
    ArrayList<double[]> positionsHistory = new ArrayList<double[]>();
    float timeBetweenPositions = 100; // in milli sec
    int maxStoredPositions = 20;
    long lastTimeSavePosition;
    float nbOfPositionPerDuck = 70;

    public HeadDuck(double x, double y, int rotation) {
        id = Handler.getNextId();
        speed = 1;
        this.x = x;
        this.y = y;
        type = Type.HeadDuck;
        List<Integer> followers = new ArrayList<>();
        importImage("HeadDuck.png");
        resizeImage(imgWidth/10, imgHeight/10);
        x -= imgWidth/2f;
        y -= imgHeight/2f;
        transform.translate(x+50,y-300);    //TODO à l'apparition du HeadDuck l'image doit être plus en haut à gauche
        rotateByAngle(rotation);
        rotateImage(angleToRotate);
        lastTimeSavePosition = System.currentTimeMillis();
        positionsHistory.add(new double[]{x, y});
    }

    public void update() {  //TODO Faire "KOIN KOIN"!
        if(lastTimeSavePosition + timeBetweenPositions <= System.currentTimeMillis()){  //save position history
            lastTimeSavePosition = System.currentTimeMillis();
            positionsHistory.add(new double[]{x, y});
            if(positionsHistory.size() > maxStoredPositions * timeBetweenPositions)
                positionsHistory.remove(0);
        }
        angleToRotate = 0;
        checkCollision();
        rotateImage(angleToRotate);
        move();
        rotateByAngle(angleToRotate);
        System.out.println(followers);
    }

    public void addFollower(Duck duck) {
        if(followers.size() > 0){
            followers.getLast().setIsLastFollower(false);
        }
        followers.add(duck);
    }

    public void removeFollower(Duck duck) {
        followers.remove(duck);
    }

    public double[] getFollowTarget(int id) {
        for(int i = 0; i < followers.size(); i++) {
            if(followers.get(i).getId() == id) {
                int index = (int) (nbOfPositionPerDuck * (i+1));
                while(index > positionsHistory.size()-1)
                    index--;
                return positionsHistory.get(positionsHistory.size() - index);
            }
        }
        return new double[]{0, 0};
    }
}
