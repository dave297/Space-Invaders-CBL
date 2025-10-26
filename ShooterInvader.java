import java.awt.*;
import java.awt.image.BufferedImage;

public class ShooterInvader extends Invader {

    private static int direction = 1;
    private static final int speed = 1;
    private static boolean hitWall = false;

    public ShooterInvader(int startX, int startY) {
        super(startX, startY);
    }

    @Override
    public void draw(Graphics g, BufferedImage img) {

        if (!alive || g == null) {
            return;
        }
        if (img != null) {
            g.drawImage(img, x, y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.drawString("Shooter", x, y);
        }   
    }

    @Override
    public void update(int panelWidth, int tick, int groupDirection, int groupSpeed) {
        if (!alive) {
            return;
        }

        // Move independently using the invader's own direction and speed
        int nextX = x + speed * direction;
    
        // Check if hitting walls
        if (nextX <= 0 || nextX >= panelWidth - WIDTH) {
            direction *= -1; // Reverse direction when hitting walls
        }

        // Move with the invader's own direction and speed
        moveHorizontal(direction * speed);

        // Keep within bounds
        if (x < 0) {
            x = 0;
            direction *= -1;
        }  
        if (x > panelWidth - WIDTH) {
            x = panelWidth - WIDTH;
            direction *= -1;
        }

    }

    public static void reverseDirection() {
        direction *= -1;
    }

    public static boolean shooterHitWall() {
        return hitWall;
    } 

    @Override
    public boolean canShoot() {
        return true;
    }

    @Override
    public boolean canDropOnBounce() {
        return false;
    }
}