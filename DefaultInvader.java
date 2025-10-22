import java.awt.*;
import java.awt.image.BufferedImage;

public class DefaultInvader extends Invader{

    public DefaultInvader(int startX, int startY) {
        super(startX, startY);
    }
    
    
    @Override
    public boolean canDropOnBounce() {
        return true;
    }

    @Override
    public void takeHit() {
        super.kill();
    }

    @Override
    public void draw(Graphics g, BufferedImage img) {
        if (g == null) return;

        int drawX = this.x;
        int drawY = this.y;

        if (img != null) {
            g.drawImage(img, drawX, drawY, width, height, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Unable to load", 20, 20);
        }
    }
}
