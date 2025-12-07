package entities;

import java.awt.image.BufferedImage;

public class Car extends Entity {

    private boolean isPlayer;
    private double speedPerTick;
    private double preciseX;
    private String name; // <--- TAMBAHAN BARU

    // Update Constructor: Tambah parameter String name
    public Car(String name, int x, int y, BufferedImage sprite, boolean isPlayer) {
        super(x, y);
        this.name = name; // Simpan nama
        this.sprite = sprite;
        this.isPlayer = isPlayer;
        this.preciseX = x;
    }

    public String getName() { return name; } // Getter nama

    public void setBotSpeed(int wpm, int totalChars, int trackDistance) {
        if (isPlayer) return;
        double totalTimeSeconds = ((double) totalChars / 5 / wpm) * 60.0;
        double totalFrames = totalTimeSeconds * 60;
        this.speedPerTick = (double) trackDistance / totalFrames;
    }

    public void updatePlayerPosition(double percent, int trackStart, int trackLength) {
        if (!isPlayer) return;
        int targetX = trackStart + (int) (percent * trackLength);
        this.x = targetX; 
    }

    @Override
    public void update() {
        if (!isPlayer) {
            preciseX += speedPerTick;
            this.x = (int) preciseX;
        }
    }
    
    // Method untuk reset posisi saat restart
    public void reset(int startX) {
        this.x = startX;
        this.preciseX = startX;
    }
}