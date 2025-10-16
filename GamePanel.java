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
    private BufferedImage invaderImg1;
    private BufferedImage invaderImg2;
    private BufferedImage invaderImg3;
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 600;
    private int playerX;
    private boolean playerAlive = true;
    private static final int playerY = 500;
    private final int playerWidth = 444 / 5;
    private final int playerHeight = 432 / 5;
    private Timer timer;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Invader> invaders = new ArrayList<>();
    private int groupDirection = 1;
    private int groupSpeed = 1;
    private int dropDistance = 20;
    private int enemyShootTick = 0;
    private int enemyShootForFrame = 45;
    

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);

        URL urlBg = getClass().getResource("/Image/bground.png");
        URL urlPl = getClass().getResource("/Image/player.png");
        URL urlInv1 = getClass().getResource("/Image/Ship/png/ship(3).png");
        URL urlInv2 = getClass().getResource("/Image/Ship/png/ship (7).png");
        URL urlInv3 = getClass().getResource("/Image/Ship/png/ship (10).png");

        try {
            backgroundImg = ImageIO.read(urlBg);
            playerImg = ImageIO.read(urlPl);
            invaderImg1 = ImageIO.read(urlInv1);
            invaderImg2 = ImageIO.read(urlInv2);
            invaderImg3 = ImageIO.read(urlInv3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerX = (WIDTH - playerWidth) / 2;
        this.timer = new Timer(15, this);
        timer.start();
        initializeInvaders();
    }

    public void initializeInvaders() {
        int rows = 3;
        int columns = 8;
        int spacingX = 80;
        int spacingY = 60;
        int startX = 50;
        int startY = 50;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int x = startX + j * spacingX;
                int y = startY + i * spacingY;
                int type = (i % 3) + 1;
                invaders.add(new Invader(x, y, type));
            }
        }
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
            for (Invader i : invaders) {
                if (!i.isAlive()) {
                    continue;
                }
                BufferedImage img = null;
                switch (i.getType()) {
                    case 1: img = invaderImg1;
                    break;
                    case 2: img = invaderImg2; 
                    break;
                    case 3: img = invaderImg3;
                    break;
                    default: 
                        img = invaderImg1;
                        break;
                }
                i.draw(g, img);
                
            }
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
        
            /*
            I was trying to make it have some delay between each press
            in order to preven spamming 'shot' key but i still really dont get it :)))
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            }*/
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
                    break;
                }
            }
        }
        if (invaders.isEmpty()) {
            groupSpeed += 2; 
            initializeInvaders();
        }
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
                if (Math.random() < 0.2) {
                    bullets.add(new Bullet(s.getX(), s.getY() + Invader.getHeight(), 2));
                }
            }

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
                    bullets.remove(i);
                }
            }
        }

        invaders.removeIf(inv -> !inv.isAlive());
        bullets.removeIf(b -> b.getY() < 0 || b.getY() > HEIGHT);
        
        repaint();
    }
}
