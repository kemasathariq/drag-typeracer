package entities;

import java.awt.image.BufferedImage;

public class Car extends Entity {

    private boolean isPlayer;
    private double speedPerTick;
    private double preciseX;
    private String name;
    
    // Status apakah mobil ini Hybrid (Rival) atau Fixed
    private boolean isHybrid = false; 

    public Car(String name, int x, int y, BufferedImage sprite, boolean isPlayer) {
        super(x, y);
        this.name = name;
        this.sprite = sprite;
        this.isPlayer = isPlayer;
        this.preciseX = x;
    }
    
    // Setter & Getter untuk status Hybrid
    public void setHybrid(boolean isHybrid) {
        this.isHybrid = isHybrid;
    }
    
    public boolean isHybrid() {
        return isHybrid;
    }

    public String getName() { return name; }

    public void setBotSpeed(int wpm, int totalChars, int trackDistance) {
        if (isPlayer) return;
        double totalTimeSeconds = ((double) totalChars / 5 / wpm) * 60.0;
        double totalFrames = totalTimeSeconds * 60;
        this.speedPerTick = (double) trackDistance / totalFrames;
    }
    
    // Method untuk update speed secara real-time (khusus Hybrid)
    public void setDirectSpeed(double speed) {
        this.speedPerTick = speed;
    }

    public void updatePlayerPosition(double percent, int trackStart, int trackLength) {
        if (!isPlayer) return;
        int targetX = trackStart + (int) (percent * trackLength);
        this.x = targetX; 
    }
    
    // Reset posisi dan speed saat restart
    public void reset(int startX) {
        this.x = startX;
        this.preciseX = startX;
        // Speed akan di-set ulang di setupRace/restart logic
    }

    @Override
    public void update() {
        if (!isPlayer) {
            preciseX += speedPerTick;
            this.x = (int) preciseX;
        }
    }
}