package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AssetManager {
    public BufferedImage menuBackground; 
    
    public BufferedImage background;
    public BufferedImage carBlue, carPink, carRed, carYellow;

    public AssetManager() {
        try {
            menuBackground = ImageIO.read(new File("res/menu_bg.png"));
            background = ImageIO.read(new File("res/background.png"));
            carBlue = ImageIO.read(new File("res/car_blue.png"));
            carPink = ImageIO.read(new File("res/car_pink.png"));
            carRed = ImageIO.read(new File("res/car_red.png"));
            carYellow = ImageIO.read(new File("res/car_yellow.png"));
        } catch (IOException e) {
            System.err.println("Gambar Error");
            e.printStackTrace();
        }
    }
}