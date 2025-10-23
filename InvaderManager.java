import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class InvaderManager {
    private ArrayList<Invader> invaders = new ArrayList<>();
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();
    private int groupDirection = 1;
    private int groupSpeed = 2;
    private int shootTick = 0;
    private int fireRate = 45;
    private int score = 0;
    private final int WIDTH = 1200;
    private final int HEIGHT = 600;

    private BufferedImage defaultInvaderIMG;
    private BufferedImage tankInvaderIMG;
    private BufferedImage diverInvaderIMG;
    private BufferedImage shooterInvaderIMG;

    public InvaderManager() {
        loadImages();
    }

    private void loadImages() {
        try {
            defaultInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderGREEN.png"));
            tankInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderRED.png"));
            diverInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderBLUE.png"));
            shooterInvaderIMG = ImageIO.read(getClass().getResource("/Image/InvaderPURPLE.png"));
            
            System.out.println("Images loaded:");
            System.out.println("Default: " + (defaultInvaderIMG != null));
            System.out.println("Tank: " + (tankInvaderIMG != null));
            System.out.println("Diver: " + (diverInvaderIMG != null));
            System.out.println("Shooter: " + (shooterInvaderIMG != null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeInvaders() {
        invaders.clear();

        if (score < 150) {
            createDefaultInvaders();
        } else if (score < 100) {
            createDefaultInvaders();
            createShooterInvaders();
        } else if (score < 100) {
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

    private void createShooterInvaders() {
        int y = 30;
        for (int i = 0; i < 5; i++) {
            int x = 100 + i * 200;
            invaders.add(new ShooterInvader(x, y));
        }
    }

    private void createTankInvaders() {
        
        int y = 250;
        for (int i = 0; i < 5; i++) {
            int x = 200 + i * 150;
            
            invaders.add(new TankInvader(x, y));
        }
    }

    private void createDiverInvaders() {
        invaders.add(new DiverInvader(100, 300));
        invaders.add(new DiverInvader(100, 500));

    }

    public void update() {

        for (Invader invader : invaders) {
            if (invader.isAlive()) {
                invader.update(WIDTH, 0, groupDirection, groupSpeed);
            }
        }

        for (Bullet bullet : enemyBullets) {
            bullet.update();
        }

        handleGroupMovement();
        handleEnemyShooting();
        
        invaders.removeIf((invader -> !invader.isAlive()));
        enemyBullets.removeIf(bullet -> bullet.getY() > HEIGHT);
    }

    private void handleGroupMovement() {
        boolean willHitWall = false;
        for (Invader invader : invaders) {
            if (invader.isAlive()) {
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

    private void handleEnemyShooting() {
        shootTick++;
        if (fireRate <= shootTick) {
            shootTick = 0;
        
            for (Invader invader : invaders) {
                if (invader.isAlive() && invader.canShoot() && Math.random() < 0.3) {
                    enemyBullets.add(new Bullet(invader.getX(), 
                        invader.getY() + invader.getWidth(), 2));
                }
            }
        }
    }

    public void draw(Graphics g) {
    
        for (Invader invader : invaders) {
            //if (!invader.isAlive()) continue;
                
            BufferedImage img = getInvaderImage(invader);
            if (img == null) {
                System.out.println("Image is null for " + invader.getClass().getSimpleName());
            }
        
            invader.draw(g, img);
        }
    }

    private BufferedImage getInvaderImage(Invader invader) {
        if (invader instanceof DefaultInvader) {
            return defaultInvaderIMG;
        } else if (invader instanceof TankInvader) {
            return tankInvaderIMG;
        } else if (invader instanceof  DiverInvader) {
            return diverInvaderIMG;
        } else if (invader instanceof  ShooterInvader) {
            return shooterInvaderIMG;
        }
        return defaultInvaderIMG;
    }

    public void checkCollision(ArrayList<Bullet> playerBullets) {
        for (int i  = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            if (bullet.getDy() < 0) {
                for (Invader invader : invaders) {
                    if (invader.isAlive() && isColliding(bullet, invader)) {
                        invader.takeHit();
                        playerBullets.remove(i);
                        score += 10;
                        break;
                    }
                }
            }
        }
    }

    public boolean checkPlayerHit(int playerX, int playerY, int playerWidth, int playerHeight) {
        for (Invader invader : invaders) {
            if (invader.isAlive() && enemyHitsPlayer(invader, 
                playerX, playerY, playerHeight, playerWidth)) {
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

    private boolean bulletHitsPlayer(Bullet bullet, int x, int y, int height, int width) {
        return bullet.getX() < x + width
            && bullet.getX() + bullet.getWidth() > x
            && bullet.getY() < y + height
            && bullet.getY() + bullet.getHeight() > y;
    }

    private boolean isColliding(Bullet bullet, Invader invader) {
        return bullet.getX() < invader.getX() + invader.getWidth()
            && bullet.getX() + bullet.getWidth() > invader.getX()
            && bullet.getY() < invader.getY() + invader.getHeight()
            && bullet.getY() + bullet.getHeight() > invader.getY();
    }

    private boolean enemyHitsPlayer(Invader invader, int x, int y, int width, int height) {
        return invader.getX() < x + width 
            && invader.getX() + invader.getWidth() > x
            && invader.getY() < y + height
            && invader.getY() + invader.getHeight() > y;
    }

    public ArrayList<Bullet> getEnemyBullets() {
        return enemyBullets;
    }

    public int getScore() {
        return score;
    }

    public boolean allDead() {
        return invaders.isEmpty();
    }
}
