package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AssetManager {
    // Tambahkan variabel baru untuk background menu
    public BufferedImage menuBackground; 
    
    public BufferedImage background; // Ini tetap untuk background game/arena
    public BufferedImage carBlue, carPink, carRed, carYellow;

    public AssetManager() {
        try {
            // Load gambar menu background baru
            menuBackground = ImageIO.read(new File("res/menu_bg.png"));
            
            // Load gambar lainnya seperti biasa
            background = ImageIO.read(new File("res/background.png"));
            carBlue = ImageIO.read(new File("res/car_blue.png"));
            carPink = ImageIO.read(new File("res/car_pink.png"));
            carRed = ImageIO.read(new File("res/car_red.png"));
            carYellow = ImageIO.read(new File("res/car_yellow.png"));
        } catch (IOException e) {
            System.err.println("GAGAL MEMUAT GAMBAR!");
            System.err.println("Pastikan ada file 'menu_bg.png' dan 'background.png' di folder 'res/'");
            e.printStackTrace();
        }
    }
}