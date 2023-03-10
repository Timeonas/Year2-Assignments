package Space_Invaders.Complete_Game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
    // member data
    private static String workingDirectory;
    private static boolean isInitialised = false;
    private static final Dimension WindowSize = new Dimension(800, 600);
    private final BufferStrategy strategy;
    private static final int NUMALIENS = 30;
    private Spaceship PlayerShip;
    private Image bulletImage;

    private final ArrayList bulletsList = new ArrayList();
    private final Alien[] AliensArray = new Alien[NUMALIENS];

    //Boolean used to store the game state
    boolean isGameInProgress = false;

    //Variable used to increment the speed of the fleet on each wave
    private int incSpeed;

    //Variables to store score data
    private int Score = 0;
    private int hScore = 0;

    // constructor
    public InvadersApplication() {
        //Display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width / 2 - WindowSize.width / 2;
        int y = screensize.height / 2 - WindowSize.height / 2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
        this.setTitle("Space Invaders");

        //initialise double-buffering
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        //create and start our animation thread
        Thread t = new Thread(this);
        t.start();

        //Send keyboard events arriving into this JFrame back to its own event handlers
        addKeyListener(this);
    }

    public void startNewGame(){
        //load images from disk
        ImageIcon icon = new ImageIcon(workingDirectory + "\\ct255-images\\alien_ship_1.png");
        Image alienImage = icon.getImage();

        ImageIcon icon2 = new ImageIcon(workingDirectory + "\\ct255-images\\alien_ship_2.png");
        Image alienImage2 = icon2.getImage();

        ImageIcon icon3 = new ImageIcon(workingDirectory + "\\ct255-images\\bullet.png");
        bulletImage = icon3.getImage();

        //create and initialise some aliens, passing them each the image we have loaded
        for (int i = 0; i < NUMALIENS; i++) {
            AliensArray[i] = new Alien(alienImage, alienImage2);
            double xx = (i % 5) * 80 + 70;
            double yy = (i / 5) * 40 + 60;
            AliensArray[i].setPosition(xx, yy);
        }

        //Set the fleet speed and set the current speed variable to 5.
        Alien.setFleetXSpeed(5);
        incSpeed = 5;

        //create and initialise the player's spaceship
        icon = new ImageIcon(workingDirectory + "\\ct255-images\\player_ship.png");
        Image shipImage = icon.getImage();
        PlayerShip = new Spaceship(shipImage);
        PlayerShip.setPosition(300, 530);

        //Score is 0 for a new game.
        Score = 0;

        //tell all sprites the window width
        Sprite2D.setWinWidth(WindowSize.width);

        //Ready to print
        isInitialised = true;
    }

    public void startNewWave(){
        //Dont allow printing to happen until all aliens are initialised for the new wave
        isInitialised = false;
        for (int i = 0; i < NUMALIENS; i++) {
            AliensArray[i].isAlive = true;
            double xx = (i % 5) * 80 + 70;
            double yy = (i / 5) * 40 + 60;
            AliensArray[i].setPosition(xx, yy);
        }
        //Increment the fleet's speed
        Alien.setFleetXSpeed((incSpeed+=5));
        //Ready to print
        isInitialised = true;
    }

    // thread's entry point
    public void run() {
            while (1 == 1) {
                    // 1: sleep for 1/50 sec
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                if (isGameInProgress) {
                    // 2: Animate game objects if the game is in progress
                    boolean alienDirectionReversalNeeded = false;
                    //Track number of aliens destroyed
                    int destroyedAliens = 0;
                    //Move all aliens and check to see if a reverse is needed
                    for (int i = 0; i < NUMALIENS; i++) {
                        if (AliensArray[i].move()) {
                            alienDirectionReversalNeeded = true;
                        }
                        //If the alien is alive, check for collisions with the player ship
                        if(AliensArray[i].isAlive) {
                            double x1 = AliensArray[i].x;
                            double x2 = PlayerShip.x;
                            double w1 = AliensArray[i].myImage.getWidth(null);
                            double w2 = PlayerShip.myImage.getWidth(null);
                            double y1 = AliensArray[i].y;
                            double y2 = PlayerShip.y;
                            double h1 = AliensArray[i].myImage.getHeight(null);
                            double h2 = PlayerShip.myImage.getHeight(null);
                            //If a collision is detected, end the game state
                            if (((x1 < x2 && x1 + w1 > x2) || (x2 < x1 && x2 + w2 > x1)) &&
                                    ((y1 < y2 && y1 + h1 > y2) || (y2 < y1 && y2 + h2 > y1))) {
                                isGameInProgress = false;
                            }
                        }
                        //If the alien isn't alive, add it to the destroyedAliens counter
                        else {
                            destroyedAliens++;
                            //If the total number of aliens destroyed equals the total number of aliens on screen, start the new wave
                            if(destroyedAliens==NUMALIENS){
                                startNewWave();
                            }
                        }
                    }
                    //If a reversal is needed, reverse the direction and make all aliens move down
                    if (alienDirectionReversalNeeded) {
                        Alien.reverseDirection();
                        for (int i = 0; i < NUMALIENS; i++)
                            AliensArray[i].jumpDownwards();
                    }

                    //Move the player ship based on input from the player
                    PlayerShip.move();

                    //Iterator class used to iterate through each member in the bullets arraylist
                    Iterator iterator = bulletsList.iterator();
                    while (iterator.hasNext()) {
                        Bullet b = (Bullet) iterator.next();
                        //If the bullet is at the top of the screen remove it from the list
                        if (!b.move()) {
                            iterator.remove();
                        }
                        //Loop through each alien
                        for (int i = 0; i < NUMALIENS; i++) {
                            //If the alien is alive, check for collisions with a bullet
                            if (AliensArray[i].isAlive) {
                                double x1 = AliensArray[i].x;
                                double x2 = b.x;
                                double w1 = AliensArray[i].myImage.getWidth(null);
                                double w2 = b.myImage.getWidth(null);
                                double y1 = AliensArray[i].y;
                                double y2 = b.y;
                                double h1 = AliensArray[i].myImage.getHeight(null);
                                double h2 = b.myImage.getHeight(null);
                                if (((x1 < x2 && x1 + w1 > x2) || (x2 < x1 && x2 + w2 > x1)) &&
                                        ((y1 < y2 && y1 + h1 > y2) || (y2 < y1 && y2 + h2 > y1))) {
                                    //If there is a collision, the alien is no longer alive
                                    AliensArray[i].isAlive = false;
                                    //Increase the player's score by 10
                                    Score+=10;
                                    //Remove the bullet that destroyed the alien from the list (Screen)
                                    iterator.remove();
                                    //Break to only remove one alien per bullet.
                                    break;
                                }
                            }
                        }
                    }
                    // 3: force an application repaint
                    this.repaint();
                }
        }
    }

    //Three Keyboard Event-Handler functions
    public void keyPressed(KeyEvent e) {
        //If the game is active, interpret the following key inputs to move to ship
        if (isGameInProgress) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                PlayerShip.setXSpeed(-4);
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                PlayerShip.setXSpeed(4);
        }
    }

    public void keyReleased(KeyEvent e) {
        //If the game is active, interpret the following key inputs to stop the ship
        if (isGameInProgress) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
                PlayerShip.setXSpeed(0);
            else if (e.getKeyCode() == KeyEvent.VK_SPACE)
                shootBullet();
        }
        else {
            //If the menu is open, interpret any key to begin the game
            startNewGame();
            isGameInProgress = true;
        }
    }

    public void keyTyped(KeyEvent e) {}

    //application's paint method
    public void paint(Graphics g) {
        //Print the following as long as the game is active and initialised
        if(isGameInProgress) {
            if (!isInitialised) {
                return;
            }
            g = strategy.getDrawGraphics(); // draw to offscreen buffer
            //clear the canvas with a big black rectangle
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WindowSize.width, WindowSize.height);

            //Show the score on top of the screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g.drawString("Score: "+Score,150,50);

            //If the current score is higher than the high score, set that as the high score
            if(Score>hScore){
                hScore = Score;
            }

            //Show the high score at the top of the screen
            g.drawString("Best: "+hScore,550,50);

            //Redraw all game objects
            for (int i = 0; i < NUMALIENS; i++) {
                if (AliensArray[i].isAlive) {
                    AliensArray[i].paint(g);
                }
            }

            //Paint the player ship
            PlayerShip.paint(g);

            //Paint all the bullets on the screen
            Iterator iterator = bulletsList.iterator();
            while (iterator.hasNext()) {
                Bullet b = (Bullet) iterator.next();
                b.paint(g);
            }

            //flip the buffers offscreen<-->onscreen
            strategy.show();
        }
        else{
            //Menu game state
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WindowSize.width, WindowSize.height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
            g.drawString("GAME OVER", 220, 170);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
            g.drawString("Press any key to play", 260, 280);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g.drawString("[Arrow keys to move, space to fire]", 245, 320);
        }
    }

    public void shootBullet() {
        //Add a new bullet to our list
        Bullet b = new Bullet(bulletImage,WindowSize.width);
        b.setPosition(PlayerShip.x+49/2, PlayerShip.y);
        bulletsList.add(b);
    }

    // application entry point
    public static void main(String[] args) {
        workingDirectory = System.getProperty("user.dir");
        System.out.println("Working Directory = " + workingDirectory);
        InvadersApplication w = new InvadersApplication();
    }
}
