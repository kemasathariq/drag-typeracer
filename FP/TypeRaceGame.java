import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// --- CLASS 1: ASSET LOADER ---
class AssetLoader {
    public BufferedImage background;
    public BufferedImage menuBackground;
    public BufferedImage carBlue, carPink, carRed; 
    
    public AssetLoader() {
        try {
            menuBackground = ImageIO.read(new File("menu_bg.png")); 
            background = ImageIO.read(new File("background.png"));
            carBlue = ImageIO.read(new File("car_blue.png"));
            carPink = ImageIO.read(new File("car_pink.png"));
            carRed = ImageIO.read(new File("car_red.png"));
            
        } catch (IOException e) {
            System.err.println("ERROR: Gagal memuat gambar!");
            System.err.println("Pastikan file background.png, car_blue.png, dll ada di folder project.");
        }
    }
}

// --- CLASS 2: LOGIKA KATA (BAHASA INDONESIA) ---
class WordGenerator {
    private ArrayList<String> dictionary;
    private Random random;

    public WordGenerator() {
        dictionary = new ArrayList<>();
        random = new Random();
        populateDictionary();
    }

    private void populateDictionary() {
        String[] words = {
            "saya", "kamu", "dia", "kita", "mereka", "belajar", "sekolah", "kuliah",
            "koding", "komputer", "laptop", "program", "data", "internet", "jaringan",
            "makan", "minum", "tidur", "jalan", "lari", "cepat", "lambat", "mobil",
            "motor", "kereta", "pesawat", "rumah", "kantor", "buku", "tulis", "baca",
            "merah", "biru", "hijau", "kuning", "hitam", "putih", "langit", "bumi",
            "hujan", "panas", "dingin", "siang", "malam", "pagi", "sore", "waktu",
            "uang", "kerja", "sukses", "juara", "kalah", "menang", "lomba", "game",
            "java", "sistem", "aplikasi", "teknologi", "digital", "online", "offline",
            "algoritma", "logika", "layar", "mouse", "keyboard", "wifi", "kode"
        };
        Collections.addAll(dictionary, words);
    }

    public String generateSentence(int wordCount) {
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            sentence.append(dictionary.get(random.nextInt(dictionary.size()))).append(" ");
        }
        return sentence.toString().trim();
    }
}

// --- CLASS 3: OBJEK MOBIL (RACER) ---
class Racer {
    private String name;
    private double x; 
    private int y;
    private BufferedImage sprite; 
    private Color fallbackColor;  
    private double speedPerTick;  

    public Racer(String name, int startX, int startY, BufferedImage sprite, Color fallbackColor) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.sprite = sprite;
        this.fallbackColor = fallbackColor;
        this.speedPerTick = 0;
    }

    public void calculateSpeed(int wpm, int totalWords, int raceDistanceInPixels) {
        if (wpm <= 0) {
            this.speedPerTick = 0;
            return;
        }
        double totalTimeSeconds = ((double) totalWords / wpm) * 60.0;
        double totalTicks = totalTimeSeconds * 20; 
        this.speedPerTick = (double) raceDistanceInPixels / totalTicks;
    }

    public void moveAuto() {
        this.x += speedPerTick;
    }

    public void moveManual(int step) {
        this.x += step;
    }
    
    public int getX() { return (int) x; }
    public String getName() { return name; }

    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int)x, y, 90, 45, null);
        } else {
            g.setColor(fallbackColor);
            g.fillRect((int)x, y + 10, 80, 30);
        }
        
        // Nama Racer
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        g.drawString(name, (int)x + 1, y - 5 + 1);
        g.setColor(Color.WHITE);
        g.drawString(name, (int)x, y - 5);
    }
}

// --- CLASS 4: VISUALISASI BALAPAN ---
class RaceTrackPanel extends JPanel {
    private Racer player;
    private List<Racer> bots;
    private AssetLoader assets;
    
    private String targetText = "";
    private String typedText = "";
    
    private boolean isFinished = false;
    private boolean isCountdown = false;
    private int countdownValue = 0;
    private String winner = "";

    private final int RACE_VIEW_HEIGHT = 400; 

    public RaceTrackPanel(AssetLoader assets) {
        this.assets = assets;
        this.setPreferredSize(new Dimension(950, 550));
        this.setBackground(new Color(30, 30, 30)); 
        this.bots = new ArrayList<>();
    }

    public void setupRace(Racer player, List<Racer> bots, String text) {
        this.player = player;
        this.bots = bots;
        this.targetText = text;
        this.typedText = "";
        this.isFinished = false;
        this.winner = "";
        repaint();
    }
    
    public void startCountdown(int value) {
        this.isCountdown = true;
        this.countdownValue = value;
        repaint();
    }
    
