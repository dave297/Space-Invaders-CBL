import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Manages all invader entities and their behaviors as a group.
 */
public class InvaderManager {
    private final ArrayList<Invader> invaders = new ArrayList<>();
    private final ArrayList<Bullet> enemyBullets = new ArrayList<>();

    private int groupDirection = 1;
    private int groupSpeed = 2;

    private int shootTick = 0;
    private int fireRate = 45;

    private int score = 0;
    private int scoreCheck = 0;

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;

    private BufferedImage defaultInvaderIMG;
    private BufferedImage tankInvaderIMG;
    private BufferedImage diverInvaderIMG;
    private BufferedImage shooterInvaderIMG;

    /**
     * Loads sprite images from the classpath for all invader variants.
     */
    private void loadImages() {
        try {
            defaultInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderGREEN.png"));
            tankInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderRED.png"));
            diverInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderBLUE.png"));
            shooterInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderPURPLE.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InvaderManager() {
        loadImages();
    }

    /**
     * Builds a new wave of invaders based on the current.
     * Clears existing invaders first, then mixes types as difficulty scales.
     */
    public void initializeInvaders() {
        invaders.clear();

        if (score < 300) {
            createDefaultInvaders();
        } else if (score < 500) {
            createDefaultInvaders();
            createShooterInvaders();
        } else if (score < 1000) {
            createDefaultInvaders();
            createShooterInvaders();
            createTankInvaders();
        } else {
            createDefaultInvaders();
            createDiverInvaders();
            createShooterInvaders();
            createTankInvaders();
        }
    }

    /**
     * Adds a grid of default invaders near the top of the screen.
     */
    private void createDefaultInvaders() {
        int rows = 2;
        int columns = 8;
        int spacingX = 100;
        int spacingY = 100;
        int startX = 110;
        int startY = 110;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int x = startX + j * spacingX;
                int y = startY + i * spacingY;
                invaders.add(new DefaultInvader(x, y));
            }
        }
    }

    /**
     * Adds a line of shooter invaders that can fire at the player.
     */
    private void createShooterInvaders() {
        int y = 30;
        for (int i = 0; i < 5; i++) {
            int x = 100 + i * 200;
            invaders.add(new ShooterInvader(x, y));
        }
    }

    /**
     * Adds a row of tank invaders with multiple hit points.
     */
    private void createTankInvaders() {
        int y = 350;
        for (int i = 0; i < 8; i++) {
            int x = 110 + i * 100;
            invaders.add(new TankInvader(x, y));
        }
    }

    /**
     * Adds several diver invaders at random spaced X positions further down the field.
     */
    private void createDiverInvaders() {
        Random rand = new Random();
        int max = 300;
        int min = 100;

        for (int i = 0; i < 5; i++) {
            int random = rand.nextInt((max - min) + 1) + min;
            invaders.add(new DiverInvader(random, 450));
            min = max + 100;
            max += 200;
        }
    }

    /**
     * Updates invaders and enemy bullets, applies group movement rules and shooting,
     * then prunes dead invaders and off-screen bullets.
     * Call once per frame from the game loop.
     */
    public void update() {
        for (Invader invader : invaders) {
            if (invader.isAlive()) {
                invader.update(WIDTH, 0, groupDirection, groupSpeed);
            }
        }

        for (Bullet bullet : enemyBullets) {
            bullet.update();
        }

        handleShooterMovement();
        handleGroupMovement();
        handleEnemyShooting();

        invaders.removeIf((invader -> !invader.isAlive()));
        enemyBullets.removeIf(bullet -> bullet.getY() > HEIGHT);
    }

    private void handleShooterMovement() {
        if (ShooterInvader.shooterHitWall()) {
            ShooterInvader.reverseDirection();
        }
    }

    /**
     * Computes whether the next group step will hit a wall, flips direction if needed,
     * and optionally drops invaders vertically on bounce according to each type's rule.
     */
    private void handleGroupMovement() {
        boolean willHitWall = false;
        for (Invader invader : invaders) {
            if (invader.isAlive() && !(invader instanceof ShooterInvader)) {
                int nextX = invader.getX() + groupSpeed * groupDirection;
                if (nextX <= 0 || nextX + invader.getWidth() >= WIDTH) {
                    willHitWall = true;
                    break;
                }
            }
        }

        if (willHitWall) {
            groupDirection *= -1;
            for (Invader invader : invaders) {
                if (invader.isAlive() && invader.canDropOnBounce()) {
                    invader.moveVertical(invader.getHeight() / 5);
                }
            }
        }
    }

