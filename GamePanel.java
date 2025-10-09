import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GamePanel extends JPanel {
    private BufferedImage backgroundImg;    
    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setDoubleBuffered(true);

        String resPath = "/Image/background.png";
        URL url = getClass().getResource(resPath);
        System.out.println(url);
        if (url != null) {
            try {
                backgroundImg = ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("KHÔNG TÌM THẤY ẢNH: /Image/test.jpg");
}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Không tải được ảnh nền", 20, 20);
        }
        
    }
}
