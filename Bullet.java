import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Bullet {
    private BufferedImage bulletImg;
    private int x;
    private int y;
    
    public Bullet(int startX, int startY) {
        URL urlBl = getClass().getResource("/Image/bullet.png");
        this.x = startX;
        this.y = startY;
        try {
            bulletImg = ImageIO.read(urlBl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        this.y -= 5;
    }

    protected void draw(Graphics g) {
        g.drawImage(bulletImg, x + 37, y, null);
    }
}
