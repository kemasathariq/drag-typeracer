package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Random;

public class WordManager {

    // Kalimat dipecah jadi kata-kata
    private String[] words; 
    private int currentWordIndex = 0; // Kata ke berapa yang sedang diketik
    
    // Apa yang sedang diketik user di Input Box
    private String userBuffer = ""; 
    
    // Status apakah input saat ini salah? (Untuk bikin kotak jadi merah)
    private boolean isError = false;
    
    // Untuk tracking progress mobil
    private int totalCharsTypedCorrectly = 0;
    private int totalCharsInSentence = 0;

    private String[] dictionary = {
        "izinkan ku lukis senja mengukir namamu di sana",
        "mendengar kamu bercerita menangis tertawa",
        "biar ku lukis malam bawa kamu bintang bintang",
        "teknik informatika its tempat kita berkarya",
        "koding itu menyenangkan jika tidak ada error"
    };
    
    private Random random;

    public WordManager() {
        random = new Random();
        generateNewSentence();
    }

    public void generateNewSentence() {
        String fullSentence = dictionary[random.nextInt(dictionary.length)];
        words = fullSentence.split(" "); // Pecah jadi array kata
        currentWordIndex = 0;
        userBuffer = "";
        isError = false;
        totalCharsTypedCorrectly = 0;
        totalCharsInSentence = fullSentence.length(); // + spasi antar kata sebenarnya
    }
    
    // --- LOGIC UTAMA MENGETIK ---
    public void typeKey(char key) {
        // 1. Handle BACKSPACE (Hapus karakter)
        if (key == '\b') { // Kode ASCII untuk Backspace
            if (userBuffer.length() > 0) {
                userBuffer = userBuffer.substring(0, userBuffer.length() - 1);
            }
        } 
        // 2. Handle SPASI (Submit kata)
        else if (key == ' ') {
            String targetWord = words[currentWordIndex];
            // Hanya pindah jika user mengetik kata dengan BENAR
            if (userBuffer.equals(targetWord)) {
                totalCharsTypedCorrectly += targetWord.length() + 1; // +1 untuk spasinya
                userBuffer = ""; // Reset buffer
                currentWordIndex++; // Pindah ke kata selanjutnya
                
                // Cek apakah kalimat habis
                if (currentWordIndex >= words.length) {
                    generateNewSentence();
                }
            } else {
                // Kalau user tekan spasi tapi katanya salah/belum selesai?
                // Opsional: Bisa dikosongkan buffer-nya (hukuman) atau dibiarkan error
                // Di sini kita biarkan saja (user harus hapus manual)
            }
        }
        // 3. Handle HURUF BIASA
        else {
            // Batasi panjang input max 20 char agar tidak kebablasan
            if (userBuffer.length() < 20) {
                userBuffer += key;
            }
        }
        
        checkErrorStatus();
    }
    
    // Cek apakah buffer sesuai dengan target kata
    private void checkErrorStatus() {
        if (currentWordIndex >= words.length) return;
        
        String targetWord = words[currentWordIndex];
        
        // Jika user mengetik lebih panjang dari kata asli, pasti error
        if (userBuffer.length() > targetWord.length()) {
            isError = true;
        } 
        // Jika apa yang diketik TIDAK COCOK dengan awal kata target
        else if (!targetWord.startsWith(userBuffer)) {
            isError = true;
        } 
        else {
            isError = false;
        }
    }

    public boolean isError() { return isError; }
    public String getUserBuffer() { return userBuffer; }
    
    public double getProgressPercent() {
        // Progress dihitung dari kata yang SUDAH selesai + huruf benar di kata saat ini
        int currentProgress = totalCharsTypedCorrectly;
        if (!isError && currentWordIndex < words.length) {
            currentProgress += userBuffer.length();
        }
        return (double) currentProgress / totalCharsInSentence;
    }

    // --- VISUALISASI SEPERTI GAMBAR ---
    public void draw(Graphics2D g2, int screenWidth) {
        g2.setFont(new Font("Consolas", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        
        int startX = 50;
        int y = 450; // Posisi teks kalimat
        int currentX = startX;
        
        // Loop setiap KATA
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            
            // KATA YANG SUDAH SELESAI (Hijau)
            if (i < currentWordIndex) {
                g2.setColor(new Color(34, 139, 34)); // Forest Green
                g2.drawString(word + " ", currentX, y);
                currentX += fm.stringWidth(word + " ");
            } 
            // KATA YANG SEDANG DIKETIK (Target)
            else if (i == currentWordIndex) {
                // Gambar per karakter untuk handle highlight merah
                for (int j = 0; j < word.length(); j++) {
                    char c = word.charAt(j);
                    String s = String.valueOf(c);
                    int charW = fm.stringWidth(s);
                    
                    boolean charIsWrong = false;
                    // Logic Merah: Jika index j < buffer.length DAN buffer[j] != word[j]
                    if (j < userBuffer.length()) {
                         if (userBuffer.charAt(j) != c) {
                             charIsWrong = true;
                         }
                    }
                    
                    // Highlight Background Merah jika salah
                    if (charIsWrong) {
                        g2.setColor(new Color(255, 100, 100, 180)); // Merah transparan
                        g2.fillRect(currentX, y - 20, charW, 25);
                    }
                    else if (j < userBuffer.length()) {
                        // Karakter benar yang sedang diketik (Hijau muda/stabilo?)
                        g2.setColor(new Color(200, 255, 200, 100)); // Hijau background tipis
                        g2.fillRect(currentX, y - 20, charW, 25);
                    }
                    
                    // Warna Huruf
                    if (charIsWrong) g2.setColor(Color.RED); // Huruf merah kalau salah
                    else if (j < userBuffer.length()) g2.setColor(new Color(0, 100, 0)); // Hijau tua kalau sudah diketik benar
                    else g2.setColor(Color.BLACK); // Hitam kalau belum diketik
                    
                    // Underline kata yang sedang aktif
                    g2.drawLine(currentX, y+2, currentX+charW, y+2);

                    g2.drawString(s, currentX, y);
                    currentX += charW;
                }
                
                // Jika user mengetik LEBIH dari panjang kata (Huruf hantu merah)
                if (userBuffer.length() > word.length()) {
                    String excess = userBuffer.substring(word.length());
                    g2.setColor(Color.RED);
                    g2.drawString(excess, currentX, y);
                    currentX += fm.stringWidth(excess);
                }
                
                // Spasi setelah kata aktif
                currentX += fm.stringWidth(" ");
            } 
            // KATA MASA DEPAN (Hitam/Abu)
            else {
                g2.setColor(Color.GRAY);
                g2.drawString(word + " ", currentX, y);
                currentX += fm.stringWidth(word + " ");
            }
            
            // Wrap text (pindah baris) jika mentok kanan
            if (currentX > screenWidth - 100) {
                currentX = startX;
                y += 35;
            }
        }
    }
    public int getTotalCharsTypedCorrectly() {
        return totalCharsTypedCorrectly;
    }
}