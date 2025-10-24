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
        void maybeShoot(Invader self, int tick);
        
        default boolean canShoot() {
            return false;
        }
        
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
