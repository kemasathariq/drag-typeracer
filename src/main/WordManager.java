package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Random;

public class WordManager {
    private String[] words; 
    private int currentWordIndex = 0;
    private String userBuffer = ""; 
    
    private boolean isError = false;

    private int totalCharsTypedCorrectly = 0;
    private int totalCharsInSentence = 0;
    
    private String[] wordBank = {
        "aku", "kamu", "dia", "kita", "mereka", "dan", "atau", "tetapi", "jika", "karena",
        "rumah", "sekolah", "kampus", "kantor", "jalan", "mobil", "motor", "kereta", "pesawat",
        "makan", "minum", "tidur", "lari", "jalan", "belajar", "kerja", "main", "coding",
        "java", "python", "script", "data", "server", "klien", "wifi", "internet", "mouse",
        "buku", "pena", "kertas", "meja", "kursi", "lampu", "pintu", "jendela", "atap",
        "merah", "biru", "hijau", "kuning", "hitam", "putih", "abu", "ungu", "jingga",
        "cepat", "lambat", "besar", "kecil", "tinggi", "rendah", "panjang", "pendek",
        "informatika", "sistem", "algoritma", "struktur", "objek", "kelas", "metode",
        "variabel", "fungsi", "logika", "teknologi", "digital", "program", "aplikasi",
        "hujan", "panas", "dingin", "siang", "malam", "pagi", "sore", "senja", "fajar",
        "cinta", "rindu", "senang", "sedih", "marah", "takut", "berani", "kuat", "lemah",
        "indonesia", "surabaya", "jakarta", "bandung", "jogja", "malang", "bali",
        "satu", "dua", "tiga", "empat", "lima", "sepuluh", "ratus", "ribu", "juta"
    };
    
    private Random random;

    public WordManager() {
        random = new Random();
        generateNewSentence();
    }

    public void generateNewSentence() {
        StringBuilder sb = new StringBuilder();
        int wordCount = 30; 
        
        for (int i = 0; i < wordCount; i++) {
            String randomWord = wordBank[random.nextInt(wordBank.length)];
            sb.append(randomWord);
            if (i < wordCount - 1) {
                sb.append(" ");
            }
        }
        
        String fullSentence = sb.toString();
        
        words = fullSentence.split(" "); 
        currentWordIndex = 0;
        userBuffer = "";
        isError = false;
        totalCharsTypedCorrectly = 0;
        totalCharsInSentence = fullSentence.length(); 
    }

    public void typeKey(char key) {
        if (key == '\b') { // Backspace
            if (userBuffer.length() > 0) {
                userBuffer = userBuffer.substring(0, userBuffer.length() - 1);
            }
        } 
        else if (key == ' ') {
            String targetWord = words[currentWordIndex];
            if (userBuffer.equals(targetWord)) {
                totalCharsTypedCorrectly += targetWord.length() + 1;
                userBuffer = ""; // Reset buffer
                currentWordIndex++;

                if (currentWordIndex >= words.length) {
                    generateNewSentence();
                }
            }
        } else {
            if (userBuffer.length() < 20) {
                userBuffer += key;
            }
        }
        
        checkErrorStatus();
    }

    private void checkErrorStatus() {
        if (currentWordIndex >= words.length) return;
        
        String targetWord = words[currentWordIndex];

        if (userBuffer.length() > targetWord.length()) {
            isError = true;
        } else if (!targetWord.startsWith(userBuffer)) {
            isError = true;
        } else {
            isError = false;
        }
    }

    public boolean isError() { 
        return isError; 
    }
    public String getUserBuffer() { 
        return userBuffer; 
    }
    
    public double getProgressPercent() {
        int currentProgress = totalCharsTypedCorrectly;

        if (currentWordIndex < words.length) {
            String targetWord = words[currentWordIndex];
            int matchingChars = 0;
            int limit = Math.min(userBuffer.length(), targetWord.length());
            
            for (int i = 0; i < limit; i++) {
                if (userBuffer.charAt(i) == targetWord.charAt(i)) {
                    matchingChars++;
                } else {
                    break; 
                }
            }
            currentProgress += matchingChars;
        }
        return (double) currentProgress / totalCharsInSentence;
    }

