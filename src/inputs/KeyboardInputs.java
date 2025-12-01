package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    // Check with the Brain
    boolean correct = gamePanel.getWordManager().checkKey(e.getKeyChar());
    
    // If correct, move the Body
    if(correct) {
        gamePanel.getPlayerCar().accelerate();
    }
}

    @Override
    public void keyPressed(KeyEvent e) {
        // We don't need this for typing, but good for "Pause" or "Exit"
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for now
    }
}