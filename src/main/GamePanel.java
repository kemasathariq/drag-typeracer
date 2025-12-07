package main;

import entities.Car;
import inputs.KeyboardInputs;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    // --- SCREEN SETTINGS ---
    final int tileSize = 48;
    public final int screenWidth = 950;
    public final int screenHeight = 550;
    int FPS = 60;

    // --- SYSTEM ---
    Thread gameThread;
    KeyboardInputs keyInputs;
    AssetManager assets;
    WordManager wordManager;
    private Main mainFrame;
    
    // --- ENTITIES ---
    Car player;
    ArrayList<Car> bots;
    
    // --- GAME STATE ---
    public boolean isRunning = true;
    public boolean isFinished = false; 
    public String winnerName = "";
    
    private long startTime;
    private boolean raceStarted = false;
    
    private Random rng = new Random();

    public GamePanel(AssetManager assets, Main mainFrame) {
        this.mainFrame = mainFrame;
        this.assets = assets; // Gunakan asset yang dilempar dari Main
        
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        wordManager = new WordManager();
        keyInputs = new KeyboardInputs(this);
        
        // Jangan langsung setupRace() di sini, karena belum tau difficulty-nya
        // setupRace("Easy"); // Default sementara
        
        this.addKeyListener(keyInputs);
    }
    
    // --- SETUP & RESTART ---
    public void setupRace(String difficulty) {
        bots = new ArrayList<>();
        int startX = 20;
        int laneHeight = 40;
        int startY = 220;
        
        int finishLineX = screenWidth - 100;
        int trackDistance = finishLineX - startX;
        
        // Atur Kecepatan Berdasarkan Pilihan Menu
        int fixedWPM = 50; // Default Medium
        if(difficulty.equals("Easy")) fixedWPM = 25;
        else if (difficulty.equals("Hard")) fixedWPM = 80;
        else if (difficulty.equals("Insane")) fixedWPM = 110;

        // Reset Bot
        Car bot1 = new Car("Blue Bot", startX, startY, assets.carBlue, false);
        bots.add(bot1);
        
        // Lane 2 (Tengah): PLAYER
        player = new Car("YOU", startX, startY + laneHeight, assets.carRed, true);
        
        // Lane 3 (Bawah): Bot B
        Car bot2 = new Car("Yellow Bot", startX, startY + (laneHeight * 2), assets.carYellow, false);
        bots.add(bot2);
        
        boolean bot1IsHybrid = rng.nextBoolean();
        
        if (bot1IsHybrid) {
            // KONFIGURASI A: Bot 1 = Hybrid, Bot 2 = Fixed
            bot1.setHybrid(true);
            bot1.setDirectSpeed(0); // Speed awal 0, nanti ikut player
            
            bot2.setHybrid(false);
            bot2.setBotSpeed(fixedWPM, 50, trackDistance); // Speed tetap
            
            System.out.println("Role: Blue=Hybrid, Yellow=Fixed (" + fixedWPM + " WPM)");
        } else {
            // KONFIGURASI B: Bot 1 = Fixed, Bot 2 = Hybrid
            bot1.setHybrid(false);
            bot1.setBotSpeed(fixedWPM, 50, trackDistance); // Speed tetap
            
            bot2.setHybrid(true);
            bot2.setDirectSpeed(0); // Speed awal 0
            
            System.out.println("Role: Blue=Fixed (" + fixedWPM + " WPM), Yellow=Hybrid");
        }
        
        // Reset State
        isFinished = false;
        winnerName = "";
        raceStarted = false;
        wordManager.generateNewSentence();
        
        // Repaint sekali biar posisi mobil update
        repaint();
    }
    
    public void startRace() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
        isRunning = true;
    }
    
    public void startRaceTimer() 
    {
        if (!raceStarted) {
            startTime = System.currentTimeMillis();
            raceStarted = true;
        }
    }
    
    public void restartGame() {
        mainFrame.startGame("Medium"); // Sementara hardcode atau simpan variable
    }

    // --- GETTERS ---
    public WordManager getWordManager() { return wordManager; }
    public Car getPlayerCar() { return player; }

    // --- GAME LOGIC ---
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
        if (isFinished || !isRunning) return;
        
        int finishLineX = screenWidth - 100;
        int trackLength = finishLineX - 20;
        
        for (Car bot : bots) {
            // Jika bot ini Hybrid, update speednya dulu mengikuti player
            if (bot.isHybrid()) {
                updateHybridBot(bot, trackLength);
            }
            
            bot.update(); // Gerakkan mobil (baik fixed maupun hybrid)
            checkWin(bot, finishLineX);
        }
        
        checkWin(player, finishLineX);
    }
    
    private void updateHybridBot(Car hybridBot, int trackLength) {
        if (!raceStarted) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < 1000) return; // Tunggu 1 detik

        // Hitung WPM Player
        int charsTyped = wordManager.getTotalCharsTypedCorrectly();
        double minutes = elapsedTime / 60000.0;
        double playerCurrentWPM = (charsTyped / 5.0) / minutes;

        // Controller Hybrid: Sedikit lebih cepat/lambat acak biar natural
        // Kita buat dia fluktuatif antara 95% - 105% speed player
        double factor = 0.95 + (Math.random() * 0.1); 
        
        double targetSpeed = 0;
        if (playerCurrentWPM > 0) {
            double totalWords = 50; 
            double estimatedFrames = ((totalWords / (playerCurrentWPM * factor)) * 60.0) * 60;
            if (estimatedFrames > 0) {
                 targetSpeed = (double) trackLength / estimatedFrames;
            }
        }
        hybridBot.setDirectSpeed(targetSpeed);
    }
    
    // Dipanggil dari KeyboardInputs saat mengetik benar
    public void updatePlayerMovement() {
        if (isFinished) return;
        int startX = 20;
        int finishLineX = screenWidth - 100;
        player.updatePlayerPosition(wordManager.getProgressPercent(), startX, finishLineX - startX);
    }
    
    private void updateRivalBot(Car rival, int trackLength) 
    {
        if (!raceStarted) return;

        // 1. Hitung WPM Player saat ini secara Real-time
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < 1000) return; // Jangan hitung di 1 detik pertama (biar gak error infinity)

        // Rumus WPM: (Jumlah Karakter Benar / 5) / (Waktu dalam Menit)
        int charsTyped = wordManager.getTotalCharsTypedCorrectly();
        double minutes = elapsedTime / 60000.0;
        double playerCurrentWPM = (charsTyped / 5.0) / minutes;

        // 2. Tentukan Faktor Kesulitan (Controller)
        // 1.0 = Sama persis dengan player
        // 0.9 = Sedikit lebih lambat (Easy Rival)
        // 1.1 = Sedikit lebih cepat (Hard Rival)
        double difficultyFactor = 1.05; // Rival akan mencoba 5% lebih cepat dari kamu

        // 3. Konversi WPM Rival ke Pixel Speed
        // Mirip rumus di Car.java, tapi kita update setiap frame!
        // Speed = (Jarak Total) / (Total Waktu Estimasi dalam Frame)
        
        // Kita pakai pendekatan simplifikasi: Match kecepatan pixel player saat ini
        // Pixel per WPM kira-kira = (TrackLength) / (TotalKata / WPM * 60detik * 60FPS)
        // Tapi cara paling mudah untuk 'meniru' adalah meniru progress player:
        
        double targetSpeed = 0;
        
        if (playerCurrentWPM > 0) {
            // Hitung estimasi speed pixel per tick berdasarkan WPM saat ini
            double totalWords = 50; // Asumsi rata-rata kalimat (bisa ambil real dari WordManager)
            double estimatedTotalSeconds = (totalWords / (playerCurrentWPM * difficultyFactor)) * 60.0;
            double totalFrames = estimatedTotalSeconds * 60; // 60 FPS
            
            if (totalFrames > 0) {
                 targetSpeed = (double) trackLength / totalFrames;
            }
        }

        // 4. Update Speed Bot
        // Kita set langsung property di Car (Perlu nambah method setter di Car.java)
        rival.setDirectSpeed(targetSpeed);
    }
    
    private void checkWin(Car car, int finishLineX) {
        if (isFinished) return;
        if (car.getX() >= finishLineX) {
            isFinished = true;
            winnerName = car.getName();
        }
    }

    // --- RENDERING / GAMBAR ---
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        if (assets.background != null) g2.drawImage(assets.background, 0, 0, screenWidth, 400, null);
        else { g2.setColor(Color.DARK_GRAY); g2.fillRect(0,0,screenWidth,400); }

        // Finish Line
        g2.setColor(Color.WHITE);
        g2.fillRect(screenWidth - 90, 190, 10, 150); 

        // Draw Bots & Player
        for (Car bot : bots) bot.draw(g2);
        player.draw(g2);

        // UI Panel
        drawBottomPanel(g2);

        // Overlay
        if (isFinished) drawWinnerOverlay(g2);
        
        g2.dispose();
    }
    
    private void drawBottomPanel(Graphics2D g2) {
        g2.setColor(new Color(245, 245, 250)); 
        g2.fillRect(0, 400, screenWidth, screenHeight - 400); 
        g2.setColor(new Color(200, 200, 200));
        g2.drawLine(0, 400, screenWidth, 400);
        wordManager.draw(g2, screenWidth);
        drawInputBox(g2);
    }

    private void drawInputBox(Graphics2D g2) {
         // (Gunakan kode drawInputBox dari jawaban sebelumnya)
         int boxWidth = 600;
         int boxHeight = 50;
         int boxX = (screenWidth - boxWidth) / 2; 
         int boxY = screenHeight - 80; 
         if (wordManager.isError()) g2.setColor(new Color(255, 200, 200)); 
         else g2.setColor(new Color(230, 230, 230)); 
         g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
         g2.setStroke(new BasicStroke(3)); 
         if (wordManager.isError()) g2.setColor(new Color(255, 80, 80));
         else g2.setColor(new Color(180, 180, 180));
         g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
         g2.setFont(new Font("Monospaced", Font.BOLD, 28));
         g2.setColor(Color.BLACK);
         String text = wordManager.getUserBuffer();
         FontMetrics fm = g2.getFontMetrics();
         int textX = boxX + 20; 
         int textY = boxY + (boxHeight - fm.getHeight()) / 2 + fm.getAscent();
         g2.drawString(text + "|", textX, textY);
    }
    
    private void drawWinnerOverlay(Graphics2D g2) {
         // (Gunakan kode drawWinnerOverlay dari jawaban sebelumnya)
         g2.setColor(new Color(0, 0, 0, 180));
         g2.fillRect(0, 0, screenWidth, screenHeight);
         g2.setFont(new Font("Arial", Font.BOLD, 60));
         String msg = winnerName + " WINS!";
         FontMetrics fm = g2.getFontMetrics();
         int x = (screenWidth - fm.stringWidth(msg)) / 2;
         int y = screenHeight / 2 - 30;
         g2.setColor(Color.BLACK);
         g2.drawString(msg, x+4, y+4);
         if(winnerName.equals("YOU")) g2.setColor(Color.GREEN);
         else g2.setColor(Color.RED);
         g2.drawString(msg, x, y);
         g2.setFont(new Font("Arial", Font.BOLD, 20));
         g2.setColor(Color.WHITE);
         String subMsg = "Press ENTER to Restart Race";
         int subX = (screenWidth - g2.getFontMetrics().stringWidth(subMsg)) / 2;
         g2.drawString(subMsg, subX, y + 60);
    }
}