package Space_Invaders.Complete_Game;

import java.awt.Image;
public class Spaceship extends Sprite2D {
    private double xSpeed = 0;

    public Spaceship(Image i) {
        super(i,i); // invoke constructor on superclass Sprite2D
    }

    public void setXSpeed(double dx) {
        xSpeed = dx;
    }

    public void move() {
        //Apply current movement
        x += xSpeed;
        //Stop movement at screen edge
        if (x <= 0) {
            x = 0;
            xSpeed = 0;
        } else if (x >= winWidth - myImage.getWidth(null)) {
            x = winWidth - myImage.getWidth(null);
            xSpeed = 0;
        }
    }
}


