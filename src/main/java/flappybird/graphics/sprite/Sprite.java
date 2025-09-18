package flappybird.graphics.sprite;

import flappybird.graphics.texture.Texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite {
    private int x;
    private int y;
    private int width;
    private int height;
    private Texture texture;

    public Sprite() {

    }

    public void render(Graphics2D g2, int dx, int dy) {
        g2.drawImage(texture.getImage(), dx, dy, width, height, null);
    }

}