    public void endCountdown() {
        this.isCountdown = false;
        repaint();
    }
    
    public void updateCountdown(int val) {
        this.countdownValue = val;
        repaint();
    }

    public void updateText(String typed) {
        this.typedText = typed;
        repaint();
    }

    public void setWinner(String winnerName) {
        this.isFinished = true;
        this.winner = winnerName;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Gambar Background
        if (assets.background != null) {
            g.drawImage(assets.background, 0, 0, getWidth(), RACE_VIEW_HEIGHT, null);
        } else {
            g.setColor(Color.DARK_GRAY); 
            g.fillRect(0, 0, getWidth(), RACE_VIEW_HEIGHT);
        }

        // 2. Garis Finish
        int finishLineX = getWidth() - 80;
        int roadTop = 190; 
        int roadBottom = 345;
        int lineHeight = roadBottom - roadTop;
        
        g.setColor(new Color(200, 200, 200));
        g.fillRect(finishLineX, roadTop - 10, 10, lineHeight + 20); 
        
        g.setColor(Color.WHITE);
        g.fillRect(finishLineX + 10, roadTop, 40, lineHeight);
        g.setColor(Color.BLACK);
        g.drawRect(finishLineX + 10, roadTop, 40, lineHeight);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("FINISH", finishLineX - 10, roadTop - 15);

        // 3. Gambar Mobil (Z-ORDER / Urutan Tumpukan 3D)
        // Kita gambar dari yang paling JAUH (Atas) ke paling DEKAT (Bawah)
        
        // Posisi 1 (Atas): Bot 2 (Pink)
        if (bots.size() > 0) bots.get(0).draw(g);
        
        // Posisi 2 (Tengah): Player (Merah)
        if (player != null) player.draw(g);        
        
        // Posisi 3 (Bawah): Bot 1 (Biru)
        if (bots.size() > 1) bots.get(1).draw(g);

        // 4. Panel Bawah (Area Mengetik)
        g.setColor(new Color(30, 30, 35));
        g.fillRect(0, RACE_VIEW_HEIGHT, getWidth(), getHeight() - RACE_VIEW_HEIGHT);
        g.setColor(new Color(255, 165, 0)); 
        g.fillRect(0, RACE_VIEW_HEIGHT, getWidth(), 3);

        drawWrappedText(g);

        // 5. Overlay Countdown
        if (isCountdown) {
            drawOverlay(g);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Verdana", Font.BOLD, 100));
            String countStr = (countdownValue > 0) ? String.valueOf(countdownValue) : "GO!";
            
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(countStr)) / 2;
            int y = (RACE_VIEW_HEIGHT / 2) + 20;
            
            g.setColor(Color.BLACK);
            g.drawString(countStr, x+4, y+4);
            g.setColor(Color.YELLOW);
            g.drawString(countStr, x, y);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            String readyMsg = "Siap-siap mengetik!";
            int x2 = (getWidth() - g.getFontMetrics().stringWidth(readyMsg)) / 2;
            g.drawString(readyMsg, x2, y + 60);
        }

        // 6. Overlay Pemenang
        if (isFinished) {
            drawOverlay(g);
            g.setColor(winner.equals("YOU") ? Color.GREEN : Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String msg = winner.equals("YOU") ? "KAMU MENANG!" : winner + " MENANG!";
            
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = RACE_VIEW_HEIGHT / 2;
            
            g.drawString(msg, x, y);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String subMsg = "Tekan ENTER untuk kembali ke Menu";
            int subX = (getWidth() - g.getFontMetrics().stringWidth(subMsg)) / 2;
            g.drawString(subMsg, subX, y + 50);
        }
    }
    
    private void drawOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 160)); 
        g.fillRect(0, 0, getWidth(), RACE_VIEW_HEIGHT); 
    }

    private void drawWrappedText(Graphics g) {
        if (targetText == null || targetText.isEmpty()) return;

        g.setFont(new Font("Consolas", Font.BOLD, 24)); 
        FontMetrics fm = g.getFontMetrics();
        
        int startX = 30;
        int startY = RACE_VIEW_HEIGHT + 50; 
        int lineHeight = 35;
        int maxWidth = getWidth() - 60;

        int currentX = startX;
        int currentY = startY;

        String[] words = targetText.split(" ");
        int charIndex = 0;

        for (String word : words) {
            String wordWithSpace = word + " ";
            int wordWidth = fm.stringWidth(wordWithSpace);

            if (currentX + wordWidth > startX + maxWidth) {
                currentX = startX;
                currentY += lineHeight;
            }

            for (char c : wordWithSpace.toCharArray()) {
                String s = String.valueOf(c);
                int charWidth = fm.stringWidth(s);

                // Warna Teks
                if (charIndex < typedText.length()) {
                    char typedChar = typedText.charAt(charIndex);
                    
                    if (typedChar == c) {
                        if (c == ' ') {
                            g.setColor(Color.YELLOW);
                            g.fillRect(currentX, currentY - 18, charWidth, 22);
                        } else {
                            g.setColor(new Color(0, 255, 0)); 
                            g.drawString(s, currentX, currentY);
                        }
                    } else {
                        g.setColor(new Color(255, 50, 50)); 
                        g.drawString(s, currentX, currentY);
                    }
                } else {
                    g.setColor(Color.GRAY);
                    g.drawString(s, currentX, currentY);
                }

                currentX += charWidth;
                charIndex++;
            }
        }
    }
}

