import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private BufferedImage backgroundImg;  
    private BufferedImage playerImg; 
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    private int playerX;
    private boolean playerAlive = true;
    private static final int playerY = HEIGHT - 100;
    private final int playerWidth = 444 / 5;
    private final int playerHeight = 432 / 5;
    private Timer timer;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private Font pixelFont;

    private InvaderManager invaderManager;
    private final Window parent;

    public GamePanel() {
        this.parent = null;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);
        invaderManager = new InvaderManager();
        invaderManager.initializeInvaders();

        URL urlBg = getClass().getResource("/Image/bground.png");
        URL urlPl = getClass().getResource("/Image/player.png");

        try {
            backgroundImg = ImageIO.read(urlBg);
            playerImg = ImageIO.read(urlPl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
            getClass().getResourceAsStream("/fonts/pixel.ttf")).deriveFont(24f);
        } catch (Exception e) {
            pixelFont = new Font("Courier New", Font.BOLD, 24);
        }
        playerX = (WIDTH - playerWidth) / 2;
        this.timer = new Timer(15, this);
        timer.start();
    }

    public GamePanel(Window parent) {
        this.parent = parent;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);
        invaderManager = new InvaderManager();
        invaderManager.initializeInvaders();

        URL urlBg = getClass().getResource("/Image/bground.png");
        URL urlPl = getClass().getResource("/Image/player.png");

        try {
            backgroundImg = ImageIO.read(urlBg);
            playerImg = ImageIO.read(urlPl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
            getClass().getResourceAsStream("/fonts/pixel.ttf")).deriveFont(24f);
        } catch (Exception e) {
            pixelFont = new Font("Courier New", Font.BOLD, 24);
        }
        playerX = (WIDTH - playerWidth) / 2;
        this.timer = new Timer(15, this);
        timer.start();
    }

    public void play(String filePath) {
        // Placeholder for audio playback
    }

    public void loop() {
        // Placeholder for audio looping
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (this.backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            if (playerAlive) {
                g.drawImage(playerImg, playerX, playerY, playerWidth, playerHeight, null);
            }
            for (Bullet b: bullets) {
                b.draw(g);
            }
            
            invaderManager.draw(g);

            for (Bullet b : invaderManager.getEnemyBullets()) {
                b.draw(g);
            }

            g.setFont(pixelFont);
            g.setColor(Color.WHITE);
            String scoreText = "SCORE: " + invaderManager.getScore();
            FontMetrics fm = g.getFontMetrics();
            int scoreX = WIDTH - fm.stringWidth(scoreText) - 20;
            g.drawString(scoreText, scoreX, 30);
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
        if (playerAlive) {
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                leftPressed = true;
            } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            } else if (key == KeyEvent.VK_SPACE) {
                bullets.add(new Bullet(playerX, playerY - 10, -5));
            }
        }
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
    public void keyTyped(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (leftPressed && playerX > 0 && playerAlive) {
            playerX -= 5;
        } else if (rightPressed && playerX < WIDTH - playerWidth && playerAlive) {
            playerX += 5;
        }
        for (Bullet b: bullets) {
            b.update();
        }

        invaderManager.update();
        invaderManager.checkCollision(bullets);

        if (invaderManager.checkPlayerHit(playerX, playerY, playerWidth, playerHeight)) {
            playerAlive = false;
        }

        if (invaderManager.allDead()) {
            bullets.removeAll(bullets);
            invaderManager.initializeInvaders();
        }

        bullets.removeIf(b -> b.getY() < 0 || b.getY() > HEIGHT);
        repaint();
    }
}