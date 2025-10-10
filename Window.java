import javax.swing.*;

public class Window extends JFrame {
    private final GamePanel gamePanel = new GamePanel();


    public Window() {
        this.setTitle("Space Invaders");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setContentPane(gamePanel);
        pack();
        this.setLocationRelativeTo(null);
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        new Window();
    }
}