// --- CLASS 5: MAIN MENU (DENGAN JUDUL & BACKGROUND) ---
class MainMenuPanel extends JPanel {
    private TypeRaceGame mainFrame;
    private JComboBox<String> difficultyBox;
    private AssetLoader assets;

    public MainMenuPanel(TypeRaceGame frame, AssetLoader assets) {
        this.mainFrame = frame;
        this.assets = assets;
        setLayout(new GridBagLayout()); // Menggunakan GridBagLayout agar fleksibel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        // --- 1. JUDUL GAME (DI KEMBALIKAN) ---
        JLabel title = new JLabel("BALAPAN KETIK PIXEL");
        title.setFont(new Font("Impact", Font.ITALIC, 50)); // Font Besar & Miring
        title.setForeground(new Color(255, 215, 0)); // Warna Emas (Gold)
        
        // Efek Shadow sederhana (Opsional: Tambahkan border hitam jika perlu)
        // Kita atur posisi Judul agak di atas
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 10, 0, 10); // Jarak dari atas layar 30px
        add(title, gbc);

        // --- 2. SUBTITLE (PEMISAH) ---
        JLabel subtitle = new JLabel("Pilih Kesulitan Bot:");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        // KUNCI POSISI: 
        // Kita beri jarak (Insets) sebesar 120px dari Judul di atasnya.
        // Ini akan mendorong Subtitle & Tombol turun ke area "Jalan Aspal"
        gbc.gridy = 1;
        gbc.insets = new Insets(80, 10, 5, 10); 
        add(subtitle, gbc);

        // --- 3. PILIHAN LEVEL ---
        String[] levels = {"Easy", "Medium", "Hard", "Insane"};
        difficultyBox = new JComboBox<>(levels);
        difficultyBox.setFont(new Font("Arial", Font.BOLD, 16));
        difficultyBox.setPreferredSize(new Dimension(250, 40));
        ((JLabel)difficultyBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 10, 10); // Jarak normal
        add(difficultyBox, gbc);

        // --- 4. TOMBOL START ---
        JButton startBtn = new JButton("MULAI BALAPAN");
        startBtn.setBackground(new Color(34, 139, 34)); // Hijau
        startBtn.setForeground(Color.WHITE);
        startBtn.setFont(new Font("Arial", Font.BOLD, 20));
        startBtn.setFocusPainted(false);
        startBtn.setPreferredSize(new Dimension(250, 50));
        startBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        startBtn.addActionListener(e -> {
            String level = (String) difficultyBox.getSelectedItem();
            mainFrame.initRace(level);
        });

