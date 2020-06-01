package game.models;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

public class Rock extends GameObject{

    public Rock(int x, int y) {
        super(x, y, Type.Rock);
        health = 1;
        importImage("Rock.png");
        resizeImage(imgWidth/5, imgHeight/5);
        transform.translate(x, y);
    }

    public void update() {

    }

    public void render(Graphics2D g){
        g.drawImage(img, transform, null);
    };
}
