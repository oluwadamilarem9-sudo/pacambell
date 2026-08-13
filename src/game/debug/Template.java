package game.debug;

import game.audio.AudioHelper;
import game.audio.SaveHelper;
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
    private float enemyDirectionX;
    private float enemyBaseSpeed;
    private float enemySpeed;
    private int enemyWidth;
    private int enemyHeight;

    private float enemy2X;
    private float enemy2Y;
    private float enemy2BaseSpeed;
    private int enemy2Width;
    private int enemy2Height;

    private float elapsedTime;
    private float collectibleX;
    private float collectibleY;
    private int collectibleSize;
    private boolean collectibleActive;
    private float collectibleTimer;

    private int score;
    private float scoreTimer;
    private int lives;
    private int highScore;
    private boolean invulnerable;
    private float invulnerableTimer;
    private boolean gameOver;
    private boolean soundOn;
    private int level;
    private int nextLevelScore;
    private GameState gameState;

    private int selectedTitleOption;
    private int selectedPauseOption;
    private static final String[] TITLE_OPTIONS = {"Start Game", "Exit"};
    private static final String[] PAUSE_OPTIONS = {"Resume", "Toggle Sound", "Reset Best", "Quit"};

    /**
     * Any objects/variables that need to be Initialized should do so
     * in Initialize().
     */
    @Override
    public void initialize()
    {
        // Initialize stuff in Base
        super.initialize();
        this.highScore = SaveHelper.loadHighScore();
        resetGame();
        this.gameState = GameState.TITLE;
        this.selectedTitleOption = 0;
        this.selectedPauseOption = 0;
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
        this.enemyX = 700;
        this.enemyY = 100;
        this.enemyDirectionX = 1.0f;
        this.enemyBaseSpeed = 126.0f;
        this.enemySpeed = this.enemyBaseSpeed;
        this.enemyWidth = 64;
        this.enemyHeight = 64;

        // Initialize a second enemy
        this.enemy2X = 700;
        this.enemy2Y = 500;
        this.enemy2BaseSpeed = 108.0f;
        this.enemy2Width = 64;
        this.enemy2Height = 64;

        // Initialize collectibles
        this.collectibleSize = 24;
        this.collectibleTimer = 0.0f;
        spawnCollectible();

        this.score = 0;
        this.scoreTimer = 0.0f;
        this.elapsedTime = 0.0f;
        this.lives = 4;
        // Short safe window at start to avoid instant collisions
        this.invulnerable = true;
        this.invulnerableTimer = 1.5f;
        this.level = 1;
        this.nextLevelScore = 20;
        this.soundOn = true;
        this.gameOver = false;
    }

    private void startGame()
    {
        resetGame();
        this.gameState = GameState.PLAYING;
        if(this.soundOn)
            AudioHelper.playBackgroundMusic();
    }

    private void spawnCollectible()
    {
        this.collectibleX = 50 + (float)(Math.random() * (800 - 100 - this.collectibleSize));
        this.collectibleY = 120 + (float)(Math.random() * (600 - 140 - this.collectibleSize));
        this.collectibleActive = true;
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
                if(Keyboard.keyDownOnce(KeyEvent.VK_DOWN) || Keyboard.keyDownOnce(KeyEvent.VK_S))
                    this.selectedTitleOption = (this.selectedTitleOption + 1) % TITLE_OPTIONS.length;
                if(Keyboard.keyDownOnce(KeyEvent.VK_UP) || Keyboard.keyDownOnce(KeyEvent.VK_W))
                    this.selectedTitleOption = (this.selectedTitleOption + TITLE_OPTIONS.length - 1) % TITLE_OPTIONS.length;
                if(Keyboard.keyDownOnce(KeyEvent.VK_ENTER))
                {
                    if(this.selectedTitleOption == 0)
                        startGame();
                    else
                        Game.exitGame();
                }
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

                // Increase difficulty over time (slower ramp, lower max)
                this.elapsedTime += deltaSeconds;
                float difficultyScale = 1.0f + Math.min(this.elapsedTime / 45.0f, 1.5f);
                this.enemySpeed = this.enemyBaseSpeed * difficultyScale;
                float enemy2Speed = this.enemy2BaseSpeed * difficultyScale;

                // Move enemy and bounce it off screen edges
                this.enemyX += this.enemyDirectionX * this.enemySpeed * deltaSeconds;
                if(this.enemyX <= 0)
                {
                    this.enemyX = 0;
                    this.enemyDirectionX = 1.0f;
                }
                else if(this.enemyX + this.enemyWidth >= 800)
                {
                    this.enemyX = 800 - this.enemyWidth;
                    this.enemyDirectionX = -1.0f;
                }

                // Move second enemy toward the player
                float chaseX = this.playerX - this.enemy2X;
                float chaseY = this.playerY - this.enemy2Y;
                float chaseLength = (float)Math.sqrt(chaseX * chaseX + chaseY * chaseY);
                if(chaseLength > 0.1f)
                {
                    this.enemy2X += (chaseX / chaseLength) * enemy2Speed * deltaSeconds;
                    this.enemy2Y += (chaseY / chaseLength) * enemy2Speed * deltaSeconds;
                }

                // Keep the second enemy inside the window
                this.enemy2X = Math.max(0, Math.min(this.enemy2X, 800 - this.enemy2Width));
                this.enemy2Y = Math.max(0, Math.min(this.enemy2Y, 600 - this.enemy2Height));

                // Score increases while the player survives
                this.scoreTimer += deltaSeconds;
                if(this.scoreTimer >= 1.0f)
                {
                    this.score += 1;
                    this.scoreTimer -= 1.0f;
                }

                // Check for collectible pickup
                if(this.collectibleActive && new Rectangle((int)this.playerX, (int)this.playerY, this.playerWidth, this.playerHeight)
                        .intersects(new Rectangle((int)this.collectibleX, (int)this.collectibleY, this.collectibleSize, this.collectibleSize)))
                {
                    this.score += 5;
                    this.collectibleActive = false;
                    this.collectibleTimer = 0.0f;
                    if(this.soundOn)
                        AudioHelper.playPickupSound();
                }

                if(!this.collectibleActive)
                {
                    this.collectibleTimer += deltaSeconds;
                    if(this.collectibleTimer >= 2.5f)
                    {
                        spawnCollectible();
                        this.collectibleTimer = 0.0f;
                    }
                }

                // Level progression
                if(this.score >= this.nextLevelScore)
                {
                    this.level += 1;
                    this.nextLevelScore += 20;
                    this.enemyBaseSpeed += 18.0f;
                    this.enemy2BaseSpeed += 14.0f;
                    if(this.soundOn)
                        AudioHelper.playLevelUpSound();
                }

                // Check for collision with the first enemy
                if(!this.invulnerable && new Rectangle((int)this.playerX, (int)this.playerY, this.playerWidth, this.playerHeight)
                        .intersects(new Rectangle((int)this.enemyX, (int)this.enemyY, this.enemyWidth, this.enemyHeight)))
                {
                    this.lives -= 1;
                    this.invulnerable = true;
                    this.invulnerableTimer = 2.0f;
                    this.playerX = 100;
                    this.playerY = 250;
                    if(this.soundOn)
                        AudioHelper.playHitSound();
                    if(this.lives <= 0)
                    {
                        this.gameOver = true;
                        this.gameState = GameState.GAME_OVER;
                        this.highScore = Math.max(this.highScore, this.score);
                        SaveHelper.saveHighScore(this.highScore);
                        if(this.soundOn)
                            AudioHelper.playGameOverSound();
                        AudioHelper.stopBackgroundMusic();
                    }
                }

                // Check for collision with the second enemy
                if(!this.invulnerable && new Rectangle((int)this.playerX, (int)this.playerY, this.playerWidth, this.playerHeight)
                        .intersects(new Rectangle((int)this.enemy2X, (int)this.enemy2Y, this.enemy2Width, this.enemy2Height)))
                {
                    this.lives -= 1;
                    this.invulnerable = true;
                    this.invulnerableTimer = 2.0f;
                    this.playerX = 100;
                    this.playerY = 250;
                    if(this.soundOn)
                        AudioHelper.playHitSound();
                    if(this.lives <= 0)
                    {
                        this.gameOver = true;
                        this.gameState = GameState.GAME_OVER;
                        this.highScore = Math.max(this.highScore, this.score);
                        SaveHelper.saveHighScore(this.highScore);
                        if(this.soundOn)
                            AudioHelper.playGameOverSound();
                        AudioHelper.stopBackgroundMusic();
                    }
                }

                if(this.invulnerable)
                {
                    this.invulnerableTimer -= deltaSeconds;
                    if(this.invulnerableTimer <= 0.0f)
                        this.invulnerable = false;
                }

                if(Keyboard.keyDownOnce(KeyEvent.VK_P))
                {
                    this.selectedPauseOption = 0;
                    this.gameState = GameState.PAUSED;
                }
                if(Keyboard.keyDownOnce(KeyEvent.VK_M))
                {
                    this.soundOn = !this.soundOn;
                    if(this.soundOn)
                        AudioHelper.playBackgroundMusic();
                    else
                        AudioHelper.stopBackgroundMusic();
                }
                break;
            case PAUSED:
                if(Keyboard.keyDownOnce(KeyEvent.VK_DOWN) || Keyboard.keyDownOnce(KeyEvent.VK_S))
                    this.selectedPauseOption = (this.selectedPauseOption + 1) % PAUSE_OPTIONS.length;
                if(Keyboard.keyDownOnce(KeyEvent.VK_UP) || Keyboard.keyDownOnce(KeyEvent.VK_W))
                    this.selectedPauseOption = (this.selectedPauseOption + PAUSE_OPTIONS.length - 1) % PAUSE_OPTIONS.length;
                if(Keyboard.keyDownOnce(KeyEvent.VK_ENTER))
                {
                    switch(this.selectedPauseOption)
                    {
                        case 0:
                            this.gameState = GameState.PLAYING;
                            break;
                        case 1:
                            this.soundOn = !this.soundOn;
                            if(this.soundOn)
                                AudioHelper.playBackgroundMusic();
                            else
                                AudioHelper.stopBackgroundMusic();
                            break;
                        case 2:
                            this.highScore = 0;
                            SaveHelper.saveHighScore(this.highScore);
                            break;
                        case 3:
                            Game.exitGame();
                            break;
                    }
                }
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
        if(Keyboard.keyDownOnce(KeyEvent.VK_M))
        {
            this.soundOn = !this.soundOn;
            if(this.soundOn && this.gameState == GameState.PLAYING)
                AudioHelper.playBackgroundMusic();
            else
                AudioHelper.stopBackgroundMusic();
        }
        if(Keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
        {
            if(this.gameState == GameState.PLAYING)
                AudioHelper.stopBackgroundMusic();
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
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(160, 160, 480, 240);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 42));
                g2d.drawString("Java Game Demo", 240, 220);
                g2d.setFont(new Font("Arial", Font.PLAIN, 20));
                g2d.drawString("Use arrow keys or WASD to move.", 260, 260);
                g2d.drawString("Press ENTER to select.", 280, 290);
                g2d.drawString("Press M to toggle sound.", 280, 320);

                for(int i = 0; i < TITLE_OPTIONS.length; i++)
                {
                    if(i == this.selectedTitleOption)
                        g2d.setColor(Color.YELLOW);
                    else
                        g2d.setColor(Color.WHITE);
                    g2d.drawString(TITLE_OPTIONS[i], 340, 340 + (i * 30));
                }
                break;
            default:
                // Draw the player sprite
                g2d.setColor(this.invulnerable ? Color.ORANGE : Color.YELLOW);
                g2d.fillOval((int)this.playerX, (int)this.playerY, this.playerWidth, this.playerHeight);

                // Draw the first enemy
                g2d.setColor(Color.RED);
                g2d.fillRect((int)this.enemyX, (int)this.enemyY, this.enemyWidth, this.enemyHeight);

                // Draw the second enemy
                g2d.setColor(Color.MAGENTA);
                g2d.fillRect((int)this.enemy2X, (int)this.enemy2Y, this.enemy2Width, this.enemy2Height);

                // Draw collectible
                if(this.collectibleActive)
                {
                    g2d.setColor(Color.GREEN);
                    g2d.fillOval((int)this.collectibleX, (int)this.collectibleY, this.collectibleSize, this.collectibleSize);
                }

                // Draw controls and status
                g2d.setColor(Color.WHITE);
                g2d.drawString("Use arrow keys or WASD to move.", 10, 20);
                g2d.drawString("Press ESC to quit.", 10, 40);
                g2d.drawString(String.format("Score: %d", this.score), 10, 60);
                g2d.drawString(String.format("Lives: %d", this.lives), 10, 80);
                g2d.drawString(String.format("Best: %d", this.highScore), 10, 100);
                g2d.drawString(String.format("Level: %d", this.level), 10, 120);
                g2d.drawString(String.format("Next: %d", this.nextLevelScore), 10, 140);
                g2d.drawString(String.format("Difficulty: %.1fx", 1.0f + Math.min(this.elapsedTime / 30.0f, 2.0f)), 10, 160);
                g2d.drawString(String.format("Sound: %s", this.soundOn ? "ON" : "OFF"), 10, 180);
                g2d.drawString("Press P to pause.", 10, 200);
                g2d.drawString("Press M to toggle sound.", 10, 220);
                g2d.drawString("Collect green orb for +5.", 10, 240);

                if(this.invulnerable)
                {
                    g2d.drawString("Invulnerable!", 10, 180);
                }

                if(this.gameState == GameState.PAUSED)
                {
                    g2d.setColor(new Color(0, 0, 0, 180));
                    g2d.fillRect(240, 220, 320, 140);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 28));
                    g2d.drawString("Paused", 340, 250);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                    for(int i = 0; i < PAUSE_OPTIONS.length; i++)
                    {
                        g2d.setColor(i == this.selectedPauseOption ? Color.YELLOW : Color.WHITE);
                        g2d.drawString(PAUSE_OPTIONS[i], 330, 290 + (i * 30));
                    }
                }
                else if(this.gameState == GameState.GAME_OVER)
                {
                    g2d.setColor(new Color(0, 0, 0, 180));
                    g2d.fillRect(220, 200, 360, 180);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 28));
                    g2d.drawString("Game Over", 310, 240);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                    g2d.drawString(String.format("Final Score: %d", this.score), 300, 280);
                    g2d.drawString(String.format("Best Score: %d", this.highScore), 300, 310);
                    g2d.drawString("Press R to restart.", 300, 340);
                    g2d.drawString("Press ESC to quit.", 300, 370);
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
