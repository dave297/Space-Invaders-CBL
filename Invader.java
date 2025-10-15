import java.awt.*;
import java.awt.image.BufferedImage;


public class Invader {
    private BufferedImage invaderImageType1;
    private BufferedImage invaderImageType2;
    private BufferedImage invaderImageType3;
    private int x;
    private int y;
    private int type;
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private boolean alive = true;

    public Invader(int startX, int startY, int type) {
        this.x = startX;
        this.y = startY;
        this.type = type;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getType() {
        return this.type;
    }

    public static int getWidth() {
        return WIDTH;
    }
    
    public static int getHeight() {
        return HEIGHT;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public void moveHorizontal(int dx) {
        x += dx;
    }

    public void moveVertical(int dy) {
        y += dy;
    }

    public void draw(Graphics g, BufferedImage image) {
        if (!alive) {
            return;
        } 
        g.drawImage(image, x, y, WIDTH, HEIGHT, null);
        }

    
}
