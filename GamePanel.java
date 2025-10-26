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

/**
 * Game panel for a Space-Invaders style game.
 * Handles rendering, input, audio, timers, player state, 
 * and coordination with {@link InvaderManager}.
 */
public final class GamePanel extends JPanel implements KeyListener, ActionListener {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    private static final int PLAYER_Y = HEIGHT - 100;
    private static final long SHOT_COOLDOWN = 250;

    private final Window parent;
    private final int playerWidth;
    private final int playerHeight;
    private final ArrayList<Bullet> bullets;

    private BufferedImage backgroundImg;
    private BufferedImage playerImg;
    private Timer timer;
    private Font pixelFont;
    private Timer blinkTimer;
    private Clip clip;
    private InvaderManager invaderManager;

    private int playerX;
    private int playerLives;
    private long lastShotTime;

    private boolean playerAlive;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean isVisible;
    private boolean invulnerable;
    private boolean confirmRestart;

    /**
     * Creates a new game panel, initializes UI, loads resources, 
     * and sets the initial game state.
     *
     * @param parent the parent window that can switch back to the menu
     */
    public GamePanel(final Window parent) {
        this.parent = parent;
        this.playerWidth = 444 / 5;
        this.playerHeight = 432 / 5;
        this.bullets = new ArrayList<>();

        initializePanel();
        loadResources();
        resetGameState();
    }

