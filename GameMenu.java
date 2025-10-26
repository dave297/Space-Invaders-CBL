import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Represents the main menu panel of the game.
 * Displays a background image and a button that allows the player to start the game.
 */
public class GameMenu extends JPanel {
    private BufferedImage backgroundImg;

    /**
     * Constructs a new game menu and sets up the layout, background image, and start button.
     *
     * @param w the parent {@link Window}
     */
    public GameMenu(Window w) {
        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        URL urlBg = getClass().getResource("/Image/bground.png");
        try {
            backgroundImg = ImageIO.read(urlBg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JButton startGame = new JButton("Start Game!");
        this.setLayout(null);
        int btX = (GamePanel.WIDTH - 150) / 2;
        int btY = (GamePanel.HEIGHT - 50) / 2;
        startGame.setBounds(btX, btY, 150, 50);
        startGame.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(startGame);

        // Start the game when the button is clicked
        startGame.addActionListener(e -> w.startGame());
        setVisible(true);
    }

    /**
     * Paints the menu screen background image.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
