package main;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
    
    private JFrame window;
    private JPanel mainContainer;
    private CardLayout cardLayout;
    
    private MainMenuPanel menuPanel;
    private GamePanel gamePanel;
    private AssetManager assets;

    public Main() {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("TypeRace: Pixel Edition");

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        assets = new AssetManager();
        
        menuPanel = new MainMenuPanel(this, assets);
        gamePanel = new GamePanel(assets, this); 
        
        mainContainer.add(menuPanel, "MENU"); // Beri nama "MENU"
        mainContainer.add(gamePanel, "GAME"); // Beri nama "GAME"
        
        window.add(mainContainer);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        cardLayout.show(mainContainer, "MENU");
    }
    
    public void startGame(String difficulty) {
        gamePanel.setupRace(difficulty); 
        gamePanel.startRace();

        cardLayout.show(mainContainer, "GAME");

        gamePanel.requestFocusInWindow();
    }

    public void showMenu() {
        cardLayout.show(mainContainer, "MENU");
        menuPanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new Main();
    }
}