    /**
     * Sets up Swing component properties and starts the main game timer.
     * Registers input listeners and constructs the {@link InvaderManager}.
     */
    private void initializePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);

        invaderManager = new InvaderManager();
        timer = new Timer(15, this);
        timer.start();
    }

    /**
     * Loads images and font resources from the classpath.
     * Falls back to a system font if the pixel font cannot be loaded.
     */
    private void loadResources() {
        URL urlBg = getClass().getResource("/Image/bground.png");
        URL urlPl = getClass().getResource("/Image/player.png");

        try {
            backgroundImg = ImageIO.read(urlBg);
            playerImg = ImageIO.read(urlPl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            pixelFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/fonts/pixel.ttf")
            ).deriveFont(24f);
        } catch (FontFormatException | IOException e) {
            pixelFont = new Font("Courier New", Font.BOLD, 24);
        }
    }

    /**
     * Resets transient player and UI state without recreating managers or timers.
     * Positions the player in the center and clears movement flags.
     */
    private void resetGameState() {
        playerX = (WIDTH - playerWidth) / 2;
        playerLives = 3;
        playerAlive = true;
        isVisible = true;
        invulnerable = false;
        leftPressed = false;
        rightPressed = false;
        confirmRestart = false;
        lastShotTime = 0;
    }

    /**
     * Plays an audio file once.
     */
    public void play(final String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops and releases any currently playing audio clip.
     */
    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    /**
     * Loops the current audio clip continuously.
     * Does nothing if no clip has been loaded.
     */
    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Paints the entire frame including background, player, bullets, invaders, and HUD.
     * Shows a fallback background if images failed to load 
     * and draws a Game Over overlay when lives are depleted.
     */
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (backgroundImg == null) {
            drawFallbackBackground(g);
            return;
        }

        drawGame(g);

        if (playerLives <= 0) {
            drawGameOver(g);
        }
    }

    /**
     * Draws a simple fallback background and status text when assets are unavailable.
     */
    private void drawFallbackBackground(final Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawString("Unable to load", 20, 20);
    }

    /**
     * Renders the full game layer stack in play state.
     * Order: background, player (if visible), player bullets, invaders, enemy bullets, HUD.
     */
    private void drawGame(final Graphics g) {
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);

        if (playerAlive && isVisible) {
            g.drawImage(playerImg, playerX, PLAYER_Y, playerWidth, playerHeight, null);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        invaderManager.draw(g);

        for (Bullet bullet : invaderManager.getEnemyBullets()) {
            bullet.draw(g);
        }

        drawHUD(g);
    }

    /**
     * Draws heads-up display elements such as score and lives using the pixel font.
     */
    private void drawHUD(final Graphics g) {
        g.setFont(pixelFont);
        g.setColor(Color.WHITE);

        String scoreText = "SCORE: " + invaderManager.getScore();
        String livesText = "Lives: " + Math.max(playerLives, 0);

        FontMetrics fm = g.getFontMetrics();
        int scoreX = WIDTH - fm.stringWidth(scoreText) - 20;

        g.drawString(scoreText, scoreX, 30);
        g.drawString(livesText, 30, 30);
    }

    /**
     * Draws the Game Over overlay and enables restart confirmation handling.
     */
    private void drawGameOver(final Graphics g) {
        String lose = "GAME OVER! PRESS 'SPACE' TO RESTART.";
        String giveUp = "PRESS 'ESC' TO MENU.";

        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getHeight();
        int y = (HEIGHT - textHeight) / 2 + fm.getAscent();

        g.drawString(lose, (WIDTH - fm.stringWidth(lose)) / 2, y);
        g.drawString(giveUp, (WIDTH - fm.stringWidth(giveUp)) / 2, y + 50);

        confirmRestart = true;
    }

    /**
     * Handles key presses for menu, movement, and shooting.
     * ESC always returns to the menu. A/D or arrow keys move the player. 
     * SPACE fires a bullet subject to cooldown.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Handle ESC key for menu (works anytime)
        if (key == KeyEvent.VK_ESCAPE) {
            timer.stop();
            parent.showMenu();
            return;
        }

        if (playerAlive) {
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                leftPressed = true;
            } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            } else if (key == KeyEvent.VK_SPACE) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShotTime >= SHOT_COOLDOWN) {
                    bullets.add(new Bullet(playerX + playerWidth / 2, PLAYER_Y, -8));
                    lastShotTime = currentTime;
                }
            }
        }
        if (confirmRestart && key == KeyEvent.VK_SPACE) {
            resetGame();
        } else if (confirmRestart && key == KeyEvent.VK_ESCAPE) {
            timer.stop();
            parent.showMenu();
        }
    }

    /**
     * Handles key releases for movement control flags.
     */
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
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Applies damage to the player.
     * If lives remain, respawns with temporary invulnerability and blinking. 
     * Otherwise stops the game and plays a death sound.
     */
    public void playerHit() {
        if (!playerAlive || invulnerable) {
            return;
        }

        playerLives--;

        if (playerLives > 0) {
            playerAlive = true;
            isVisible = true;
            invulnerable = true;
            playerX = (WIDTH - playerWidth) / 2;
            blinkOnRespawn(3, 120);
        } else {
            playerAlive = false;
            stopMusic();
            play("Music/death_sound_effect.wav");
            timer.stop();
            confirmRestart = true;
            repaint();
        }
    }

    /**
     * Respawns the player if there are remaining respawns allowed.
     */
    public void respawnPlayer(int n) {
        if (!playerAlive && n > 0) {
            playerAlive = true;
            isVisible = true;
            playerX = (WIDTH - playerWidth) / 2;
            invulnerable = true;
            blinkOnRespawn(3, 120);
        }
    }

    /**
     * Toggles player visibility to create a blinking effect during invulnerability after respawn.
     * The blink timer automatically restores visibility and disables invulnerability when finished.
     */
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

    /**
     * Fully resets the game session.
     * Clears bullets, reinitializes invaders and score, restores player state, 
     * and ensures the main timer is running.
     * Triggers a repaint at the end.
     */
    public void resetGame() {
        if (blinkTimer != null) {
            blinkTimer.stop();
            blinkTimer = null;
        }

        bullets.clear();
        invaderManager.initializeInvaders();
        invaderManager.reset();

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

    /**
     * Main game loop handler called by the Swing timer.
     * Updates player position, bullets, invaders, collision checks, wave resets, 
     * culls off-screen bullets, and repaints.
     */
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

        if (invaderManager.checkPlayerHit(playerX, PLAYER_Y, playerWidth, playerHeight)) {
            playerHit();
        }

        if (invaderManager.allDead()) {
            bullets.removeAll(bullets);
            invaderManager.initializeInvaders();
        }

        bullets.removeIf(b -> b.getY() < 0 || b.getY() > HEIGHT);
        repaint();
    }
}
