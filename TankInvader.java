import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An {@link Invader} variant with extra durability.
 * Starts with multiple hit points and only dies after
 * taking several hits. It draws itself using a provided sprite if available,
 * otherwise it falls back to a simple colored rectangle with a label.
 */
public class TankInvader extends Invader {

    /** Remaining hit points; the tank dies when this reaches zero. */
    int health = 3;

    /**
     * Creates a tank invader at the given starting position.
     */
    public TankInvader(int startX, int startY) {
        super(startX, startY);
    }

    /**
     * Applies one unit of damage to this invader.
     * When health drops to zero, the invader is killed via {@link #kill()} (inherited).
     */
    @Override
    public void takeHit() {
        health -= 1;
        if (health == 0) {
            super.kill();
        }
    }

    /**
     * Renders the invader.
     *   If {@code img} is non-null, draws the sprite scaled to {@link #WIDTH} × {@link #HEIGHT}.
     *   Otherwise, draws a fallback rectangle with a "Tank" label.
     * If the invader is not alive, nothing is drawn.
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
            g.drawString("Tank", x, y);
        }
    }

    /**
     * Updates horizontal movement each tick according to the group's direction and speed,
     * and clamps the invader inside the panel's horizontal bounds.
     * No update occurs if the invader is not alive.
     */
    @Override
    public void update(int panelWidth, int tick, int groupDirection, int groupSpeed) {
        if (!alive) {
            return;
        }

        moveHorizontal(groupDirection * groupSpeed);

        if (x < 0) {
            x = 0;
        }
        if (x > panelWidth - WIDTH) {
            x = panelWidth - WIDTH;
        }
    }

    /**
     * Indicates whether this invader type should drop down when the group bounces on a wall.
     */
    @Override
    public boolean canDropOnBounce() {
        return true;
    }
}
