package flappybird;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import utils.vector2;
import utils.Rect;

public class Bird
{
    private vector2<Float> position;
    private final vector2<Integer> size;
    public Rect<Float> bounds;//!Make private
    private final BufferedImage sprite;
    public  float velocityY = 1.f;
    public boolean isGrounded = false;
    private final ImageObserver observer;
    Bird(float x, float y, int width, int height, BufferedImage sprite, ImageObserver observer) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero.");
        }
        this.position = new vector2<>(x, y);
        this.size = new vector2<>(width, height);
        this.sprite = sprite;
        this.observer = observer;
        bounds = new Rect<>(x, y,(float)width,(float)height);
    }

    public vector2<Float> getPosition() { return this.position; }

    public vector2<Integer> getSize() { return this.size; }

    public void move(float x, float y)
    {
        this.position.x += x;
        this.position.y += y;
        bounds.setPosition(position);
    }
    public void move(vector2<Float> newPos)
    {
        this.position.x += newPos.x;
        this.position.y += newPos.y;
        bounds.setPosition(position);
    }

    public void setPosition(vector2<Float> newPos)
    {
        this.position = newPos;
        bounds.setPosition(position);
    }

    public void setPosition(float x, float y)
    {
        this.position = new vector2<>(x, y);
    }

    public void drawSprite(Graphics2D g2) {
        g2.drawImage(
                this.sprite,
                this.position.x.intValue(),
                this.position.y.intValue(),
                (int)(sprite.getWidth() * 1.8),
                (int)(sprite.getHeight() * 1.7),
                observer
        );
    }

    public boolean isCollidedWith(Rect<Float> rect) {
        return bounds.intersects(rect);
    }
}