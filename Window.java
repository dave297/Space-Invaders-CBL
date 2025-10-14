import java.awt.*;
import javax.swing.*;

public class Window extends JFrame {

    private final CardLayout cardLayout = new CardLayout(); 
    private final JPanel root = new JPanel(cardLayout);
    private GamePanel gamePanel = new GamePanel();
    private GameMenu gameMenu;


    public Window() {
        this.setTitle("Space Invaders");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gameMenu = new GameMenu(this);
        root.add(gameMenu, "menu");
        root.add(gamePanel, "game");
        this.setContentPane(root);
        cardLayout.show(root, "menu");
        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void StartGame() {
        gamePanel = new GamePanel();
        root.add(gamePanel, "game");
        cardLayout.show(root, "game");
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
        //System.out.println("I'm working");
        root.validate();
    }
    
    public static void main(String[] args) {
        Window a = new Window();
    }
}
