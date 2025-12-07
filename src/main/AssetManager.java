package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AssetManager {
    public BufferedImage background;
    public BufferedImage carBlue, carPink, carRed, carYellow;

    public AssetManager() {
        try {
            // Pastikan path file sesuai. Jika di root project, langsung nama file.
            // Jika di folder res, gunakan "res/namafile.png"
            background = ImageIO.read(new File("res/background.png"));
            carBlue = ImageIO.read(new File("res/car_blue.png"));
            carPink = ImageIO.read(new File("res/car_pink.png"));
            carRed = ImageIO.read(new File("res/car_red.png"));
            carYellow = ImageIO.read(new File("res/car_yellow.png"));
        } catch (IOException e) {
            System.err.println("GAGAL MEMUAT GAMBAR!");
            e.printStackTrace();
        }
    }
}