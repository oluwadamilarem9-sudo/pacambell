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
    private enum GameState
    {
        TITLE,
        PLAYING,
        PAUSED,
        GAME_OVER
    }

    private float playerX;
    private float playerY;
    private float playerSpeed;
    private int playerWidth;
    private int playerHeight;

    private float enemyX;
    private float enemyY;
    private float enemySpeed;
    private int enemyWidth;
    private int enemyHeight;

    private int score;
    private float scoreTimer;
    private boolean gameOver;
    private GameState gameState;

    /**
     * Any objects/variables that need to be Initialized should do so
     * in Initialize().
     */
    @Override
    public void initialize()
    {
        // Initialize stuff in Base
        super.initialize();
        resetGame();
        this.gameState = GameState.TITLE;
    }

    private void resetGame()
    {
        // Initialize the player sprite
        this.playerX = 100;
        this.playerY = 250;
        this.playerSpeed = 240.0f; // pixels per second
        this.playerWidth = 64;
        this.playerHeight = 64;

        // Initialize a simple enemy
        this.enemyX = 500;
        this.enemyY = 220;
        this.enemySpeed = 140.0f;
        this.enemyWidth = 64;
        this.enemyHeight = 64;

        this.score = 0;
        this.scoreTimer = 0.0f;
        this.gameOver = false;
    }

    private void startGame()
    {
        resetGame();
        this.gameState = GameState.PLAYING;
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

        float deltaSeconds = gameTime.getDeltaTimeSeconds();

        switch(this.gameState)
        {
            case TITLE:
                if(Keyboard.keyDownOnce(KeyEvent.VK_ENTER))
                    startGame();
                break;
            case PLAYING:
                float movementX = 0.0f;
                float movementY = 0.0f;

                if(Keyboard.keyDown(KeyEvent.VK_LEFT) || Keyboard.keyDown(KeyEvent.VK_A))
                    movementX -= 1.0f;
                if(Keyboard.keyDown(KeyEvent.VK_RIGHT) || Keyboard.keyDown(KeyEvent.VK_D))
                    movementX += 1.0f;
                if(Keyboard.keyDown(KeyEvent.VK_UP) || Keyboard.keyDown(KeyEvent.VK_W))
                    movementY -= 1.0f;
                if(Keyboard.keyDown(KeyEvent.VK_DOWN) || Keyboard.keyDown(KeyEvent.VK_S))
                    movementY += 1.0f;

                if(movementX != 0.0f || movementY != 0.0f)
                {
                    float length = (float)Math.sqrt(movementX * movementX + movementY * movementY);
                    movementX /= length;
                    movementY /= length;
                }

                this.playerX += movementX * this.playerSpeed * deltaSeconds;
                this.playerY += movementY * this.playerSpeed * deltaSeconds;

                // Clamp the player inside the window bounds
                this.playerX = Math.max(0, Math.min(this.playerX, 800 - this.playerWidth));
                this.playerY = Math.max(0, Math.min(this.playerY, 600 - this.playerHeight));

                // Move enemy and bounce it off screen edges
                this.enemyX += this.enemySpeed * deltaSeconds;
                if(this.enemyX <= 0)
                {
                    this.enemyX = 0;
                    this.enemySpeed = Math.abs(this.enemySpeed);
                }
                else if(this.enemyX + this.enemyWidth >= 800)
                {
                    this.enemyX = 800 - this.enemyWidth;
                    this.enemySpeed = -Math.abs(this.enemySpeed);
                }

                // Score increases while the player survives
                this.scoreTimer += deltaSeconds;
                if(this.scoreTimer >= 1.0f)
                {
                    this.score += 1;
                    this.scoreTimer -= 1.0f;
                }

                // Check for collision with the enemy
                if(new Rectangle((int)this.playerX, (int)this.playerY, this.playerWidth, this.playerHeight)
                        .intersects(new Rectangle((int)this.enemyX, (int)this.enemyY, this.enemyWidth, this.enemyHeight)))
                {
                    this.gameOver = true;
                    this.gameState = GameState.GAME_OVER;
                }

                if(Keyboard.keyDownOnce(KeyEvent.VK_P))
                    this.gameState = GameState.PAUSED;
                break;
            case PAUSED:
                if(Keyboard.keyDownOnce(KeyEvent.VK_P))
                    this.gameState = GameState.PLAYING;
                break;
            case GAME_OVER:
                if(Keyboard.keyDownOnce(KeyEvent.VK_R))
                    startGame();
                break;
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

        switch(this.gameState)
        {
            case TITLE:
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 36));
                g2d.drawString("Java Game Demo", 240, 220);
                g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                g2d.drawString("Press ENTER to start.", 300, 260);
                g2d.drawString("Use arrow keys or WASD to move.", 280, 290);
                g2d.drawString("Press ESC to quit.", 310, 320);
                break;
            default:
                // Draw the player sprite
                g2d.setColor(Color.YELLOW);
                g2d.fillOval((int)this.playerX, (int)this.playerY, this.playerWidth, this.playerHeight);

                // Draw the enemy
                g2d.setColor(Color.RED);
                g2d.fillRect((int)this.enemyX, (int)this.enemyY, this.enemyWidth, this.enemyHeight);

                // Draw controls and status
                g2d.setColor(Color.WHITE);
                g2d.drawString("Use arrow keys or WASD to move.", 10, 20);
                g2d.drawString("Press ESC to quit.", 10, 40);
                g2d.drawString(String.format("Score: %d", this.score), 10, 60);
                g2d.drawString(String.format("Position: %.0f, %.0f", this.playerX, this.playerY), 10, 80);
                g2d.drawString("Press P to pause.", 10, 100);

                if(this.gameState == GameState.PAUSED)
                {
                    g2d.setFont(new Font("Arial", Font.BOLD, 24));
                    g2d.drawString("Paused", 360, 280);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                    g2d.drawString("Press P to resume.", 320, 310);
                }
                else if(this.gameState == GameState.GAME_OVER)
                {
                    g2d.setFont(new Font("Arial", Font.BOLD, 24));
                    g2d.drawString("Game Over", 340, 280);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                    g2d.drawString("Press R to restart.", 330, 310);
                }
                break;
        }

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
