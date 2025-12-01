package main;

import entities.PlayerCar;
import inputs.KeyboardInputs;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3; 
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    
    // 16:9 Aspect Ratio
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 9;
    public final int screenWidth = tileSize * maxScreenCol;  // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 432 pixels

    // FPS
    int FPS = 60;

    // SYSTEM
    Thread gameThread;
    KeyboardInputs keyInputs;
    
    // GAME OBJECTS
    WordManager wordManager;
    PlayerCar playerCar;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true); // IMPORTANT: Allows the panel to "listen" to keys

        // 1. Initialize Managers & Entities
        wordManager = new WordManager();
        playerCar = new PlayerCar(50, 200); // Start the car at x=50, y=200
        keyInputs = new KeyboardInputs(this);

        // 2. Add the Listener
        this.addKeyListener(keyInputs);
    }

    // --- GETTERS (So KeyboardInputs can talk to these objects) ---
    public WordManager getWordManager() {
        return wordManager;
    }

    public PlayerCar getPlayerCar() {
        return playerCar;
    }

    // --- GAME LOOP ---
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            
            // 1. UPDATE
            update();

            // 2. DRAW
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        // Update the car's physics (friction, movement) every frame
        playerCar.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // DRAW ORDER MATTERS!
        
        // 1. Draw Player Car (Red Box)
        playerCar.draw(g2);

        // 2. Draw Text UI on top
        wordManager.draw(g2);

        g2.dispose();
    }
}