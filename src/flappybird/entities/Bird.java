package flappybird.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import flappybird.core.GameState;
import flappybird.core.GameConstants;
import flappybird.math.Vector2;
import flappybird.math.Rect;

public class Bird
{
    private Vector2<Float> position;
    private final Vector2<Integer> size;
    private final Rect<Float> bounds;

    private final ImageObserver observer;

    public float velocityY = 1.0f;
    public boolean isBelowCeiling = false;
    public boolean isGrounded = false;

    private float animationTimer = 0.f;
    private float idleTimer = 0.f;
    //? Animation speed in frames per second (modifiable)
    private static final float ANIMATION_FPS = 10.f;
    //? Duration each animation frame is shown (derived from FPS)
    private static final float frameDuration = 1.f / ANIMATION_FPS;
    private int currentFrame = 0;

    public Bird(float x, float y, ImageObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null.");
        }
        this.position = new Vector2<>(x, y);
        this.size = new Vector2<>((int)(GameConstants.BIRD[0].getWidth() * 1.8), (int)(GameConstants.BIRD[0].getHeight() * 1.7));
        this.observer = observer;
        bounds = new Rect<>(x, y,(float)size.x,(float)size.y);
    }

    public Vector2<Float> getPosition() { return this.position; }

    public Vector2<Integer> getSize() { return this.size; }

    public void move(float x, float y)
    {
        this.position.x += x;
        this.position.y += y;
        bounds.setPosition(position);
        isGrounded = (position.y + size.y) > (GameConstants.SCREEN_HEIGHT - GameConstants.FLOOR_HEIGHT);
        isBelowCeiling = position.y <= GameConstants.MAX_HEIGHT;
    }

    public void setPosition(float x, float y)
    {
        this.position = new Vector2<>(x, y);
    }

    public void drawSprite(Graphics2D g2) {
        g2.drawImage(
                getCurrentFrame(),
                this.position.x.intValue(),
                this.position.y.intValue(),
                size.x,
                size.y,
                observer
        );
    }
    public void drawBounds(Graphics2D g2) {
        bounds.draw(g2);
    }

    public void idleBob(GameState state, float deltaTime) {
        if(!state.equals(GameState.START)) return;

        final float amplitude = 2.f;
        final float speed = 8.f;
        idleTimer += deltaTime;

        float offsetY = (float) (amplitude * Math.sin(speed * idleTimer));

        move(0.f, offsetY);
    }

    public void animate( float deltaTime) {
        this.animationTimer += deltaTime;

        if(animationTimer >= frameDuration) {
            currentFrame = (currentFrame + 1) % GameConstants.BIRD.length;
            animationTimer -= frameDuration;
        }
    }

    private BufferedImage getCurrentFrame() {
        return GameConstants.BIRD[currentFrame];
    }

    public boolean isCollidedWith(Rect<Float> rect) {
        return bounds.intersects(rect);
    }
}