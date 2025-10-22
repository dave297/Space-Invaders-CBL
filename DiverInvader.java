import java.awt.*;
import java.awt.image.BufferedImage;

public class DiverInvader extends Invader {
    public DiverInvader(int startX, int startY) {
        super(startX, startY);
    }

    @Override
    public void update(int panelWidth, int tick, int groupSpeed, int groupDirection) {
        if (!alive) {
            return;
        }

        moveVertical(2);

        if (y > 600) {
            alive = false;
        }
    }

    @Override
    public void draw(Graphics g, BufferedImage img) {
        if (!alive || g == null) {
            return;
        }
        if (image != null) {
            g.drawImage(img, x, y, width, height, null);
        } else { 
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
            g.setColor(Color.WHITE);
            g.drawString("Diver", x, y);
        }
    }
}
