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

    public Sprite(Texture texture) {
        this.texture = texture;
        this.width = texture.getImage().getWidth();
        this.height = texture.getImage().getHeight();
    }

    public Texture getTexture() {
        return texture;
    }

    public int getWidth() {
        return texture.getImage().getWidth();
    }

    public int getHeight() {
        return texture.getImage().getHeight();
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        this.width = texture.getImage().getWidth();
        this.height = texture.getImage().getHeight();
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(Graphics2D g2) {
        g2.drawImage(texture.getImage(), x, y, width, height, null);
    }

}
