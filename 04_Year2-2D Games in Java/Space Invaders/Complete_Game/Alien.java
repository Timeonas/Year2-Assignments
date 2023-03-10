package Space_Invaders.Complete_Game;

import java.awt.Image;
public class Alien extends Sprite2D {
    //Member data
    private static double xSpeed=0;
    public boolean isAlive = true;
    public Alien(Image i, Image i2) {
        super(i, i2); // invoke constructor on superclass Sprite2D
    }

    public boolean move() {
        x+=xSpeed;
        //Ensures that aliens do not leave the screen and returns a boolean to indicate whether to change direction
        if (x<=0 || x>=winWidth-myImage.getWidth(null))
            return true;
        else
            return false;
    }
    public static void setFleetXSpeed(double dx) {
        xSpeed=dx;
    }
    public static void reverseDirection() {
        xSpeed=-xSpeed;
    }
    public void jumpDownwards() {
        y+=20;
    }
}
