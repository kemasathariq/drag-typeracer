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
        // Jika game selesai, matikan fungsi mengetik
        if (gamePanel.isFinished) return;

        boolean correct = gamePanel.getWordManager().checkKey(e.getKeyChar());
        if(correct) {
            gamePanel.updatePlayerMovement();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Jika Game Selesai DAN tombol yang ditekan adalah ENTER
        if (gamePanel.isFinished && e.getKeyCode() == KeyEvent.VK_ENTER) {
            gamePanel.restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}