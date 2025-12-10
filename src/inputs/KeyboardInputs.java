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
        if (gamePanel.isFinished) return;
        if (e.getKeyChar() == '\n') return;
        
        gamePanel.startRaceTimer();

        gamePanel.getWordManager().typeKey(e.getKeyChar());
        gamePanel.updatePlayerMovement();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gamePanel.isFinished) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                gamePanel.restartGame();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                gamePanel.backToMenu();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}