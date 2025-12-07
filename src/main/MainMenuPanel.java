package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel {

    private Main mainFrame; // Referensi ke Main untuk pindah layar
    private AssetManager assets;
    private JComboBox<String> difficultyBox;

    public MainMenuPanel(Main mainFrame, AssetManager assets) {
        this.mainFrame = mainFrame;
        this.assets = assets;
        
        // Ukuran sama dengan GamePanel
        this.setPreferredSize(new Dimension(950, 550));
        
        // Layout Manager untuk menengahkan elemen
        this.setLayout(new GridBagLayout());
        
        initUI();
    }
    
    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Jarak antar elemen
        gbc.gridx = 0; // Kolom 0 (Tengah)
        
        // 1. JUDUL: "BALAPAN KETIK PIXEL"
        JLabel titleLabel = new JLabel("BALAPAN KETIK PIXEL");
        titleLabel.setFont(new Font("Impact", Font.ITALIC, 60)); // Font tebal & miring
        titleLabel.setForeground(Color.YELLOW); // Warna Emas/Kuning
        
        // Efek Outline Hitam pada Teks (Opsional, trik visual)
        // Kalau mau simple, cukup setForeground saja.
        
        gbc.gridy = 0; // Baris ke-0
        this.add(titleLabel, gbc);
        
        // 2. LABEL: "Pilih Kesulitan Bot:"
        JLabel subtitleLabel = new JLabel("Pilih Kesulitan Bot:");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLabel.setForeground(Color.WHITE);
        
        gbc.gridy = 1; // Baris ke-1
        this.add(subtitleLabel, gbc);
        
        // 3. DROPDOWN: Pilihan Difficulty
        String[] levels = {"Easy", "Medium", "Hard", "Insane"};
        difficultyBox = new JComboBox<>(levels);
        difficultyBox.setFont(new Font("Arial", Font.PLAIN, 16));
        difficultyBox.setPreferredSize(new Dimension(200, 40));
        
        gbc.gridy = 2; // Baris ke-2
        this.add(difficultyBox, gbc);
        
        // 4. TOMBOL: "MULAI BALAPAN"
        JButton startButton = new JButton("MULAI BALAPAN");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(new Color(34, 139, 34)); // Forest Green
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false); // Hilangkan border fokus
        startButton.setPreferredSize(new Dimension(250, 60));
        
        // Action Listener: Saat tombol ditekan
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDiff = (String) difficultyBox.getSelectedItem();
                // Panggil method di Main.java untuk pindah layar
                mainFrame.startGame(selectedDiff);
            }
        });
        
        gbc.gridy = 3; // Baris ke-3
        gbc.insets = new Insets(40, 10, 10, 10); // Tambah jarak atas biar agak jauh
        this.add(startButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // PERUBAHAN DI SINI: Gunakan 'menuBackground'
        if (assets.menuBackground != null) {
            // Gambar full layar
            g2.drawImage(assets.menuBackground, 0, 0, getWidth(), getHeight(), null);
            
            // Tambah efek gelap (Overlay) biar tulisan kebaca jelas (Opsional)
            g2.setColor(new Color(0, 0, 0, 100)); // Hitam transparan
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fallback jika gambar menu_bg.png tidak ditemukan
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
            System.out.println("Gambar menu_bg.png tidak ditemukan!");
        }
    }
}