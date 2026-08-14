package game.debug;

import game.framework.Game;
import game.framework.GameTime;
import game.input.Keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;

public class StarterGame extends Game
{
    private enum GameState
    {
        TITLE,
        PLAYING,
        GAME_OVER
    }

    private GameState gameState;

    private float playerX;
    private float playerY;
    private float playerSpeed;
    private int playerSize;

    private float[] enemyX;
    private float[] enemyY;
    private float[] enemySpeed;
    private int[] enemySize;
    private int enemyCount;

    private float targetX;
    private float targetY;
    private int targetSize;

    private int score;
    private int lives;
    private int bestScore;
    private int wave;
    private float waveTimer;

    public StarterGame()
    {
        super("Orb Escape", 800, 600);
    }

    @Override
    public void initialize()
    {
        super.initialize();
        this.gameState = GameState.TITLE;
        this.bestScore = 0;
        resetGame();
    }

    private void resetGame()
    {
        this.playerX = 80.0f;
        this.playerY = 80.0f;
        this.playerSpeed = 220.0f;
        this.playerSize = 28;

        this.enemyCount = 3;
        this.enemyX = new float[] {700.0f, 720.0f, 680.0f};
        this.enemyY = new float[] {500.0f, 120.0f, 320.0f};
        this.enemySpeed = new float[] {120.0f, 135.0f, 160.0f};
        this.enemySize = new int[] {30, 28, 32};

        this.targetSize = 18;
        this.targetX = 350.0f;
        this.targetY = 250.0f;

        this.score = 0;
        this.lives = 3;
        this.wave = 1;
        this.waveTimer = 0.0f;
    }

    private void startGame()
    {
        resetGame();
        this.gameState = GameState.PLAYING;
    }

    private void respawnTarget()
    {
        this.targetX = 40 + (float)(Math.random() * (800 - 80 - this.targetSize));
        this.targetY = 60 + (float)(Math.random() * (600 - 100 - this.targetSize));
    }

    @Override
    public void loadContent()
    {
        // Add image or audio loading later.
    }

    @Override
    public void unloadContent()
    {
        // Add cleanup later.
    }

