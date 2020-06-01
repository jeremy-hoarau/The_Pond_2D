package game.models;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class GameObject {
    protected int health;
    protected double x, y;
    protected float speed;
    protected Type type;

    protected AffineTransform transform = new AffineTransform();
    protected Image img;
    protected int imgWidth;
    protected int imgHeight;

    public GameObject(double x, double y, Type id) {
        this.x = x;
        this.y = y;
        this.type = id;
    }

    public abstract void update();

    public abstract void render(Graphics2D g);


    //Getters
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getSpeed() {
        return speed;
    }
    public Type getType() {
        return type;
    }
    public int getHealth() {
        return health;
    }
    public Rectangle getCollider() {
        return new Rectangle((int) x-imgWidth/2, (int) y-imgHeight/2, imgWidth, imgHeight);
    }
    public double getWidth() {
        return imgWidth;
    }
    public double getHeight() {
        return imgHeight;
    }


    //Setters
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public void setHealth(int health) { this.health = health;}


    //////////////////////////////////////////////


    public void importImage(String imageName) {
        try {
            img = ImageIO.read(new FileInputStream("assets/" + imageName));
            imgWidth = img.getWidth(null);
            imgHeight = img.getHeight(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resizeImage(int width, int height) {
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imgWidth = img.getWidth(null);
        imgHeight = img.getHeight(null);
        x = x + imgWidth/2f;
        y = y + imgHeight/2f;
    }
}
