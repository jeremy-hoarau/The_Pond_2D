package game;

import game.models.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.LinkedList;

public class Handler {

    private static LinkedList<Rock> rocks = new LinkedList<>();
    private static LinkedList<WaterLily> waterLilies = new LinkedList<>();
    private static LinkedList<HeadDuck> headDucks = new LinkedList<>();
    private static LinkedList<Duck> ducks = new LinkedList<>();
    private static LinkedList<Duck> ducksToAdd = new LinkedList<>();
    private static LinkedList<Duck> ducksToRemove = new LinkedList<>();
    private static LinkedList<DeadDuck> deadDucks = new LinkedList<>();
    private static LinkedList<DeadDuck> deadDucksToRemove = new LinkedList<>();

    private static int nextId;

    private long lastSpawnTime = System.currentTimeMillis();
    private final int spawnTime = 20000;

    public void update() throws UnsupportedAudioFileException {
        if((lastSpawnTime + spawnTime) < System.currentTimeMillis()){
            ducksToAdd.add(new Duck());
            lastSpawnTime = System.currentTimeMillis();
        }
        ducks.addAll(ducksToAdd);
        ducksToAdd.clear();

        for(Duck duck : ducks) {
            if(duck.getHealth() == 0) {     // dies
                deadDucks.add(new DeadDuck(duck.getX(), duck.getY(), duck.getRotation()));
                ducksToRemove.add(duck);
                continue;
            }
            if(duck.getHealth() == 5) {     // becomes HeadDuck
                addHeadDuck(new HeadDuck(duck.getX()-duck.getWidth()/2, duck.getY()-duck.getHeight()/2, duck.getRotation()));
                ducksToRemove.add(duck);
                continue;
            }
            duck.update();
        }
        ducks.removeAll(ducksToRemove);
        ducksToRemove.clear();

        for (HeadDuck headDuck : headDucks) {
            headDuck.update();
        }

        for (WaterLily waterLily : waterLilies) {
            waterLily.update();
        }

        for (DeadDuck deadDuck : deadDucks) {
            if(deadDuck.getDeathTime()+deadDuck.getTimeBeforeDelete() <= System.currentTimeMillis()) {
                deadDucksToRemove.add(deadDuck);
            }
        }
        deadDucks.removeAll(deadDucksToRemove);
        deadDucksToRemove.clear();
    }

    public void render(Graphics2D g) {
        for (int i = 0; i < getNumberOfRocks(); i++) {          // ROCKS
            GameObject go = rocks.get(i);
            go.render(g);
        }
        for (int i = 0; i < getNumberOfWaterLilies(); i++) {    // WATER LILIES
            GameObject go = waterLilies.get(i);
            go.render(g);
        }
        for (int i = 0; i < getNumberOfDucks(); i++) {          // DUCKS
            GameObject go = ducks.get(i);
            go.render(g);
        }
        for (int i = 0; i < getNumberOfHeadDucks(); i++) {          // HEAD DUCKS
            GameObject go = headDucks.get(i);
            go.render(g);
        }
        for (int i = 0; i < getNumberOfDeadDucks(); i++) {          // DEAD DUCKS
            GameObject go = deadDucks.get(i);
            go.render(g);
        }
    }


    public void addDuck(Duck duck) {
        ducks.add(duck);
    }
    public void addHeadDuck(HeadDuck headDuck) {
        headDucks.add(headDuck);
    }
    public void addRock(Rock rock) {
        rocks.add(rock);
    }
    public void addWaterLily(WaterLily waterLily) {
        waterLilies.add(waterLily);
    }


    public static LinkedList<Duck> getDucks() {
        return ducks;
    }
    public static LinkedList<HeadDuck> getHeadDucks() {
        return headDucks;
    }
    public static LinkedList<Rock> getRocks() {
        return rocks;
    }
    public static LinkedList<WaterLily> getWaterLilies() {
        return waterLilies;
    }


    public static int getNumberOfDucks() {
        return ducks.size();
    }
    public static int getNumberOfHeadDucks() {
        return headDucks.size();
    }
    public static int getNumberOfDeadDucks() {
        return deadDucks.size();
    }
    public static int getNumberOfRocks() {
        return rocks.size();
    }
    public static int getNumberOfWaterLilies() {
        return waterLilies.size();
    }

    public static int getNextId() {
        nextId++;
        return nextId;
    }

}
