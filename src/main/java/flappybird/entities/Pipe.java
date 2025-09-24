package flappybird.entities;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import flappybird.graphics.sprite.SpriteManager;
import flappybird.graphics.texture.Texture;
import flappybird.utils.Utils;
import flappybird.math.Vector2;
import flappybird.math.Rect;


public class Pipe {
    private final Vector2<Float> position;
    private final ImageObserver observer;
    private final Rect<Float> bounds;
    private static final Texture texture = SpriteManager.getTexture("pipe2");
    private static final Texture textureFlipped = Utils.flipTextureVertically(SpriteManager.getTexture("pipe2"));
    private final int width;
    private final int height;
    public static float scale = 1.7f;
    private final boolean isBottom;
    public boolean scored = false;

    public static final int MAX_HEIGHT = 300;
    public static final int MIN_HEIGHT = 50;
    public static final int UPPER = 0;
    public static final int BOTTOM = 1;

    public Pipe(float cx, float cy, int height, ImageObserver observer)
    {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null.");
        }
        position = new Vector2<>(cx, cy);
        this.height = (int) (texture.getImage().getHeight() * scale);
        this.width = (int) (texture.getImage().getWidth() * scale);
        this.isBottom = this.position.y > 0;
        this.observer = observer;
        bounds = new Rect<>(cx, cy, (float) width, (float)height);
    }

    public float getWidth() {
        return this.width;
    }

    public float getCx() { return this.position.x; }
    public void move(float cx, float cy) {
        this.position.x += cx;
        this.position.y += cy;
        if(!isBottom) {
            bounds.setPosition(position.x, position.y);
        }
        else {
            bounds.setPosition(position.x ,position.y - (height) );
        }
    }
    public void setScored(boolean scored) { this.scored = scored; }
    public boolean isScored() {return this.scored; }

    public static int getTextureWidth() {
        return texture.getImage().getWidth();
    }
    public static int getTextureHeight() {
        return texture.getImage().getHeight();
    }

    public void spawn(Graphics2D g2)
    {
        if(!this.isBottom)
        {
            g2.drawImage(textureFlipped.getImage(), position.x.intValue(), position.y.intValue(), width, height, observer);
        }
        else
        {
            g2.drawImage(texture.getImage(), position.x.intValue(), position.y.intValue() - height, width, height, observer);
        }
    }
    public void drawBounds(Graphics2D g2) {
        bounds.draw(g2);
    }

    public Rect<Float> getBounds() {
        return this.bounds;
    }
}