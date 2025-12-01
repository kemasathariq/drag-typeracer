package entities;

import java.awt.Color;
import java.awt.Graphics2D;

public class PlayerCar extends Entity {

    private int acceleration = 2; // How much speed we gain per correct letter
    private int friction = 1;     // How fast we slow down automatically

    public PlayerCar(int x, int y) {
        super(x, y);
        this.maxSpeed = 10; // Cap the speed so we don't fly off instantly
    }

    public void accelerate() {
        speed += acceleration;
        if(speed > maxSpeed) {
            speed = maxSpeed;
        }
    }

    @Override
    public void update() {
        // 1. Move the car based on speed
        // In a real drag race, the car stays left, background moves right.
        // But for Phase 1 visualization, let's actually move the box right.
        x += speed;

        // 2. Apply Friction (Slow down if not typing)
        // We only slow down if we are moving
        if (speed > 0) {
            speed -= friction;
            // Prevent jittering (don't let friction make speed negative)
            if (speed < 0) {
                speed = 0;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // Draw the chassis (Body)
        g2.setColor(Color.RED);
        g2.fillRect(x, y, 50, 30); // 50x30 pixel box

        // Draw the engine/speed visual
        g2.setColor(Color.WHITE);
        g2.drawString("Speed: " + speed, x, y - 10);
    }
}