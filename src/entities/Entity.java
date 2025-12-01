package entities;

import java.awt.Graphics2D;

public abstract class Entity {
    // Protected means children (PlayerCar) can access these directly
    protected int x, y;
    protected int speed;
    protected int maxSpeed;
    
    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Every entity must have these, but they might do them differently
    public abstract void update(); 
    public abstract void draw(Graphics2D g2);
    
    public int getX() { return x; }
    public int getY() { return y; }
}