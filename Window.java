import java.awt.*;
import javax.swing.*;

public class Window extends JFrame {

    private final CardLayout cardLayout = new CardLayout(); 
    private final JPanel root = new JPanel(cardLayout);
    private GamePanel gamePanel = new GamePanel(this);
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

    public void startGame() {
        root.remove(gamePanel);
        gamePanel = new GamePanel(this);
        root.add(gamePanel, "game");
        cardLayout.show(root, "game");
        root.validate();
        root.repaint();
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
        //System.out.println("I'm working");
        
    }

    public void showMenu() {
        cardLayout.show(root, "menu");
        root.validate();
        root.repaint();
        SwingUtilities.invokeLater(() -> root.requestFocusInWindow());
    }
    
    public static void main(String[] args) {
        Window a = new Window();
        GamePanel b = new GamePanel(a);
        b.play("Music/A _Lonely_Cherry_Tree.wav");
        b.loop();
    }
}