    /**
     * Triggers enemy shots at a fixed tick cadence based on {@link #fireRate}.
     * For each shooter-capable invader, fires a bullet downward with a simple probability test.
     */
    private void handleEnemyShooting() {
        shootTick++;
        if (fireRate <= shootTick) {
            shootTick = 0;

            for (Invader invader : invaders) {
                if (invader.isAlive() && invader.canShoot() && Math.random() < 0.4) {
                    enemyBullets.add(new Bullet(
                        invader.getX(),
                        invader.getY() + invader.getWidth(), 
                        // uses width as height offset for sprite bottom
                        8
                    ));
                }
            }
        }
    }

    /**
     * Draws all invaders using their specific sprite where available, 
     * or falls back to shape rendering.
     */
    public void draw(Graphics g) {
        for (Invader invader : invaders) {
            BufferedImage img = getInvaderImage(invader);
            if (img == null) {
                System.out.println("Image is null for " + invader.getClass().getSimpleName());
            }
            invader.draw(g, img);
        }
    }

    /**
     * Returns the sprite image for the given invader type.
     * @return the corresponding sprite
     */
    private BufferedImage getInvaderImage(Invader invader) {
        if (invader instanceof DefaultInvader) {
            return defaultInvaderIMG;
        } else if (invader instanceof TankInvader) {
            return tankInvaderIMG;
        } else if (invader instanceof DiverInvader) {
            return diverInvaderIMG;
        } else if (invader instanceof ShooterInvader) {
            return shooterInvaderIMG;
        }
        return defaultInvaderIMG;
    }

    /**
     * Checks collisions between player bullets and invaders.
     * Removes collided player bullets, applies damage, 
     * awards score on kills, and increases difficulty periodically.
     */
    public void checkCollision(ArrayList<Bullet> playerBullets) {
        for (int i  = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            if (bullet.getDy() < 0) {
                for (Invader invader : invaders) {
                    if (invader.isAlive() && isColliding(bullet, invader)) {
                        invader.takeHit();
                        playerBullets.remove(i);
                        if (!invader.isAlive()) {
                            score += 10;
                            if (score % 100 == 0 && score > 0) {
                                scoreCheck += 1;
                            }
                            if (scoreCheck == 5) {
                                scoreCheck = 0;
                                groupSpeed += 1;
                                System.out.println(groupSpeed);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Determines whether the player has been hit either by a colliding invader 
     * or by an enemy bullet.
     * Enemy bullets are removed when they hit the player.
     */
    public boolean checkPlayerHit(int playerX, int playerY, int playerWidth, int playerHeight) {
        for (Invader invader : invaders) {
            if (invader.isAlive() && enemyHitsPlayer(invader,
                playerX, playerY, playerWidth, playerHeight)) {
                return true;
            }
        }

        for (int i = enemyBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            if (bullet.getDy() > 0
                && bulletHitsPlayer(bullet, playerX, playerY, playerHeight, playerWidth)) {
                enemyBullets.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Axis-aligned bounding box intersection test between a bullet and the player bounds.
     */
    private boolean bulletHitsPlayer(Bullet bullet, int x, int y, int height, int width) {
        return bullet.getX() < x + width
            && bullet.getX() + bullet.getWidth() > x
            && bullet.getY() < y + height
            && bullet.getY() + bullet.getHeight() > y;
    }

    /**
     * Axis-aligned bounding box intersection test between a bullet and an invader.
     */
    private boolean isColliding(Bullet bullet, Invader invader) {
        return bullet.getX() < invader.getX() + invader.getWidth()
            && bullet.getX() + bullet.getWidth() > invader.getX()
            && bullet.getY() < invader.getY() + invader.getHeight()
            && bullet.getY() + bullet.getHeight() > invader.getY();
    }

    /**
     * Axis-aligned bounding box intersection test between an invader and the player.
     */
    private boolean enemyHitsPlayer(Invader invader, int x, int y, int width, int height) {
        return invader.getX() < x + width
            && invader.getX() + invader.getWidth() > x
            && invader.getY() < y + height
            && invader.getY() + invader.getHeight() > y;
    }

    /**
     * Returns a live list of enemy bullets fired by invaders.
     * The list is owned by this manager. External code should modify it carefully.
     *
     * @return list of enemy bullets
     */
    public ArrayList<Bullet> getEnemyBullets() {
        return enemyBullets;
    }

    /**
     * Returns the current score value.
     *
     * @return player score
     */
    public int getScore() {
        return score;
    }

    /**
     * Indicates whether all invaders have been destroyed.
     *
     * @return true if no invaders remain, false otherwise
     */
    public boolean allDead() {
        return invaders.isEmpty();
    }

    /**
     * Resets the manager to its initial state and spawns a fresh wave.
     * Clears entities, resets difficulty parameters, and calls {@link #initializeInvaders()}.
     */
    public void reset() {
        invaders.clear();
        enemyBullets.clear();

        groupDirection = 1;
        groupSpeed = 2;
        shootTick = 0;
        fireRate = 45;
        score = 0;
        scoreCheck = 0;

        initializeInvaders();
    }
}
