package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

public class WordManager {

    private String[] words = {"java", "code", "class", "object", "system", "swing", "pixel", "race"};
    private String currentWord;
    private int currentIndex; // Tracks which letter we are on (0, 1, 2...)
    private Random random;
    private boolean wordCompleted;

    public WordManager() {
        random = new Random();
        generateNewWord();
    }

    public void generateNewWord() {
        // Pick a random word from the array
        int index = random.nextInt(words.length);
        currentWord = words[index];
        currentIndex = 0;
        wordCompleted = false;
        System.out.println("New word generated: " + currentWord); // Debugging
    }

    public boolean checkKey(char key) {
    char targetChar = currentWord.charAt(currentIndex);

    if (Character.toLowerCase(key) == targetChar) {
        currentIndex++;
        if (currentIndex >= currentWord.length()) {
            wordCompleted = true;
            generateNewWord();
        }
        return true; // <--- RETURN TRUE (Correct Hit)
    } else {
        return false; // <--- RETURN FALSE (Miss)
    }
}

    public void draw(Graphics2D g2) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        
        // Draw the full word in Gray (Ghost text)
        g2.setColor(Color.GRAY);
        g2.drawString(currentWord, 300, 200);

        // Draw the typed part in Green (Overlay)
        g2.setColor(Color.GREEN);
        String typedPart = currentWord.substring(0, currentIndex);
        g2.drawString(typedPart, 300, 200);
    }
}