package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Entity {
    protected int x, y;
    protected BufferedImage sprite; // Gambar untuk entitas ini
    
    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract void update();
    
    public void draw(Graphics2D g2) {
        if (sprite != null) {
            // Gambar ukuran 90x45 (sesuai referensi)
            g2.drawImage(sprite, x, y, 90, 45, null);
        }
    }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
}