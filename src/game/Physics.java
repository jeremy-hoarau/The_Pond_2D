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
            new Rectangle(0, 0, xMin, HEIGHT),       // left
            new Rectangle(xMax, 0, 500, HEIGHT),  // right
            new Rectangle(0, 0, WIDTH, yMin),       // top
            new Rectangle(0, yMax, WIDTH, 500)  // bot
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

    public static String collisionDuck(Duck duck) {
        for (GameObject go:game.Handler.getDucks()) {
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

    private static String getDirection(GameObject duck, GameObject go) {
        // COLLIDE LEFT
        if((duck.getX() < go.getX()+go.getWidth() && go.getX() < duck.getX())) {
            if(go.getY() < duck.getY()+duck.getHeight() && duck.getY()+duck.getHeight() < go.getY()+go.getHeight())        // collide left-bot
                return "right-top";
            else if(duck.getY() < go.getY()+go.getHeight() && go.getY() < duck.getY())      // collide left-top
                return "right-bot";
            else
                return "right";
        }

        // COLLIDE RIGHT
        if(go.getX() < duck.getX()+duck.getWidth() && duck.getX()+duck.getWidth() < go.getX()+go.getWidth()) {
            if(go.getY() < duck.getY()+duck.getHeight() && duck.getY()+duck.getHeight() < go.getY()+go.getHeight())        // collide right-bot
                return "left-top";
            else if(duck.getY() < go.getY()+go.getHeight() && go.getY() < duck.getY())      // collide right-top
                return "left-bot";
            else
                return "left";
        }

        // COLLIDE TOP
        else if(go.getY() < duck.getY() && duck.getY() < go.getY()+go.getHeight() && duck.getX() < go.getX() && go.getX()+go.getWidth() < duck.getX()+duck.getWidth()) {
            return "bot";
        }

        // COLLIDE BOT
        else if(go.getY() < duck.getY()+duck.getHeight() && duck.getY()+duck.getHeight() < go.getY()+go.getHeight() && duck.getX() < go.getX() && go.getX()+go.getWidth() < duck.getX()+duck.getWidth()) {
            return "top";
        }
        return null;
    }
}
