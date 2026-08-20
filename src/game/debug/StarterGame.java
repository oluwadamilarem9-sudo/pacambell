package game.debug;

import game.framework.Game;
import game.framework.GameTime;
import game.input.Keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class StarterGame extends Game
{
    private enum GameState
    {
        TITLE,
        PLAYING,
        PAUSED,
        WIN,
        GAME_OVER
    }

    private static final int WIN_SCORE = 8;
    private static final int MAX_BULLETS = 8;
    private static final int MAX_LIVES = 5;

    private static class Bullet
    {
        float x;
        float y;
        float vx;
        float vy;
    }

    private static class PowerUp
    {
        String type;
        float x;
        float y;
        int size;
    }

    private GameState gameState;

    private float playerX;
    private float playerY;
    private float playerSpeed;
    private int playerSize;
    private float lastMoveX;
    private float lastMoveY;

    private float[] enemyX;
    private float[] enemyY;
    private float[] enemySpeed;
    private int[] enemySize;
    private int[] enemyHealth;
    private int enemyCount;

    private boolean bossActive;
    private float bossX;
    private float bossY;
    private float bossSpeed;
    private int bossSize;
    private int bossHealth;
    private int bossMaxHealth;

    private float targetX;
    private float targetY;
    private int targetSize;

    private int score;
    private int lives;
    private int bestScore;
    private int wave;
    private float rapidFireTimer;
    private float shieldTimer;
    private float fireCooldown;

    private final ArrayList<Bullet> bullets;
    private final ArrayList<PowerUp> powerUps;

    public StarterGame()
    {
        super("Orb Escape", 800, 600);
        this.bullets = new ArrayList<>();
        this.powerUps = new ArrayList<>();
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
        this.lastMoveX = 1.0f;
        this.lastMoveY = 0.0f;

        this.enemyCount = 3;
        this.enemyX = new float[] {700.0f, 720.0f, 680.0f};
        this.enemyY = new float[] {500.0f, 120.0f, 320.0f};
        this.enemySpeed = new float[] {90.0f, 110.0f, 130.0f};
        this.enemySize = new int[] {30, 28, 32};
        this.enemyHealth = new int[] {1, 1, 1};

        this.bossActive = false;
        this.bossX = 400.0f;
        this.bossY = 80.0f;
        this.bossSpeed = 90.0f;
        this.bossSize = 90;
        this.bossHealth = 8;
        this.bossMaxHealth = 8;

        this.targetSize = 18;
        this.targetX = 350.0f;
        this.targetY = 250.0f;

        this.score = 0;
        this.lives = 3;
        this.wave = 1;
        this.rapidFireTimer = 0.0f;
        this.shieldTimer = 0.0f;
        this.fireCooldown = 0.0f;
        this.bullets.clear();
        this.powerUps.clear();
        spawnPowerUp();
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

    private void spawnBoss()
    {
        if(this.bossActive)
            return;

        this.bossActive = true;
        this.bossX = 360.0f;
        this.bossY = 90.0f;
        this.bossSpeed = 70.0f;
        this.bossSize = 90;
        this.bossHealth = 6;
        this.bossMaxHealth = 6;
        this.wave += 1;
    }

    private void spawnPowerUp()
    {
        String[] types = {"heal", "rapid", "shield"};
        String type = types[(int)(Math.random() * types.length)];
        PowerUp powerUp = new PowerUp();
        powerUp.type = type;
        powerUp.size = 14;
        powerUp.x = 60 + (float)(Math.random() * (800 - 120));
        powerUp.y = 80 + (float)(Math.random() * (600 - 160));
        this.powerUps.add(powerUp);
    }

    private void fireBullet()
    {
        if(this.bullets.size() >= MAX_BULLETS)
            return;

        float dirX = this.lastMoveX;
        float dirY = this.lastMoveY;

        if(dirX == 0.0f && dirY == 0.0f)
        {
            dirX = 1.0f;
            dirY = 0.0f;
        }

        Bullet bullet = new Bullet();
        bullet.x = this.playerX + (this.playerSize / 2.0f) - 3.0f;
        bullet.y = this.playerY + (this.playerSize / 2.0f) - 3.0f;
        bullet.vx = dirX * 320.0f;
        bullet.vy = dirY * 320.0f;
        this.bullets.add(bullet);
    }

    private void setEnemyPosition(int index, float x, float y)
    {
        this.enemyX[index] = x;
        this.enemyY[index] = y;
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

        if(this.gameState == GameState.PAUSED)
        {
            if(Keyboard.keyDownOnce(KeyEvent.VK_P) || Keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
                this.gameState = GameState.PLAYING;
            if(Keyboard.keyDownOnce(KeyEvent.VK_Q))
                Game.exitGame();
            return;
        }

        if(this.gameState == GameState.WIN || this.gameState == GameState.GAME_OVER)
        {
            if(Keyboard.keyDownOnce(KeyEvent.VK_ENTER) || Keyboard.keyDownOnce(KeyEvent.VK_R))
                startGame();
            if(Keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
                Game.exitGame();
            return;
        }

        if(Keyboard.keyDownOnce(KeyEvent.VK_P))
        {
            this.gameState = GameState.PAUSED;
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
            this.lastMoveX = moveX;
            this.lastMoveY = moveY;
        }

        if(Keyboard.keyDown(KeyEvent.VK_SPACE) && this.fireCooldown <= 0.0f)
        {
            fireBullet();
            this.fireCooldown = (this.rapidFireTimer > 0.0f) ? 0.12f : 0.28f;
        }
        this.fireCooldown = Math.max(0.0f, this.fireCooldown - delta);
        if(this.rapidFireTimer > 0.0f)
            this.rapidFireTimer = Math.max(0.0f, this.rapidFireTimer - delta);
        if(this.shieldTimer > 0.0f)
            this.shieldTimer = Math.max(0.0f, this.shieldTimer - delta);

        this.playerX += moveX * this.playerSpeed * delta;
        this.playerY += moveY * this.playerSpeed * delta;
        this.playerX = Math.max(0, Math.min(this.playerX, 800 - this.playerSize));
        this.playerY = Math.max(0, Math.min(this.playerY, 600 - this.playerSize));

        Rectangle playerRect = new Rectangle((int)this.playerX, (int)this.playerY, this.playerSize, this.playerSize);
        Rectangle targetRect = new Rectangle((int)this.targetX, (int)this.targetY, this.targetSize, this.targetSize);

        Iterator<PowerUp> powerIt = this.powerUps.iterator();
        while(powerIt.hasNext())
        {
            PowerUp powerUp = powerIt.next();
            Rectangle powerRect = new Rectangle((int)powerUp.x, (int)powerUp.y, powerUp.size, powerUp.size);
            if(playerRect.intersects(powerRect))
            {
                if("heal".equals(powerUp.type))
                    this.lives = Math.min(MAX_LIVES, this.lives + 1);
                else if("rapid".equals(powerUp.type))
                    this.rapidFireTimer = 6.0f;
                else if("shield".equals(powerUp.type))
                    this.shieldTimer = 6.0f;

                powerIt.remove();
                spawnPowerUp();
            }
        }

        if(playerRect.intersects(targetRect))
        {
            this.score += 1;
            this.bestScore = Math.max(this.bestScore, this.score);
            respawnTarget();

            if(this.score >= WIN_SCORE)
            {
                this.gameState = GameState.WIN;
                return;
            }

                if(this.score >= 4 && !this.bossActive)
                spawnBoss();

            if(this.score % 2 == 0)
            {
                this.wave += 1;
                for(int i = 0; i < this.enemyCount; i++)
                    this.enemySpeed[i] += 10.0f;
            }

            if(this.powerUps.size() < 2)
                spawnPowerUp();
        }

        if(this.bossActive)
        {
            float bossChaseX = this.playerX - this.bossX;
            float bossChaseY = this.playerY - this.bossY;
            float bossLength = (float)Math.sqrt(bossChaseX * bossChaseX + bossChaseY * bossChaseY);

            if(bossLength > 0.1f)
            {
                this.bossX += (bossChaseX / bossLength) * this.bossSpeed * delta;
                this.bossY += (bossChaseY / bossLength) * this.bossSpeed * delta;
            }

            this.bossX = Math.max(0, Math.min(this.bossX, 800 - this.bossSize));
            this.bossY = Math.max(0, Math.min(this.bossY, 600 - this.bossSize));

            Rectangle bossRect = new Rectangle((int)this.bossX, (int)this.bossY, this.bossSize, this.bossSize);
            Rectangle playerRectBoss = new Rectangle((int)this.playerX, (int)this.playerY, this.playerSize, this.playerSize);
            if(playerRectBoss.intersects(bossRect))
            {
                this.lives -= 1;
                this.playerX = 80.0f;
                this.playerY = 80.0f;
                if(this.lives <= 0)
                {
                    this.gameState = GameState.GAME_OVER;
                    this.bestScore = Math.max(this.bestScore, this.score);
                }
            }
        }

        Iterator<Bullet> bulletIt = this.bullets.iterator();
        while(bulletIt.hasNext())
        {
            Bullet bullet = bulletIt.next();
            bullet.x += bullet.vx * delta;
            bullet.y += bullet.vy * delta;

            if(bullet.x < 0 || bullet.x > 800 || bullet.y < 0 || bullet.y > 600)
            {
                bulletIt.remove();
                continue;
            }

            Rectangle bulletRect = new Rectangle((int)bullet.x, (int)bullet.y, 8, 8);
            boolean bulletHit = false;

            if(this.bossActive)
            {
                Rectangle bossRect = new Rectangle((int)this.bossX, (int)this.bossY, this.bossSize, this.bossSize);
                if(bulletRect.intersects(bossRect))
                {
                            this.bossHealth -= 1;
                    bulletHit = true;

                    if(this.bossHealth <= 0)
                    {
                        this.bossActive = false;
                        this.score += 3;
                        this.bestScore = Math.max(this.bestScore, this.score);
                        this.wave += 1;
                        respawnTarget();
                    }
                }
            }

            if(!bulletHit)
            {
                for(int i = 0; i < this.enemyCount; i++)
                {
                    Rectangle enemyRect = new Rectangle((int)this.enemyX[i], (int)this.enemyY[i], this.enemySize[i], this.enemySize[i]);
                    if(bulletRect.intersects(enemyRect))
                    {
                        float spawnX = 50 + (float)(Math.random() * (800 - 110));
                        float spawnY = 60 + (float)(Math.random() * (600 - 110));
                        setEnemyPosition(i, spawnX, spawnY);
                        bulletHit = true;
                        break;
                    }
                }
            }

            if(bulletHit)
                bulletIt.remove();
        }

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
                if(this.shieldTimer > 0.0f)
                {
                    this.shieldTimer = 0.0f;
                    this.playerX = 80.0f;
                    this.playerY = 80.0f;
                }
                else
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
            g2d.fillRect(160, 160, 480, 250);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 38));
            g2d.drawString("Orb Escape", 295, 230);

            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.drawString("Move with WASD or arrow keys.", 215, 280);
            g2d.drawString("Press SPACE to shoot.", 260, 310);
            g2d.drawString("Collect green orbs and power-ups.", 205, 340);
            g2d.drawString("Collect 8 green orbs to win.", 225, 370);
            g2d.drawString("Press P to pause at any time.", 225, 400);
            g2d.drawString("Press ENTER to play.", 270, 440);
            return;
        }

        g2d.setColor(new Color(10, 12, 20, 200));
        g2d.fillRoundRect(12, 12, 205, 190, 16, 16);
        g2d.setColor(new Color(120, 180, 255, 160));
        g2d.drawRoundRect(12, 12, 205, 190, 16, 16);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Score: " + this.score + " / " + WIN_SCORE, 24, 34);
        g2d.drawString("Lives: " + this.lives, 24, 58);
        g2d.drawString("Best: " + this.bestScore, 24, 82);
        g2d.drawString("Wave: " + this.wave, 24, 106);
        g2d.drawString("Press P to pause", 24, 130);

        int statusY = 154;
        if(this.rapidFireTimer > 0.0f)
        {
            g2d.setColor(new Color(110, 200, 255));
            g2d.drawString("Rapid Fire!", 24, statusY);
            statusY += 22;
        }
        if(this.shieldTimer > 0.0f)
        {
            g2d.setColor(new Color(255, 190, 80));
            g2d.drawString("Shield!", 24, statusY);
        }

        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)this.playerX, (int)this.playerY, this.playerSize, this.playerSize);
        if(this.shieldTimer > 0.0f)
        {
            g2d.setColor(new Color(100, 220, 255, 120));
            g2d.fillOval((int)this.playerX - 8, (int)this.playerY - 8, this.playerSize + 16, this.playerSize + 16);
        }

        for(Bullet bullet : this.bullets)
        {
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int)bullet.x, (int)bullet.y, 8, 8);
        }

        for(int i = 0; i < this.enemyCount; i++)
        {
            g2d.setColor(Color.RED);
            g2d.fillRect((int)this.enemyX[i], (int)this.enemyY[i], this.enemySize[i], this.enemySize[i]);
        }

        for(PowerUp powerUp : this.powerUps)
        {
            if("heal".equals(powerUp.type))
            {
                g2d.setColor(new Color(0, 255, 120));
                g2d.fillOval((int)powerUp.x, (int)powerUp.y, powerUp.size, powerUp.size);
                g2d.setColor(Color.BLACK);
                g2d.drawString("+", (int)powerUp.x + 5, (int)powerUp.y + 10);
            }
            else if("rapid".equals(powerUp.type))
            {
                g2d.setColor(new Color(80, 180, 255));
                g2d.fillOval((int)powerUp.x, (int)powerUp.y, powerUp.size, powerUp.size);
                g2d.setColor(Color.BLACK);
                g2d.drawString("R", (int)powerUp.x + 4, (int)powerUp.y + 10);
            }
            else if("shield".equals(powerUp.type))
            {
                g2d.setColor(new Color(255, 180, 60));
                g2d.fillOval((int)powerUp.x, (int)powerUp.y, powerUp.size, powerUp.size);
                g2d.setColor(Color.BLACK);
                g2d.drawString("S", (int)powerUp.x + 4, (int)powerUp.y + 10);
            }
        }

        g2d.setColor(Color.GREEN);
        g2d.fillOval((int)this.targetX, (int)this.targetY, this.targetSize, this.targetSize);
        g2d.setColor(new Color(255, 255, 255, 160));
        g2d.drawOval((int)this.targetX - 6, (int)this.targetY - 6, this.targetSize + 12, this.targetSize + 12);

        if(this.bossActive)
        {
            g2d.setColor(new Color(90, 80, 160));
            g2d.fillRect((int)this.bossX, (int)this.bossY, this.bossSize, this.bossSize);
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int)this.bossX, (int)this.bossY - 18, this.bossSize, 8);
            g2d.setColor(Color.RED);
            g2d.fillRect((int)this.bossX, (int)this.bossY - 18, (int)((this.bossHealth / (float)this.bossMaxHealth) * this.bossSize), 8);
            g2d.setColor(Color.WHITE);
            g2d.drawString("BOSS", (int)this.bossX + 20, (int)this.bossY + 13);
        }

        if(this.gameState == GameState.PAUSED)
        {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(200, 200, 400, 170);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString("Paused", 350, 260);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            g2d.drawString("Press P to resume", 310, 300);
            g2d.drawString("Press Q to quit", 325, 330);
        }
        else if(this.gameState == GameState.WIN)
        {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(220, 220, 360, 180);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.drawString("You Win!", 325, 270);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            g2d.drawString("Final score: " + this.score, 315, 305);
            g2d.drawString("Best score: " + this.bestScore, 315, 335);
            g2d.drawString("Press ENTER to play again", 285, 365);
        }
        else if(this.gameState == GameState.GAME_OVER)
        {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(220, 220, 360, 180);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.drawString("Game Over", 320, 270);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            g2d.drawString("Final score: " + this.score, 315, 305);
            g2d.drawString("Best score: " + this.bestScore, 315, 335);
            g2d.drawString("Press R to restart", 315, 365);
        }
    }

    public static void main(String[] args)
    {
        StarterGame game = new StarterGame();
        game.run();
    }
}
