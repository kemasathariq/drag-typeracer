package main;

import entities.Car;
import inputs.KeyboardInputs;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    final int tileSize = 48;
    public final int screenWidth = 950;
    public final int screenHeight = 550;
    int FPS = 60;

    Thread gameThread;
    KeyboardInputs keyInputs;
    AssetManager assets;
    WordManager wordManager;
    
    Car player;
    ArrayList<Car> bots;
    
    // --- STATE GAME ---
    public boolean isRunning = true;
    public boolean isFinished = false; // Cek apakah balapan selesai
    public String winnerName = "";     // Siapa pemenangnya?

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        assets = new AssetManager();
        wordManager = new WordManager();
        keyInputs = new KeyboardInputs(this);
        
        setupRace();
        this.addKeyListener(keyInputs);
    }
    
    public void setupRace() {
        bots = new ArrayList<>();
        int startX = 20;
        int laneHeight = 40;
        int startY = 200;
        int finishLineX = screenWidth - 100;
        int trackDistance = finishLineX - startX;
        
        // Update Constructor Car dengan Nama
        Car bot1 = new Car("Blue Bot", startX, startY, assets.carBlue, false);
        bot1.setBotSpeed(30, 50, trackDistance);
        bots.add(bot1);
        
        Car bot2 = new Car("Pink Bot", startX, startY + laneHeight, assets.carPink, false);
        bot2.setBotSpeed(60, 50, trackDistance);
        bots.add(bot2);
        
        player = new Car("YOU", startX, startY + (laneHeight * 2), assets.carRed, true);
        
        Car bot3 = new Car("Yellow Bot", startX, startY + (laneHeight * 3), assets.carYellow, false);
        bot3.setBotSpeed(90, 50, trackDistance);
        bots.add(bot3);
    }
    
    // --- METHOD RESTART GAME ---
    public void restartGame() {
        isFinished = false;
        winnerName = "";
        
        // Reset Posisi Semua Mobil
        player.reset(20);
        for(Car bot : bots) {
            bot.reset(20);
        }
        
        // Reset Kata/Kalimat
        wordManager.generateNewSentence();
    }

    public WordManager getWordManager() { return wordManager; }
    public Car getPlayerCar() { return player; }

    public void updatePlayerMovement() {
        if (isFinished) return; // Kunci input jika game selesai

        int startX = 20;
        int finishLineX = screenWidth - 100;
        int trackLength = finishLineX - startX;
        
        player.updatePlayerPosition(wordManager.getProgressPercent(), startX, trackLength);
        
        // Cek jika kalimat selesai, generate baru (tapi balapan tetap jalan berdasarkan posisi)
        if (wordManager.isFinished()) {
             // Opsional: Kalau mau kalimat habis langsung gerak full, bisa logic lain.
             // Untuk sekarang kita biarkan player ngetik sampai mobil sampe finish.
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void update() {
        // Jika game selesai, stop update posisi (mobil berhenti)
        if (isFinished || !isRunning) return;
        
        int finishLineX = screenWidth - 100;

        // 1. Update Bots
        for (Car bot : bots) {
            bot.update();
            checkWin(bot, finishLineX);
        }
        
        // 2. Cek Player Win (Logic update posisi player ada di updatePlayerMovement)
        checkWin(player, finishLineX);
    }
    
    // Cek siapa yang melewati garis finish
    private void checkWin(Car car, int finishLineX) {
        if (isFinished) return; // Jika sudah ada pemenang, abaikan yang lain
        
        if (car.getX() >= finishLineX) {
            isFinished = true;
            winnerName = car.getName();
            System.out.println("WINNER: " + winnerName);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // --- DRAW GAME ---
        if (assets.background != null) {
            g2.drawImage(assets.background, 0, 0, screenWidth, 400, null);
        }

        g2.setColor(Color.WHITE);
        g2.fillRect(screenWidth - 90, 190, 10, 150); // Garis Finish

        for (Car bot : bots) bot.draw(g2);
        player.draw(g2);

        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 400, screenWidth, screenHeight - 400);
        wordManager.draw(g2, screenWidth, screenHeight);

        // --- DRAW GAME OVER OVERLAY ---
        if (isFinished) {
            // Layar Hitam Transparan
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            
            // Tulisan Pemenang
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            String msg = winnerName + " WINS!";
            
            // Logic Text Center
            FontMetrics fm = g2.getFontMetrics();
            int x = (screenWidth - fm.stringWidth(msg)) / 2;
            int y = screenHeight / 2 - 20;
            
            // Efek Shadow & Warna Text
            g2.setColor(Color.BLACK);
            g2.drawString(msg, x+3, y+3);
            if (winnerName.equals("YOU")) g2.setColor(Color.GREEN);
            else g2.setColor(Color.RED);
            g2.drawString(msg, x, y);
            
            // Tulisan Restart
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.WHITE);
            String subMsg = "Press ENTER to Restart";
            int subX = (screenWidth - g2.getFontMetrics().stringWidth(subMsg)) / 2;
            g2.drawString(subMsg, subX, y + 50);
        }

        g2.dispose();
    }
}