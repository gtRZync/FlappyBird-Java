package flappybird.graphics.texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Texture{
    private BufferedImage image;


    public Texture(String path) {
        URL url = Texture.class.getResource(path);
        if(url == null) {
            throw new IllegalArgumentException("[ERROR] - Unable to locate texture file: " + path);
        }
        try
        {
            image = ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException("[ERROR] - Failed to read texture file",e);
        }
    }

    public Texture(BufferedImage image) { this.image = image; }


    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
}
