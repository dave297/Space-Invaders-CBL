import java.awt.*;
import java.awt.image.BufferedImage;

public class ShooterInvader extends Invader {

    public ShooterInvader(int startX, int startY) {
        super(startX, startY);
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
            g.drawString("Shooter", x, y);
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

        if (tick % 60 == 0 && Math.random() < 0.1) {
            //shoot
        }
    }

    @Override
    public boolean canShoot() {
        return true;
    }

    @Override
    public boolean canDropOnBounce() {
        return false;
    }
}