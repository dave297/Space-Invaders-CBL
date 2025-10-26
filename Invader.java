import java.awt.*;
import java.awt.image.BufferedImage;


abstract class Invader {
    protected int x;
    protected int y;
    protected static final int WIDTH = 100;
    protected static final int HEIGHT = 100;
    protected static final int PANEL_HEIGHT = 800;
    protected static final int PANEL_WIDHT = 1200;
    protected boolean alive = true;
    protected int health = 1;
    protected int shoot;
    protected int move;
    
    protected MovementBehaviour movement;
    protected HealthModel healthModel;
    protected ShootingBehaviour shooting;

    /**
     * Starting position of invaders.
     */
    public Invader(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }
    
    public abstract void update(int panelWidth, int tick, int groupDirection, int groupSpeed);

    public interface MovementBehaviour {
        void updatePosition(Invader self, int panelWidth, 
            int tick, int groupDirection, int groupSpeed);
        
        default boolean wantsFormationDrop() {
            return true;
        }
    }

    public interface ShootingBehaviour {
        /**
         * Called each tick to give this behaviour a chance to fire.
         * Implementations should create projectiles or otherwise trigger
         * shooting using the provided invader as context.
         *
         * @param self the invader attempting to shoot
         * @param tick the current game tick or frame counter
         */
        void maybeShoot(Invader self, int tick);
        
        default boolean canShoot() {
            return false;
        }
        
        /**
         * Probability (0.0 - 1.0) that this invader will attempt to shoot
         * on a given tick. Used by higher-level logic to decide shooting.
         * Default is 0.0 (never).
         *
         * @return the probability of shooting on a tick
         */
        default double probability() {
            return 0.0;
        }
    }
    
    public interface HealthModel {
        void applyDamage(int amount);

        boolean isAlive();

        int currentHealth();
    }
    
    public abstract void draw(Graphics g, BufferedImage image);

    /**
     * Apply a single hit to this invader. If a HealthModel is attached,
     * damage is delegated to it; otherwise the invader is killed immediately.
     * If health reaches zero the invader's alive flag is set to false.
     */
    public void takeHit() {
        if (healthModel != null) {
            healthModel.applyDamage(1);
            if (!healthModel.isAlive()) {
                alive = false;
            }
        } else {
            alive = false;
        }
    }


    /**
     * Return the current configured shoot probability for this invader.
     * If no ShootingBehaviour is set, returns 0.0.
     *
     * @return shooting probability in range [0.0, 1.0]
     */
    public double getShootProbability() {
        if (shooting != null) {
            return shooting.probability();
        }
        return 0.0;
    }

    public boolean canDropOnBounce() {
        return movement.wantsFormationDrop();
    }

    public boolean canShoot() {
        return false;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public static final int getHeight() {
        return HEIGHT;
    }

    public static final int getWidth() {
        return WIDTH;
    }

    public final boolean isAlive() {
        return alive;
    }
    
    public final void kill() {
        alive = false;
    }

    public final void moveHorizontal(int dx) {
        x += dx;
    }

    public final void moveVertical(int dy) {
        y += dy;
    }

    
}
