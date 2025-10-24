import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Bullet {
    private BufferedImage bulletImg;
    private int x;
    private int y;
    private int dy;
    
    public Bullet(int startX, int startY, int dy) {
        URL urlBl = getClass().getResource("/Image/bullet.png");
        this.x = startX;
        this.y = startY;
        this.dy = dy;
        try {
            bulletImg = ImageIO.read(urlBl);
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }

    public void update() {
        this.y += dy;
    }


    protected void draw(Graphics g) {
        g.drawImage(bulletImg, x, y, null);
    }
    
    // Getters for collision detection
    public int getX() {
        return x + 37; 
    } // Account for the offset in drawing

    public int getY(){
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
