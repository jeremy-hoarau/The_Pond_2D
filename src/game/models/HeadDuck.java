package game.models;

import game.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HeadDuck extends Duck {

    LinkedList<Duck> followers = new LinkedList<>();

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
        transform.translate(x+50,y-300);
        rotateByAngle(rotation);
        rotateImage(angleToRotate);
    }

    public void update() {  //TODO Faire "KOIN KOIN"!
                            //TODO Générer un historique de positions pour les followers
        angleToRotate = 0;
        checkCollision();
        rotateImage(angleToRotate);
        move();
        rotateByAngle(angleToRotate);
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

    public Duck getFollowTarget(int id) {               //TODO retourner la position à follow
        for (int i = 0; i < followers.size(); i++) {
            if(followers.get(i).getId() == id){
                if(i == 0)
                    return this;
                return followers.get(i-1);
            }
        }
        return this;
    }
}
