package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Random;

public class WordManager {

    private String[] dictionary = {
        "teknik informatika its jaya", 
        "belajar pemrograman berorientasi objek",
        "struktur data dan algoritma itu seru",
        "raditya sedang mengerjakan tugas besar",
        "java swing untuk membuat game desktop"
    };
    
    private String currentSentence;
    private String typedText = ""; // Apa yang sudah diketik user
    private Random random;
    
    public WordManager() {
        random = new Random();
        generateNewSentence();
    }

    public void generateNewSentence() {
        currentSentence = dictionary[random.nextInt(dictionary.length)];
        typedText = "";
    }

    public boolean checkKey(char key) {
        // Cek apakah karakter yang diketik sesuai dengan urutan karakter di kalimat
        String nextTyped = typedText + key;
        
        // Logic Strict: Hanya terima input jika BENAR
        if (currentSentence.startsWith(nextTyped)) {
            typedText += key;
            return true; // Input benar
        }
        return false; // Input salah/typo
    }
    
    public boolean isFinished() {
        return typedText.equals(currentSentence);
    }
    
    public double getProgressPercent() {
        return (double) typedText.length() / currentSentence.length();
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.setFont(new Font("Consolas", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        
        // Posisi teks di bawah layar (seperti referensi)
        int startX = 50;
        int y = screenHeight - 100;
        
        int currentX = startX;
        
        // Loop setiap karakter untuk pewarnaan
        char[] sentenceChars = currentSentence.toCharArray();
        for (int i = 0; i < sentenceChars.length; i++) {
            String s = String.valueOf(sentenceChars[i]);
            
            if (i < typedText.length()) {
                g2.setColor(Color.GREEN); // Sudah diketik
            } else {
                g2.setColor(Color.GRAY);  // Belum diketik
            }
            
            g2.drawString(s, currentX, y);
            currentX += fm.stringWidth(s);
        }
    }
}