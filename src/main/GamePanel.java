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
    final int tileSize = 48;
    public final int screenWidth = 950;
    public final int screenHeight = 550;
    int FPS = 60;

    Thread gameThread;
    KeyboardInputs keyInputs;
    AssetManager assets;
    WordManager wordManager;
    private Main mainFrame;

    Car player;
    ArrayList<Car> bots;

    public boolean isRunning = true;
    public boolean isFinished = false; 
    public String winnerName = "";
    private String currentDifficulty = "Medium";
    
    private long startTime;
    private boolean raceStarted = false;
    
    private Random rng = new Random();

    public GamePanel(AssetManager assets, Main mainFrame) 
    {
        this.mainFrame = mainFrame;
        this.assets = assets;
        
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        wordManager = new WordManager();
        keyInputs = new KeyboardInputs(this);
        
        this.addKeyListener(keyInputs);
    }

    public void setupRace(String difficulty) {
        this.currentDifficulty = difficulty;
        bots = new ArrayList<>();
        
        int minWPM = 30, maxWPM = 60;
        
        int startX = 20;
        int laneHeight = 40;
        int startY = 220;
        
        if(difficulty.equals("Easy")) { 
            minWPM = 20; maxWPM = 40; 
        } else if (difficulty.equals("Medium")) { 
            minWPM = 40; maxWPM = 70; 
        } else if (difficulty.equals("Hard")) { 
            minWPM = 70; maxWPM = 100; 
        } else if (difficulty.equals("Insane")) { 
            minWPM = 100; maxWPM = 140; 
        }
        
        boolean bot1IsDecel = rng.nextBoolean();
        int typeBot1 = bot1IsDecel ? Car.TYPE_DECEL_HYBRID : Car.TYPE_ACCEL_STATIC;
        int typeBot2 = bot1IsDecel ? Car.TYPE_ACCEL_STATIC : Car.TYPE_DECEL_HYBRID;

        Car bot1 = new Car("Player 1", startX, startY, assets.carBlue, typeBot1);
        bot1.setSpeedBounds(minWPM, maxWPM);
        bots.add(bot1);

        player = new Car("YOU", startX, startY + laneHeight, assets.carRed, Car.TYPE_PLAYER);

        Car bot2 = new Car("Player 2", startX, startY + (laneHeight * 2), assets.carYellow, typeBot2);
        bot2.setSpeedBounds(minWPM, maxWPM);
        bots.add(bot2);
        
        System.out.println("--- RACE SETUP ---");
        System.out.println("Difficulty: " + difficulty + " (" + minWPM + "-" + maxWPM + " WPM)");
        System.out.println("Blue Bot: " + (typeBot1 == Car.TYPE_DECEL_HYBRID ? "Awal Hybrid" : "Awal Lambat"));
        System.out.println("Yellow Bot: " + (typeBot2 == Car.TYPE_DECEL_HYBRID ? "Awal Ngebut" : "Awal Lambat"));

        isFinished = false;
        winnerName = "";
        raceStarted = false;
        wordManager.generateNewSentence();
        repaint();
    }
    
    private double calculatePixelSpeed(double wpm, int trackDistance) {
        if (wpm <= 0) return 0;
        double totalChars = wordManager.getTotalCharsInSentence(); 
        double totalWords = totalChars / 5.0;
        
        double totalTimeMinutes = totalWords / wpm;
        double totalTimeSeconds = totalTimeMinutes * 60.0;

        double totalFrames = totalTimeSeconds * 60.0; 
        
        return trackDistance / totalFrames;
    }
    
    public void startRace() 
    {
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
        setupRace(this.currentDifficulty);
        startRace();
    }
    
    public void backToMenu() {
        isRunning = false;
        mainFrame.showMenu();
    }

    // --- GETTERS ---
    public WordManager getWordManager() 
    { 
        return wordManager; 
    }
    public Car getPlayerCar() 
    { 
        return player; 
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
        if (isFinished || !isRunning) return;
        
        int finishLineX = screenWidth - 100;
        int startX = 20;
        int trackLength = finishLineX - startX;
        double playerWPM = 0;
        if (raceStarted) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > 1000) { 
                double minutes = elapsedTime / 60000.0;
                double wordsTyped = wordManager.getCurrentCorrectCharsCount() / 5.0;
                
                playerWPM = wordsTyped / minutes;
            }
        }

        for (Car bot : bots) {
            double currentWPM = 0;
            double progress = (double)(bot.getX() - startX) / trackLength;
            if (progress < 0) progress = 0;
            if (progress > 1) progress = 1;

            if (bot.getType() == Car.TYPE_ACCEL_STATIC) {
                currentWPM = bot.getMinWPM() + (progress * (bot.getMaxWPM() - bot.getMinWPM()));
            } else if (bot.getType() == Car.TYPE_DECEL_HYBRID) {
                double baseWPM = bot.getMaxWPM() - (progress * (bot.getMaxWPM() - bot.getMinWPM()));
                
                if (playerWPM > 0) {
                    currentWPM = (baseWPM * 0.7) + (playerWPM * 0.3); 
                } else {
                    currentWPM = baseWPM;
                }
                if (currentWPM < bot.getMinWPM()) currentWPM = bot.getMinWPM();
                if (currentWPM > bot.getMaxWPM()) currentWPM = bot.getMaxWPM();
            }

            bot.setDirectSpeed(calculatePixelSpeed(currentWPM, trackLength));
            
            bot.update();
            checkWin(bot, finishLineX);
        }
        checkWin(player, finishLineX);
    }
    
    private void updateHybridBot(Car hybridBot, int trackLength) {
        if (!raceStarted) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < 1000) return; // Tunggu 1 detik

        int charsTyped = wordManager.getTotalCharsTypedCorrectly();
        double minutes = elapsedTime / 60000.0;
        double playerCurrentWPM = (charsTyped / 5.0) / minutes;

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

    public void updatePlayerMovement() {
        if (isFinished) return;
        int startX = 20;
        int finishLineX = screenWidth - 100;
        player.updatePlayerPosition(wordManager.getProgressPercent(), startX, finishLineX - startX);
    }
    
    private void updateRivalBot(Car rival, int trackLength) 
    {
        if (!raceStarted) return;
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < 1000) return;

        int charsTyped = wordManager.getTotalCharsTypedCorrectly();
        double minutes = elapsedTime / 60000.0;
        double playerCurrentWPM = (charsTyped / 5.0) / minutes;

        double difficultyFactor = 1.05;
        
        double targetSpeed = 0;
        
        if (playerCurrentWPM > 0) {
            double totalWords = 50;
            double estimatedTotalSeconds = (totalWords / (playerCurrentWPM * difficultyFactor)) * 60.0;
            double totalFrames = estimatedTotalSeconds * 60;
            
            if (totalFrames > 0) {
                 targetSpeed = (double) trackLength / totalFrames;
            }
        }
        rival.setDirectSpeed(targetSpeed);
    }
    
    private void checkWin(Car car, int finishLineX) {
        if (isFinished) return;
        if (car.getX() >= finishLineX) {
            isFinished = true;
            winnerName = car.getName();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (assets.background != null) g2.drawImage(assets.background, 0, 0, screenWidth, 400, null);
        else { g2.setColor(Color.DARK_GRAY); g2.fillRect(0,0,screenWidth,400); }

        // Finish Line
        //g2.setColor(Color.WHITE);
        //g2.fillRect(screenWidth - 90, 190, 10, 150); 

        for (Car bot : bots) bot.draw(g2);
        player.draw(g2);
        drawBottomPanel(g2);
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
         int boxWidth = 600;
         int boxHeight = 50;
         int boxX = (screenWidth - boxWidth) / 2; 
         int boxY = screenHeight - 65; 

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
         g2.setColor(new Color(0, 0, 0, 180));
         g2.fillRect(0, 0, screenWidth, screenHeight);
         
         g2.setFont(new Font("Arial", Font.BOLD, 60));
         String msg = winnerName + " WINS!";
         FontMetrics fm = g2.getFontMetrics();
         int x = (screenWidth - fm.stringWidth(msg)) / 2;
         int y = screenHeight / 2 - 40;
         
         g2.setColor(Color.BLACK);
         g2.drawString(msg, x+4, y+4);
         
         if(winnerName.equals("YOU")) g2.setColor(Color.GREEN);
         else g2.setColor(Color.RED);
         g2.drawString(msg, x, y);

         g2.setFont(new Font("Arial", Font.BOLD, 22));
         g2.setColor(Color.WHITE);
         String subMsg = "[ ENTER ]  to Restart Race";
         int subX = (screenWidth - g2.getFontMetrics().stringWidth(subMsg)) / 2;
         g2.drawString(subMsg, subX, y + 60);
         
         g2.setColor(Color.YELLOW);
         String homeMsg = "[ ESC ]  to Main Menu";
         int homeX = (screenWidth - g2.getFontMetrics().stringWidth(homeMsg)) / 2;
         g2.drawString(homeMsg, homeX, y + 100);
    }
}