    @Override
    public void update(GameTime gameTime)
    {
        super.update(gameTime);

        if(this.gameState == GameState.TITLE)
        {
            if(Keyboard.keyDownOnce(KeyEvent.VK_ENTER))
                startGame();
            if(Keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
                Game.exitGame();
            return;
        }

        if(this.gameState == GameState.GAME_OVER)
        {
            if(Keyboard.keyDownOnce(KeyEvent.VK_R))
                startGame();
            if(Keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
                Game.exitGame();
            return;
        }

        float delta = gameTime.getDeltaTimeSeconds();
        float moveX = 0.0f;
        float moveY = 0.0f;

        if(Keyboard.keyDown(KeyEvent.VK_LEFT) || Keyboard.keyDown(KeyEvent.VK_A))
            moveX -= 1.0f;
        if(Keyboard.keyDown(KeyEvent.VK_RIGHT) || Keyboard.keyDown(KeyEvent.VK_D))
            moveX += 1.0f;
        if(Keyboard.keyDown(KeyEvent.VK_UP) || Keyboard.keyDown(KeyEvent.VK_W))
            moveY -= 1.0f;
        if(Keyboard.keyDown(KeyEvent.VK_DOWN) || Keyboard.keyDown(KeyEvent.VK_S))
            moveY += 1.0f;

        if(moveX != 0.0f || moveY != 0.0f)
        {
            float length = (float)Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
        }

        this.playerX += moveX * this.playerSpeed * delta;
        this.playerY += moveY * this.playerSpeed * delta;
        this.playerX = Math.max(0, Math.min(this.playerX, 800 - this.playerSize));
        this.playerY = Math.max(0, Math.min(this.playerY, 600 - this.playerSize));

        Rectangle playerRect = new Rectangle((int)this.playerX, (int)this.playerY, this.playerSize, this.playerSize);
        Rectangle targetRect = new Rectangle((int)this.targetX, (int)this.targetY, this.targetSize, this.targetSize);

        if(playerRect.intersects(targetRect))
        {
            this.score += 1;
            this.bestScore = Math.max(this.bestScore, this.score);
            respawnTarget();

            if(this.score % 5 == 0)
            {
                this.wave += 1;
                this.waveTimer = 0.0f;
                for(int i = 0; i < this.enemyCount; i++)
                    this.enemySpeed[i] += 18.0f;
            }
        }

        this.waveTimer += delta;
        for(int i = 0; i < this.enemyCount; i++)
        {
            float chaseX = this.playerX - this.enemyX[i];
            float chaseY = this.playerY - this.enemyY[i];
            float chaseLength = (float)Math.sqrt(chaseX * chaseX + chaseY * chaseY);

            if(chaseLength > 0.1f)
            {
                this.enemyX[i] += (chaseX / chaseLength) * this.enemySpeed[i] * delta;
                this.enemyY[i] += (chaseY / chaseLength) * this.enemySpeed[i] * delta;
            }

            this.enemyX[i] = Math.max(0, Math.min(this.enemyX[i], 800 - this.enemySize[i]));
            this.enemyY[i] = Math.max(0, Math.min(this.enemyY[i], 600 - this.enemySize[i]));

            Rectangle enemyRect = new Rectangle((int)this.enemyX[i], (int)this.enemyY[i], this.enemySize[i], this.enemySize[i]);
            if(playerRect.intersects(enemyRect))
            {
                this.lives -= 1;
                this.playerX = 80.0f;
                this.playerY = 80.0f;
                this.enemyX[i] = 700.0f + i * 25.0f;
                this.enemyY[i] = 100.0f + i * 120.0f;

                if(this.lives <= 0)
                {
                    this.gameState = GameState.GAME_OVER;
                    this.bestScore = Math.max(this.bestScore, this.score);
                }
                break;
            }
        }

        if(Keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
            Game.exitGame();
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        super.draw(g2d);

        if(this.gameState == GameState.TITLE)
        {
            g2d.setColor(new Color(0, 0, 0, 170));
            g2d.fillRect(160, 160, 480, 220);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 38));
            g2d.drawString("Orb Escape", 300, 230);

            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.drawString("Use WASD or arrow keys to move.", 215, 280);
            g2d.drawString("Collect the green orb to score.", 230, 310);
            g2d.drawString("Avoid the red enemies.", 255, 340);
            g2d.drawString("Press ENTER to play.", 270, 380);
            return;
        }

        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + this.score, 20, 24);
        g2d.drawString("Lives: " + this.lives, 20, 48);
        g2d.drawString("Best: " + this.bestScore, 20, 72);
        g2d.drawString("Wave: " + this.wave, 20, 96);

        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)this.playerX, (int)this.playerY, this.playerSize, this.playerSize);

        for(int i = 0; i < this.enemyCount; i++)
        {
            g2d.setColor(Color.RED);
            g2d.fillRect((int)this.enemyX[i], (int)this.enemyY[i], this.enemySize[i], this.enemySize[i]);
        }

        g2d.setColor(Color.GREEN);
        g2d.fillOval((int)this.targetX, (int)this.targetY, this.targetSize, this.targetSize);

        if(this.gameState == GameState.GAME_OVER)
        {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(220, 220, 360, 170);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.drawString("Game Over", 320, 270);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            g2d.drawString("Final score: " + this.score, 315, 305);
            g2d.drawString("Best score: " + this.bestScore, 315, 335);
            g2d.drawString("Wave reached: " + this.wave, 315, 365);
            g2d.drawString("Press R to restart", 315, 395);
        }
    }

    public static void main(String[] args)
    {
        StarterGame game = new StarterGame();
        game.run();
    }
}
