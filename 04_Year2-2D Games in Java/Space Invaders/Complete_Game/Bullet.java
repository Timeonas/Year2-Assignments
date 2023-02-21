package Space_Invaders.Complete_Game;

import java.awt.Image;

public class Bullet extends Sprite2D {
    public Bullet(Image i, int winWidth) {
        super(i,i);
    }

    //Move the bullet up towards the aliens every frame, if it reaches the top border, return false to remove the bullet
    public boolean move() {
        y-=5;
        if(y<45){
            return false;
        }
        return true;
    }
}
