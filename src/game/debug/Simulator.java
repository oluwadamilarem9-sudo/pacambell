package game.debug;

public class Simulator
{
    public static class Stats
    {
        public double totalScore = 0;
        public double totalTimeToCollision = 0;
        public int collisions = 0;
        public int pickups = 0;
        public int levelsReached = 0;
    }

    public static void main(String[] args)
    {
        int trials = 200;
        double simTime = 120.0; // seconds per trial
        Stats s = new Stats();
        for(int t=0;t<trials;t++)
        {
            runSingle(simTime, s);
        }

        System.out.println("Trials: " + trials);
        System.out.printf("Avg score: %.2f\n", s.totalScore / trials);
        System.out.printf("Avg pickups per trial: %.2f\n", (double)s.pickups / trials);
        System.out.printf("Avg collisions per trial: %.2f\n", (double)s.collisions / trials);
        System.out.printf("Avg time to first collision: %.2f s\n", s.totalTimeToCollision / Math.max(1, s.collisions));
        System.out.printf("Avg levels reached: %.2f\n", (double)s.levelsReached / trials);
    }

    private static void runSingle(double simTime, Stats s)
    {
        // Use same parameters as Template.resetGame (approx)
        double playerX = 100, playerY = 250;
        int playerW = 64, playerH = 64;
        double enemyX = 700, enemyY = 100, enemyDirX = 1.0;
        double enemyBaseSpeed = 126.0;
        double enemy2X = 700, enemy2Y = 500;
        double enemy2BaseSpeed = 108.0;
        double elapsed = 0.0;
        double collectibleX = 0, collectibleY = 0;
        int collectibleSize = 24;
        boolean collectibleActive = false;
        double collectibleTimer = 0.0;
        int score = 0;
        double scoreTimer = 0.0;
        int lives = 4;
        // Mirror Template: start with short invulnerability
        boolean invulnerable = true;
        double invulnerableTimer = 1.5;
        int level = 1;
        int nextLevelScore = 20;

        double dt = 1.0/60.0;
        boolean firstCollisionRecorded = false;
        double timeToFirstCollision = 0.0;

        // For this sim, player will perform a slow random walk
        double vx = 0, vy = 0;
        java.util.Random rnd = new java.util.Random();

        for(int step=0; step < (int)(simTime/dt); step++)
        {
            double delta = dt;
            elapsed += delta;
            double difficultyScale = 1.0 + Math.min(elapsed / 45.0, 1.5);
            double enemySpeed = enemyBaseSpeed * difficultyScale;
            double enemy2Speed = enemy2BaseSpeed * difficultyScale;

            // Random small movement for player
            if(rnd.nextDouble() < 0.05)
            {
                vx = (rnd.nextDouble()*2 -1) * 80; // pixels/sec
                vy = (rnd.nextDouble()*2 -1) * 80;
            }
            double px = playerX + vx * delta;
            double py = playerY + vy * delta;
            px = Math.max(0, Math.min(px, 800 - playerW));
            py = Math.max(0, Math.min(py, 600 - playerH));
            playerX = px; playerY = py;

            // enemy bounce
            enemyX += enemyDirX * enemySpeed * delta;
            if(enemyX <= 0) { enemyX = 0; enemyDirX = 1.0; }
            else if(enemyX + 64 >= 800) { enemyX = 800 - 64; enemyDirX = -1.0; }

            // enemy2 chases
            double chaseX = playerX - enemy2X;
            double chaseY = playerY - enemy2Y;
            double chaseLen = Math.sqrt(chaseX*chaseX + chaseY*chaseY);
            if(chaseLen > 0.1)
            {
                enemy2X += (chaseX / chaseLen) * enemy2Speed * delta;
                enemy2Y += (chaseY / chaseLen) * enemy2Speed * delta;
            }
            enemy2X = Math.max(0, Math.min(enemy2X, 800 - 64));
            enemy2Y = Math.max(0, Math.min(enemy2Y, 600 - 64));

            // score
            scoreTimer += delta;
            if(scoreTimer >= 1.0) { score += 1; scoreTimer -= 1.0; }

            // collectible
            if(!collectibleActive)
            {
                collectibleTimer += delta;
                if(collectibleTimer >= 2.5)
                {
                    collectibleActive = true;
                    collectibleTimer = 0.0;
                    collectibleX = 50 + rnd.nextDouble() * (800 - 100 - collectibleSize);
                    collectibleY = 120 + rnd.nextDouble() * (600 - 140 - collectibleSize);
                }
            }
            else
            {
                // check pickup
                if(intersects(playerX, playerY, playerW, playerH, collectibleX, collectibleY, collectibleSize, collectibleSize))
                {
                    score += 5;
                    collectibleActive = false;
                    s.pickups++;
                }
            }

            // collisions
            if(!invulnerable && intersects(playerX, playerY, playerW, playerH, enemyX, enemyY, 64, 64))
            {
                lives -= 1;
                invulnerable = true;
                invulnerableTimer = 2.0;
                playerX = 100; playerY = 250;
                s.collisions++;
                if(!firstCollisionRecorded) { timeToFirstCollision = elapsed; firstCollisionRecorded = true; }
                if(lives <= 0) break;
            }
            if(!invulnerable && intersects(playerX, playerY, playerW, playerH, enemy2X, enemy2Y, 64, 64))
            {
                lives -= 1;
                invulnerable = true;
                invulnerableTimer = 2.0;
                playerX = 100; playerY = 250;
                s.collisions++;
                if(!firstCollisionRecorded) { timeToFirstCollision = elapsed; firstCollisionRecorded = true; }
                if(lives <= 0) break;
            }

            if(invulnerable)
            {
                invulnerableTimer -= delta;
                if(invulnerableTimer <= 0) invulnerable = false;
            }

            // level up
            if(score >= nextLevelScore)
            {
                level += 1;
                nextLevelScore += 20;
                enemyBaseSpeed += 18.0;
                enemy2BaseSpeed += 14.0;
                s.levelsReached++;
            }
        }

        s.totalScore += score;
        s.totalTimeToCollision += (firstCollisionRecorded ? timeToFirstCollision : simTime);
    }

    private static boolean intersects(double x1, double y1, double w1, double h1, double x2, double y2, double w2, double h2)
    {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }
}
