import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private BufferedImage backgroundImg;  
    private BufferedImage playerImg; 
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private int playerX;
    private static final int playerY = 500;
    private final int playerWidth = 444 / 5;
    private final int playerHeight = 432 / 5;
    private Timer timer;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);

        URL urlBg = getClass().getResource("/Image/bground.png");
        URL urlPl = getClass().getResource("/Image/player.png");
        try {
            backgroundImg = ImageIO.read(urlBg);
            playerImg = ImageIO.read(urlPl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerX = (WIDTH - playerWidth) / 2;
        Timer timer = new Timer(20, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
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

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            leftPressed = true;
        } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            leftPressed = false;
        } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (leftPressed) {
            playerX -= 5;
        } else if (rightPressed) {
            playerX += 5;
        }
        repaint();
    }
}