        gbc.gridy = 3;
        add(startBtn, gbc);
    }

    // Menggambar Background
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (assets.menuBackground != null) {
            g.drawImage(assets.menuBackground, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(15, 15, 20));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

// --- CLASS 6: GAME CONTROLLER ---
public class TypeRaceGame extends JFrame implements KeyListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private RaceTrackPanel gamePanel;
    private MainMenuPanel menuPanel;
    private AssetLoader assets;

    private WordGenerator wordGen;
    private Racer playerCar;
    private ArrayList<Racer> botCars;
    
    private String currentSentence;
    private StringBuilder userBuffer;
    
    private Timer gameLoopTimer;
    private Timer countdownTimer;
    private int countdownSeconds = 3;
    
    private boolean isRacing = false;
    private boolean isPausedForCountdown = false;
    
    private final int WORD_COUNT_PER_RACE = 25; 

    public TypeRaceGame() {
        setTitle("TypeRace: Indonesia Edition (3 Cars)");
        setSize(950, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        assets = new AssetLoader();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MainMenuPanel(this, assets);
        gamePanel = new RaceTrackPanel(assets);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);
        
        addKeyListener(this);
        setFocusable(true);
    }

    public void initRace(String difficulty) {
        wordGen = new WordGenerator();
        currentSentence = wordGen.generateSentence(WORD_COUNT_PER_RACE);
        userBuffer = new StringBuilder();

        // --- PENGATURAN POSISI MOBIL (3 JALUR) ---
        int startX = 20;
        
        // Aspal mulai Y=190 sampai Y=350
        // Kita bagi jadi 3 jalur lega (masing-masing 50px space)
        int laneHeight = 35; 
        int startY = 220; 

        // 1. JALUR ATAS: Bot 2 (Pink)
        // 2. JALUR TENGAH: Player (Merah)
        // 3. JALUR BAWAH: Bot 1 (Biru) --> Pindah ke posisi bawah (ex-Bot 3)

        // Setup Player (Tengah)
        playerCar = new Racer("YOU", startX, startY + laneHeight, assets.carRed, Color.RED);
        
        botCars = new ArrayList<>();
        // Setup Bot 2 (Atas)
        botCars.add(new Racer("BOT 2", startX, startY, assets.carPink, Color.MAGENTA));
        // Setup Bot 1 (Bawah)
        botCars.add(new Racer("BOT 1", startX, startY + (laneHeight * 2), assets.carBlue, Color.BLUE));

        // Konfigurasi Kecepatan
        int wpm1=0, wpm2=0;
        switch (difficulty) {
            case "Easy":   wpm1=30; wpm2=25; break;
            case "Medium": wpm1=60; wpm2=50; break;
            case "Hard":   wpm1=90; wpm2=80; break;
            case "Insane": wpm1=130; wpm2=110; break;
        }
        
        int realDistance = (gamePanel.getWidth() - 90) - startX;
        
        // Ingat urutan di List: index 0 = Bot 2, index 1 = Bot 1
        botCars.get(0).calculateSpeed(wpm2, WORD_COUNT_PER_RACE, realDistance); // Speed Bot 2
        botCars.get(1).calculateSpeed(wpm1, WORD_COUNT_PER_RACE, realDistance); // Speed Bot 1

        gamePanel.setupRace(playerCar, botCars, currentSentence);
        cardLayout.show(mainPanel, "GAME");
        this.requestFocusInWindow();
        
        startCountdown();
    }
    
    private void startCountdown() {
        isPausedForCountdown = true;
        isRacing = false;
        countdownSeconds = 3;
        
        gamePanel.startCountdown(countdownSeconds);
        
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdownSeconds--;
                if (countdownSeconds > 0) {
                    gamePanel.updateCountdown(countdownSeconds);
                } else {
                    ((Timer)e.getSource()).stop();
                    beginRaceLogic();
                }
            }
        });
        countdownTimer.start();
    }
    
    private void beginRaceLogic() {
        isPausedForCountdown = false;
        isRacing = true;
        gamePanel.endCountdown();
        
        gameLoopTimer = new Timer(50, e -> {
            if (!isRacing) return;

            for (Racer bot : botCars) {
                bot.moveAuto();
            }
            gamePanel.repaint();
            checkWinCondition();
        });
        gameLoopTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!isRacing || isPausedForCountdown) {
            if (e.getKeyChar() == '\n' && !isRacing && !isPausedForCountdown) {
                if (gameLoopTimer != null) gameLoopTimer.stop();
                cardLayout.show(mainPanel, "MENU");
            }
            return;
        }

        char typed = e.getKeyChar();
        
        if (typed == KeyEvent.VK_BACK_SPACE) { 
             if (userBuffer.length() > 0) {
                userBuffer.setLength(userBuffer.length() - 1);
                gamePanel.updateText(userBuffer.toString());
            }
            return;
        }

        String expectedText = currentSentence;
        String nextTyped = userBuffer.toString() + typed;

        if (expectedText.startsWith(nextTyped)) {
            userBuffer.append(typed);
            movePlayer();
            gamePanel.updateText(userBuffer.toString());
            checkWinCondition();
        }
    }

    private void movePlayer() {
        int realDistance = (gamePanel.getWidth() - 90) - 20;
        double percent = (double) userBuffer.length() / currentSentence.length();
        
        int targetX = 20 + (int) (percent * realDistance);
        int step = targetX - playerCar.getX();
        playerCar.moveManual(step);
    }

    private void checkWinCondition() {
        int finishLine = gamePanel.getWidth() - 90;

        if (playerCar.getX() >= finishLine) {
            gameOver("YOU");
            return;
        }
        for (Racer bot : botCars) {
            if (bot.getX() >= finishLine) {
                gameOver(bot.getName());
                return;
            }
        }
    }

    private void gameOver(String winner) {
        isRacing = false;
        if (gameLoopTimer != null) gameLoopTimer.stop();
        gamePanel.setWinner(winner);
    }

    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TypeRaceGame().setVisible(true);
        });
    }
}