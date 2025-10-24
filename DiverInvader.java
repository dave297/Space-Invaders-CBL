import java.awt.*;
import java.awt.image.BufferedImage;

public class DiverInvader extends Invader {
    private long creationTime;
    private final int DELAY_MS = 750;
    private boolean timingInit = false;
    private long lastVibration = 0;
    private final long VIBRATION_DURATION = 100;
    private boolean vibrateDirection = true;

    public DiverInvader(int startX, int startY) {
        super(startX, startY);
    }

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

        if (currentTime - creationTime < DELAY_MS) {
            if (currentTime - lastVibration >= VIBRATION_DURATION) {
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

    @Override
    public boolean canDropOnBounce() {
        return false;
    }
}