import java.awt.*;
import java.awt.image.BufferedImage;

public class TankInvader extends Invader{

    public TankInvader(int startX, int startY) {
        super(startX, startY);
    }
    
    @Override
    public void takeHit() {
        health -= 1;
        if (health == 0) {
            super.kill();
        }
    }

    @Override
    public void draw(Graphics g, BufferedImage img) {
        if (!alive || g == null) {
            return;
        }

        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
            g.setColor(Color.WHITE);
            g.drawString("Tank", x, y);
        }
    }

    @Override
    public void update(int panelWidth, int tick, int groupDirection, int groupSpeed) {
        if (!alive) {
            return;
        }

        moveHorizontal(groupDirection * groupSpeed);

        if (x < 0) {
            x = 0;
        }
        if (x > panelWidth - width) {
            x = panelWidth - width;
        }
    }
    
    @Override
    public boolean canDropOnBounce() {
        return true;
    }
}
