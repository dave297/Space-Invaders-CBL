import java.awt.*;
import javax.swing.*;

/**
 * Main application window that hosts the game and menu screens.
 * Uses a {@link CardLayout} to switch between the menu and the game.
 */
public class Window extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private GamePanel gamePanel = new GamePanel(this);
    private final GameMenu gameMenu;

    /**
     * Constructs the main window, initializes UI components, and shows the menu.
     * Sets window properties, creates the menu and game panels, 
     * and wires them into the card layout.
     */
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

    /**
     * Starts a new game.
     * Removes the old {@link GamePanel}, creates a new one, adds it to the card layout,
     * switches to the game view, and requests keyboard focus for game input.
     */
    public void startGame() {
        root.remove(gamePanel);
        gamePanel = new GamePanel(this);
        root.add(gamePanel, "game");
        cardLayout.show(root, "game");
        root.validate();
        root.repaint();
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    /**
     * Shows the main menu.
     * Switches the visible card to the menu and requests focus for the root container.
     */
    public void showMenu() {
        cardLayout.show(root, "menu");
        root.validate();
        root.repaint();
        SwingUtilities.invokeLater(() -> root.requestFocusInWindow());
    }

    /**
     * Application entry point.
     * Creates the window, 
     * then starts background music on a temporary {@link GamePanel} and loops it.
     */
    public static void main(String[] args) {
        Window a = new Window();
        GamePanel b = new GamePanel(a);
        b.play("Music/A _Lonely_Cherry_Tree.wav");
        b.loop();
    }
}
