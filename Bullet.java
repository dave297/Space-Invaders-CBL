import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Represents a bullet fired either by the player or an invader.
 * Handles image loading, position updates, and drawing on the game screen.
 */
public class Bullet {
    private static BufferedImage PLAYER_BULLET_IMG;
    private static BufferedImage INVADER_BULLET_IMG;

    private final BufferedImage bulletImg;
    private final int x;
    private int y;
    private final int dy;

    static {
        try {
            PLAYER_BULLET_IMG = ImageIO.read(Bullet.class.getResource("/Image/bullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            INVADER_BULLET_IMG =
                ImageIO.read(Bullet.class.getResource("/Image/invaders_bullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a bullet with a given starting position and vertical velocity.
     * Automatically assigns the correct image depending on the direction (player or invader).
     */
    public Bullet(int startX, int startY, int dy) {
        this.x = startX;
        this.y = startY;
        this.dy = dy;
        this.bulletImg =
            (dy > 0 && INVADER_BULLET_IMG != null) ? INVADER_BULLET_IMG : PLAYER_BULLET_IMG;
    }

    /**
     * Updates the bullet's position by adding its vertical speed 
     */
    public void update() {
        this.y += dy;
    }

    /**
     * Draws the bullet image at its current position.
     */
    protected void draw(Graphics g) {
        g.drawImage(bulletImg, x, y, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDy() {
        return dy;
    }

    public int getWidth() {
        return bulletImg != null ? bulletImg.getWidth() : 10;
    }

    public int getHeight() {
        return bulletImg != null ? bulletImg.getHeight() : 20;
    }
}
