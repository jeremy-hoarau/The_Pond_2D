package game.models;

import game.Handler;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class HeadDuck extends Duck {

    private final LinkedList<Duck> followers = new LinkedList<>();
    private final ArrayList<double[]> positionsHistory = new ArrayList<>();

    private long lastTimeSavePosition;
    private long lastTimeADuckLeft;
    private final int timeBetweenSounds = 2000;
    private long lastTimeSoundPlayed;
    private long timeOfKoink;

    public HeadDuck(double x, double y, int rotation) {

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

        lastTimeSavePosition = System.currentTimeMillis();
        positionsHistory.add(new double[]{x, y});
        lastTimeADuckLeft = System.currentTimeMillis();
        lastTimeSoundPlayed = System.currentTimeMillis() - timeBetweenSounds;
    }

    public void update() {
        int koinkDuration = 420;
        if(doingKoink && timeOfKoink + koinkDuration < System.currentTimeMillis()) {
            doingKoink = false;
            importImage("HeadDuck.png");
            resizeDuckImage();
        }
        // in milli sec
        float timeBetweenPositions = 100;
        if(lastTimeSavePosition + timeBetweenPositions <= System.currentTimeMillis()){  //save position history
            lastTimeSavePosition = System.currentTimeMillis();
            positionsHistory.add(new double[]{x, y});
            int maxStoredPositions = 20;
            if(positionsHistory.size() > maxStoredPositions * timeBetweenPositions)
                positionsHistory.remove(0);
        }
        angleToRotate = 0;
        checkCollision();
        move();
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
            float soundVolume = 0.5f;
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
        float nbOfPositionPerDuck = 70;
        if(positionsHistory.size() < nbOfPositionPerDuck * followers.size())
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
        int timeBetweenLeavers = 1000;
        return lastTimeADuckLeft + timeBetweenLeavers < System.currentTimeMillis();
    }
}
