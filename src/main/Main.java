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
        
        // 1. Setup Layout
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        // 2. Load Assets Sekali Saja (Biar hemat memori)
        assets = new AssetManager();
        
        // 3. Inisialisasi Panel
        // Kita kirim 'this' (Main) ke MenuPanel agar menu bisa manggil fungsi startGame()
        menuPanel = new MainMenuPanel(this, assets);
        
        // GamePanel butuh akses ke Main juga jika nanti mau ada tombol "Back to Menu"
        gamePanel = new GamePanel(assets, this); 
        
        // 4. Masukkan ke Container
        mainContainer.add(menuPanel, "MENU"); // Beri nama "MENU"
        mainContainer.add(gamePanel, "GAME"); // Beri nama "GAME"
        
        window.add(mainContainer);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        // Tampilkan Menu Dulu
        cardLayout.show(mainContainer, "MENU");
    }
    
    // Method untuk dipanggil dari Tombol "MULAI BALAPAN"
    public void startGame(String difficulty) {
        // Reset game sebelum mulai (agar posisi mobil kembali ke awal)
        gamePanel.setupRace(difficulty); 
        gamePanel.startRace(); // Mulai timer, dll
        
        // Pindah tampilan ke Game
        cardLayout.show(mainContainer, "GAME");
        
        // Fokuskan keyboard ke GamePanel agar bisa langsung ngetik
        gamePanel.requestFocusInWindow();
    }
    
    // Method untuk kembali ke menu (Opsional, nanti bisa dipanggil saat Game Over)
    public void showMenu() {
        cardLayout.show(mainContainer, "MENU");
    }

    public static void main(String[] args) {
        new Main();
    }
}