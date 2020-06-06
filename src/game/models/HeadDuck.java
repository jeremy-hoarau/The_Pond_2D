package game.models;

import game.Game;
import game.Handler;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class HeadDuck extends Duck {

    LinkedList<Duck> followers = new LinkedList<>();
    ArrayList<double[]> positionsHistory = new ArrayList<>();

    float timeBetweenPositions = 100; // in milli sec
    int maxStoredPositions = 20;
    long lastTimeSavePosition;
    float nbOfPositionPerDuck = 70;
    long lastTimeADuckLeft;
    int timeBetweenLeavers = 1000;
    int timeBetweenSounds = 2000;
    long lastTimeSoundPlayed;
    float soundVolume = 0.5f;
    long timeOfKoink;
    int koinkDuration = 420;

    public HeadDuck(double x, double y, int rotation) throws UnsupportedAudioFileException {

        this.x = x;
        this.y = y;
        id = Handler.getNextId();
        health = 5;
        speed = 1;
        type = Type.HeadDuck;
        importImage("HeadDuck.png");
        srcImgWidth = img.getWidth(null);
        srcImgHeight = img.getHeight(null);
        resizeDuckImage();
        transform.translate(x+50 ,y-300);
        rotateByAngle(rotation);
        rotateImage(angleToRotate);

        lastTimeSavePosition = System.currentTimeMillis();
        positionsHistory.add(new double[]{x, y});
        lastTimeADuckLeft = System.currentTimeMillis();
        lastTimeSoundPlayed = System.currentTimeMillis() - timeBetweenSounds;
    }

    public void update() {
        if(doingKoink && timeOfKoink + koinkDuration < System.currentTimeMillis()) {
            doingKoink = false;
            importImage("HeadDuck.png");
            resizeDuckImage();
        }
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
        if (lastTimeSoundPlayed + timeBetweenSounds < System.currentTimeMillis()){
            koink();
            lastTimeSoundPlayed = System.currentTimeMillis();
        }
    }

    private void koink() {
        try {
            File inputStream = new File("assets/quack.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue((volume.getMaximum()-volume.getMinimum())* soundVolume + volume.getMinimum());
            clip.start();
            koinkAnimation();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void koinkAnimation() {
        doingKoink = true;
        timeOfKoink = System.currentTimeMillis();
        importImage("HeadDuck_Koink.png");
        resizeImage(srcImgWidth/20+(srcImgWidth/100)*health, srcImgHeight/20+(srcImgHeight/100)*health);
    }

    public void addFollower(Duck duck) {
        if(followers.size() > 0){
            followers.getLast().setIsLastFollower(false);
        }
        followers.add(duck);
        duck.setIsLastFollower(true);
    }

    public void removeFollower(Duck duck) {
        followers.remove(duck);
        duck.setIsLastFollower(false);
        if(followers.size() > 0)
            followers.getLast().setIsLastFollower(true);
        lastTimeADuckLeft = System.currentTimeMillis();
    }

    public double[] getFollowTarget(int id) {
        if(positionsHistory.size() == 0)
            return new double[]{0, 0};
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

    public boolean canLeave() {
        return lastTimeADuckLeft + timeBetweenLeavers < System.currentTimeMillis();
    }
}
