import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
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
    private int playerLives = 3;
    private boolean isVisible = true;
    private Timer blinkTimer;
    private boolean invulnerable = false;
    private boolean confirmRestart = false;
    private final Window parent;
    private Clip clip;

    private InvaderManager invaderManager;

    public GamePanel() {
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
            invaderImg1 = ImageIO.read(urlInv1);
            invaderImg2 = ImageIO.read(urlInv2);
            invaderImg3 = ImageIO.read(urlInv3);


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
        initializeInvaders();
    }

    public void play(String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void updateScore(int score) {

    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (this.backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            if (playerAlive && isVisible) {
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
            String scoreText = "SCORE: " + score;
            String availLives = "";
            if (playerLives > 0) {
                availLives = "Lives: " + playerLives;
            } else {
                availLives = "Lives: " + 0;
            }
            FontMetrics fm = g.getFontMetrics();
            int scoreX = WIDTH - fm.stringWidth(scoreText) - 20;
            g.drawString(scoreText, scoreX, 30);
            g.drawString(availLives, 30, 30);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Unable to load", 20, 20);
        }


        if (playerLives < 0) {
            String lose = "GAME OVER! PRESS 'SPACE' TO RESTART.";
            String giveUp = "PRESS 'ESC' TO MENU.";
            FontMetrics fm = g.getFontMetrics();
            int textWidthLose = fm.stringWidth(lose);
            int textWidthGiveUp = fm.stringWidth(giveUp);
            int textHeight = fm.getHeight();
            int xLose = (WIDTH - textWidthLose) / 2;
            int xGiveUp = (WIDTH - textWidthGiveUp) / 2;
            int y = (HEIGHT - textHeight) / 2 + fm.getAscent();


            g.drawString(lose, xLose, y);
            g.drawString(giveUp, xGiveUp, y + 50);
            confirmRestart = true;
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
        
            /*
            I was trying to make it have some delay between each press
            in order to preven spamming 'shot' key but i still really dont get it :)))
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            }*/
        }
        if (confirmRestart && key == KeyEvent.VK_SPACE) {
            resetGame();
        } else if (confirmRestart && key == KeyEvent.VK_ESCAPE) {
            timer.stop();
            parent.showMenu();
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
    
    
    public void willHitWall() {
        boolean willHitNext = false;
        for (Invader i : invaders) {
            int nextLeft = i.getX() + groupDirection * groupSpeed;
            int nextRight = nextLeft + Invader.getWidth();
            if (nextLeft <= 0 || nextRight >= WIDTH) {
                willHitNext = true;
                break;
            }
        }
        if (willHitNext) {
            groupDirection *= -1;
            for (Invader i : invaders) {
                i.moveVertical(Invader.getHeight() / 5);
            }
        } else {
            for (Invader i : invaders) {
                i.moveHorizontal(groupDirection * groupSpeed);
            }
        }
    }

    public void invaderGetsHit() {
        for (int bul = bullets.size() - 1; bul >= 0; bul--) {
            Bullet b = bullets.get(bul);
            boolean consumed = false;
            for (Invader i: invaders) {
                if (!i.isAlive()) {
                    continue;
                }
                boolean intersects = 
                    b.getX() < i.getX() + Invader.getWidth() 
                    && b.getX() + b.getWidth() > i.getX() 
                    && b.getY() < i.getY() + Invader.getHeight() 
                    && b.getY() + b.getHeight() > i.getY();

                if (intersects && b.getDy() < 0) {
                    i.kill();
                    bullets.remove(b);
                    consumed = true;
                    score += 10;    
                    break;
                }
            }
        }
        if (invaders.isEmpty()) {
            groupSpeed += 2;
            score += 50;
            initializeInvaders();
        }
    }
    public void enemyShoot() {
        enemyShootTick++;
        if (enemyShootForFrame <= enemyShootTick) {
            enemyShootTick = 0;

            int minYvalue = Integer.MAX_VALUE;
            for (Invader inv : invaders) {
                if (inv.getY() < minYvalue && inv.isAlive()) {
                    minYvalue = inv.getY();
                }
            }

            ArrayList<Invader> shooters = new ArrayList<>();
            for (Invader inv : invaders) {
                if (inv.isAlive() && inv.getY() == minYvalue) {
                    shooters.add(inv);
                }
            }

            for (Invader s : shooters) {
                if (Math.random() < 0.5) {
                    bullets.add(new Bullet(s.getX(), s.getY() + Invader.getHeight(), 5));
                }
            }

        }
    }

    public void playerHit() {
        if (!playerAlive || invulnerable) {
            return;
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            
            if (b.getDy() > 0 && playerAlive) {
                boolean hitPlayer = 
                    b.getX() < playerX + playerWidth
                    && b.getX() + b.getWidth() > playerX
                    && b.getY() < playerY + playerHeight
                    && b.getY() + b.getHeight() > playerY;
                if (hitPlayer) {
                    playerAlive = false;
                    respawnPlayer(playerLives);
                    playerLives--;
                    bullets.remove(i);
                    
                }
                if (playerLives < 0) {
                    play("Music/death_sound_effect.wav");
                    timer.stop();
                } 
            }
        }
    }

    public void respawnPlayer(int n) {
        if (!playerAlive && n > 0) {
            playerAlive = true;
            isVisible = true;
            playerX = (WIDTH - playerWidth) / 2;
            invulnerable = true;
            blinkOnRespawn(3, 120);
        }
    }

    public void blinkOnRespawn(int flashes, int intervalMs) {
        if (blinkTimer != null) {
            blinkTimer.stop();
            blinkTimer = null;
        }

        final int[] togglesLeft = {Math.max(0, flashes) * 2};
        blinkTimer = new Timer(intervalMs, e -> {
            isVisible = !isVisible;
            togglesLeft[0]--;
            if (togglesLeft[0] <= 0) {
                ((Timer) e.getSource()).stop();
                blinkTimer = null;
                isVisible = true;
                invulnerable = false;
            }
            repaint();
        });
        blinkTimer.setInitialDelay(0);
        blinkTimer.start();
    }

    public void resetGame() {
        if (blinkTimer != null) {
            blinkTimer.stop();
            blinkTimer = null;
        }

        bullets.clear();
        invaders.clear();
        initializeInvaders();

        groupDirection = 1;
        groupSpeed = 1;
        dropDistance = 20;
        enemyShootTick = 0;
        enemyShootForFrame = 45;
        score = 0;
        playerLives = 3;
        playerAlive = true;
        invulnerable = false;
        isVisible = true;
        leftPressed = false;
        rightPressed = false;
        confirmRestart = false;
        playerX = (WIDTH - playerWidth) / 2;

        if (timer == null) {
            timer = new Timer(15, this);
            timer.start();
        } else if (!timer.isRunning()) {
            timer.start();
        }

        repaint();
    }

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