    public void draw(Graphics2D g2, int screenWidth) {
        g2.setFont(new Font("Consolas", Font.BOLD, 22));
        FontMetrics fm = g2.getFontMetrics();
        
        int startX = 50;
        int lineHeight = 32;
        int maxLineWidth = screenWidth - 100;

        int activeLine = 0;
        int tempX = startX;
        int currentLineIndex = 0;
        
        for (int i = 0; i < words.length; i++) {
            String content = words[i];
            if (i == currentWordIndex && userBuffer.length() > words[i].length()) {
                content += userBuffer.substring(words[i].length());
            }
            
            int wordWidth = fm.stringWidth(content + " ");
            
            if (tempX + wordWidth > startX + maxLineWidth) {
                currentLineIndex++;
                tempX = startX;
            }
            
            if (i == currentWordIndex) {
                activeLine = currentLineIndex;
            }
            tempX += wordWidth;
        }
        // error tetap scroll
        int scrollPixelY = activeLine * lineHeight;
        int startY = 435; 

        java.awt.Shape originalClip = g2.getClip();
        g2.setClip(0, 400, screenWidth, 75); 
        
        int drawX = startX;
        int drawY = startY - scrollPixelY; 
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String contentForWidth = word;
            String excess = "";
            if (i == currentWordIndex && userBuffer.length() > word.length()) {
                excess = userBuffer.substring(word.length());
                contentForWidth += excess;
            }
            
            int wordWidth = fm.stringWidth(contentForWidth + " ");

            if (drawX + wordWidth > startX + maxLineWidth) {
                drawX = startX;
                drawY += lineHeight;
            }
            if (drawY > 380 && drawY < 500) { 
                if (i < currentWordIndex) {
                    g2.setColor(new Color(34, 139, 34)); // Hijau
                    g2.drawString(word + " ", drawX, drawY);
                } 
                else if (i == currentWordIndex) {
                    int charX = drawX;
                    for (int j = 0; j < word.length(); j++) {
                        char c = word.charAt(j);
                        String s = String.valueOf(c);
                        int charW = fm.stringWidth(s);
                        
                        boolean charIsWrong = (j < userBuffer.length() && userBuffer.charAt(j) != c);
                        if (charIsWrong) {
                            g2.setColor(new Color(255, 100, 100, 180));
                            g2.fillRect(charX, drawY - 20, charW, 25);
                            g2.setColor(Color.RED);
                        } else if (j < userBuffer.length()) {
                            g2.setColor(new Color(200, 255, 200, 100));
                            g2.fillRect(charX, drawY - 20, charW, 25);
                            g2.setColor(new Color(0, 100, 0));
                        } else {
                            g2.setColor(Color.BLACK);
                        }
                        
                        g2.drawString(s, charX, drawY);
                        g2.drawLine(charX, drawY+2, charX+charW, drawY+2); // Underline
                        charX += charW;
                    }

                    if (!excess.isEmpty()) {
                        g2.setColor(Color.RED);
                        g2.drawString(excess, charX, drawY);
                    }
                } 
                else {
                    g2.setColor(Color.GRAY);
                    g2.drawString(word + " ", drawX, drawY);
                }
            }
            drawX += wordWidth;
        }
        g2.setClip(originalClip);
    }
    
    public int getTotalCharsTypedCorrectly() {
        return totalCharsTypedCorrectly;
    }
    
    public int getTotalCharsInSentence() {
        return totalCharsInSentence;
    }
    
    public int getCurrentCorrectCharsCount() {
        int count = totalCharsTypedCorrectly;
        if (currentWordIndex < words.length) {
            String targetWord = words[currentWordIndex];
            int limit = Math.min(userBuffer.length(), targetWord.length());
            for (int i = 0; i < limit; i++) {
                if (userBuffer.charAt(i) == targetWord.charAt(i)) {
                    count++;
                } else {
                    break;
                }
            }
        }
        return count;
    }
}