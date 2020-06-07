package game;

import game.models.Duck;
import game.models.GameObject;
import game.models.Type;
import game.models.WaterLily;

import java.awt.*;

import static game.Game.*;


public class Physics {

    public static String collisionPond(Duck duck) {

        Rectangle[] pondColliders = {
            new Rectangle(0, 0, pondMinX, HEIGHT),       // left
            new Rectangle(pondMaxX, 0, 500, HEIGHT),  // right
            new Rectangle(0, 0, WIDTH, pondMinY),       // top
            new Rectangle(0, pondMaxY, WIDTH, 500)  // bot
        };

        String[] directionToGo = {"right", "left", "bot", "top"};

        for (int i = 0; i < 4; i++) {
            if(duck.getCollider().intersects(pondColliders[i])) {
                for (int j = 0; j < 4; j++) {
                    if(j == i)
                        continue;
                    if(duck.getCollider().intersects(pondColliders[j])) {
                        if(i <= 1) {
                            return directionToGo[i] + "-" + directionToGo[j];
                        }
                        return directionToGo[j] + "-" + directionToGo[i];
                    }
                }
                return directionToGo[i];
            }
        }
        return null;
    }

    public static String collisionRock(Duck duck) {
        for (GameObject go:game.Handler.getRocks()) {
             if(go.getCollider().intersects(duck.getCollider()))
                 return getDirection(duck, go);
        }
        return null;
    }

    public static String collisionWaterLily(Duck duck) {
        for (WaterLily waterLily:game.Handler.getWaterLilies()) {
            if(waterLily.getCollider().intersects(duck.getCollider())) {
                if(waterLily.isAlive()) {
                    if(duck.getType() == Type.Duck) {
                        waterLily.die();
                        duck.eat();
                        return null;
                    }
                    else
                        return getDirection(duck, waterLily);
                }
            }
        }
        return null;
    }

    private static String getDirection(Duck duck, GameObject go) {
        // COLLIDE LEFT
        if((duck.getX()-duck.getWidth()/2 < go.getX()+go.getWidth()/2 && go.getX()-go.getWidth()/2 < duck.getX()-go.getWidth()/2)) {
            if(go.getY()-go.getHeight()/2 < duck.getY()+duck.getHeight()/2 && duck.getY()+duck.getHeight()/2 < go.getY()+go.getHeight()/2)        // collide left-bot
                return "right-top";
            else if(duck.getY()-duck.getHeight()/2 < go.getY()+go.getHeight()/2 && go.getY()-go.getHeight()/2< duck.getY()-duck.getHeight()/2)      // collide left-top
                return "right-bot";
            else
                return "right";
        }

        // COLLIDE RIGHT
        if(go.getX()-go.getWidth()/2 < duck.getX()+duck.getWidth()/2 && duck.getX()+duck.getWidth()/2 < go.getX()+go.getWidth()/2) {
            if(go.getY()-go.getHeight()/2 < duck.getY()+duck.getHeight()/2 && duck.getY()+duck.getHeight()/2 < go.getY()+go.getHeight()/2)        // collide right-bot
                return "left-top";
            else if(duck.getY()-duck.getHeight()/2 < go.getY()+go.getHeight()/2 && go.getY()-go.getHeight()/2 < duck.getY()-duck.getHeight()/2)      // collide right-top
                return "left-bot";
            else
                return "left";
        }

        // COLLIDE TOP
        else if(go.getY()-go.getHeight()/2 < duck.getY()-duck.getHeight()/2 && duck.getY()-duck.getHeight()/2 < go.getY()+go.getHeight()/2) {
            return "bot";
        }

        // COLLIDE BOT
        else if(go.getY()-go.getHeight()/2 < duck.getY()+duck.getHeight()/2 && duck.getY()+duck.getHeight()/2 < go.getY()+go.getHeight()/2) {
            return "top";
        }
        return null;
    }
}
