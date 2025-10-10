import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GamePanel extends JPanel {
    private BufferedImage backgroundImg;  
    private BufferedImage playerImg; 
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);

        URL urlBg = getClass().getResource("/Image/bground.png");
        URL urlPl = getClass().getResource("/Image/player.png");
        try {
            backgroundImg = ImageIO.read(urlBg);
            playerImg = ImageIO.read(urlPl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int playerWidth = 444 / 5;
        int playerHeight = 432 / 5;
        int playerX = (WIDTH - playerWidth) / 2;
        int playerY = 500;
        if (this.backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            g.drawImage(playerImg, playerX, playerY, playerWidth, playerHeight, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Unable to load", 20, 20);
            
        }
        
    }
}
