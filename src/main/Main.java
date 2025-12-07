package main;

import javax.swing.JFrame;

public class Main {
    public static void main() {
        JFrame window = new JFrame();
        
        // This closes the app when you click the X button
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Code Drifter - Type to Race");
        
        // Add the GamePanel (our game engine)
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        
        // Fit the window size to the preferred size of the GamePanel
        window.pack();
        
        // Center on screen
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        // Start the game loop!
        gamePanel.startGameThread();
    }
}