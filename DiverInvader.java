import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A specialized Invader that performs a diving attack pattern.
 * After spawning, the DiverInvader vibrates in place for a short delay,
 * then rapidly dives straight down towards the player. The invader
 * self-destructs when reaching the bottom of the screen.
 */
public class DiverInvader extends Invader {
    private long creationTime;
    private static final int DLMS = 1500;
    private boolean timingInit = false;
    private long lastVibration = 0;
    private static final long VIDU = 100;
    private boolean vibrateDirection = true;

    /**
     * Creates a new DiverInvader at the specified starting position.
     * This invader type waits for a delay period while vibrating,
     * then dives straight down towards the player.
     */
    public DiverInvader(int startX, int startY) {
        super(startX, startY);
    }

    /**
     * Updates the diver invader's state and position.
     * The invader will self-destruct when reaching near the bottom of the panel.
     */
    @Override
    public void update(int panelWidth, int tick, int groupSpeed, int groupDirection) {
        if (!alive) {
            return;
        }

        if (!timingInit) {
            creationTime = System.currentTimeMillis();
            timingInit = true;
            return;
        }
        long currentTime = System.currentTimeMillis();

        if (currentTime - creationTime < DLMS) {
            if (currentTime - lastVibration >= VIDU) {
                if (vibrateDirection) {
                    moveHorizontal(4);
                    vibrateDirection = false;
                } else {
                    moveHorizontal(-4);
                    vibrateDirection = true;
                }
                lastVibration = currentTime;
            }


            return;
        }
        moveVertical(7);

        if (y > PANEL_HEIGHT - 50) {
            alive = false;
        }
    }

    /**
     * Draws this diver invader to the screen. Uses the provided image if available,
     * otherwise falls back to a simple rectangle with text representation.
     */
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
            g.drawString("Diver", x, y);
        }
    }

    /**
     * Indicates whether this invader should drop when the formation bounces.
     */
    @Override
    public boolean canDropOnBounce() {
        return false;
    }
}
