package game.debug;

import game.framework.Game;
import game.framework.GameTime;
import game.input.Keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Base template for a project. This is the bare minimum needed to start project
 * that extends the Game class.
 * @author paul
 */
public class Template extends Game
{
    private float spriteX;
    private float spriteY;
    private float spriteSpeed;
    private int spriteWidth;
    private int spriteHeight;
    private int spriteDirectionX = 1;

    /**
     * Any objects/variables that need to be Initialized should do so
     * in Initialize().
     */
    @Override
    public void initialize()
    {
        // Initialize stuff in Base
        super.initialize();
        // Initialize the moving sprite
        this.spriteX = 100;
        this.spriteY = 250;
        this.spriteSpeed = 200.0f; // pixels per second
        this.spriteWidth = 64;
        this.spriteHeight = 64;
    }

    /**
     * Any Content that needs to be loaded should do so
     * in loadContent().
     */
    @Override
    public void loadContent()
    {
        // All Resources should be placed in the resource folder
        // To load a BufferedImage; BufferedImage texture = ImageHelper.load("path");
        // This file should load on all OS inside the jar :)
        // TODO: Any Content that needs to be loaded should be done here.
    }

    /**
     * Any Content that needs to be unloaded should do so
     * in unloadContent().
     * Note: I wouldn't worry about this; Its not implemented properly as of now..
     */
    @Override
    public void unloadContent()
    {
        // TODO: Any Content that needs to be disposed should be done here.
    }

    /**
     * Objects/Variables/Logic/Input that need to be updated should be placed
     * in this method.
     * @param gameTime
     */
    @Override
    public void update(GameTime gameTime)
    {
        // Call base class
        super.update(gameTime);
        // Move the sprite
        float deltaSeconds = gameTime.getDeltaTimeSeconds();
        this.spriteX += this.spriteDirectionX * this.spriteSpeed * deltaSeconds;

        // Bounce at the screen edges
        if(this.spriteX <= 0)
        {
            this.spriteX = 0;
            this.spriteDirectionX = 1;
        }
        else if(this.spriteX + this.spriteWidth >= 800)
        {
            this.spriteX = 800 - this.spriteWidth;
            this.spriteDirectionX = -1;
        }

        this.fps.update(gameTime);
        //<editor-fold defaultstate="collapsed" desc="System & Menu Keys">
        if(Keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
        {
            Game.exitGame();
        }
        //</editor-fold>
    }

    /**
     * This method draws images to the screen buffer
     * @param g2d
     */
    @Override
    public void draw(Graphics2D g2d)
    {
        // Call Base Class to clear Screen
        super.draw(g2d);
        // Draw the moving sprite
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)this.spriteX, (int)this.spriteY, this.spriteWidth, this.spriteHeight);
        this.fps.draw(g2d);
    }

    /**
     * Main Entrance into TestFramework
     * @param args
     */
    public static void main(String[] args)
    {
        Template game  = new Template();
        game.setTitle("Template - Program");
        game.setDimensions(800, 600);
        game.run();
    }
}
