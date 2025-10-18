import java.awt.*;
import java.awt.image.BufferedImage;


abstract class Invader {
    protected int x;
    protected int y;
    protected int width = 50;
    protected int height = 50;
    protected boolean alive = true;
    protected int health = 1;

    protected MovementBehaviour movement;
    protected HealthModel healthModel;
    protected ShootingBehaviour shooting;


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

    public final void update(int panelWidth, int tick, int groupDirection, int groupSpeed) {
        if (!alive) {
            return;
        }
        movement.updatePosition(this, panelWidth, tick, groupDirection, groupSpeed);
        shooting.maybeShoot(this, tick);
    }

    public void takeHit() {
        healthModel.applyDamage(1);
        if (!healthModel.isAlive()) {
            alive = false;
        }
    }

    public boolean canDropOnBounce() {
        return movement.wantsFormationDrop();
    }

    public Invader(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public boolean canShoot() {
        return false;
    }


    public double getShootProbability() {
        return 0.0;
    }

    public void moveHorizontal(int dx) {
        x += dx;
    }

    public void moveVertical(int dy) {
        y += dy;
    }

    public void draw(Graphics g, BufferedImage image) {
        if (!alive) {
            return;
        } 
        g.drawImage(image, x, y, width, height, null);
    }

    
}
