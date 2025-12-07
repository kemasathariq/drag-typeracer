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
        // 1. Jika game sudah selesai, jangan ngetik apa-apa
        if (gamePanel.isFinished) return;

        // --- PERBAIKAN BUG DISINI ---
        // 2. Abaikan tombol ENTER (karakter new line) agar tidak masuk ke Input Box
        if (e.getKeyChar() == '\n') return;
        // -----------------------------
        gamePanel.startRaceTimer();
        // 3. Kirim karakter ke WordManager
        gamePanel.getWordManager().typeKey(e.getKeyChar());
        
        // 4. Update posisi player
        gamePanel.updatePlayerMovement();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gamePanel.isFinished && e.getKeyCode() == KeyEvent.VK_ENTER) {
            gamePanel.restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}