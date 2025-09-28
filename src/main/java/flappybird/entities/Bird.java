package flappybird.entities;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.Map;
import java.util.Objects;

import flappybird.core.GameState;
import flappybird.core.GameConstants;
import flappybird.graphics.sprite.SpriteManager;
import flappybird.graphics.texture.Texture;
import flappybird.math.Vector2;
import flappybird.math.Rect;

public class Bird
{
    public static final float STARTING_Y = 250.f;
    public static final float DEFAULT_X = 100.f;
    public static final float JUMP_VELOCITY = -8;
    public static final float TERMINAL_VELOCITY = 10;
    public static final float TERMINAL_DOWNWARD_VELOCITY = 14;
    private Vector2<Float> position;
    private final Vector2<Integer> size;
    private final Rect<Float> bounds;
    private double tiltAngle  = 0;

    private final ImageObserver observer;

    public float velocityY = 1.0f;
    public boolean isBelowCeiling = false;
    public boolean isGrounded = false;

    private float animationTimer = 0.f;
    //? Animation speed in frames per second (modifiable)
    private float ANIMATION_FPS = 10.f;
    private GameState prevGamestate = GameState.MENU;
    //? Duration each animation frame is shown (derived from FPS)
    private float frameDuration = 1.f / ANIMATION_FPS;
    private int currentFrame = 0;

    private static final double MAX_UPWARD_ANGLE = Math.toRadians(-20); // Tilt up when jumping
    public static final double MAX_DOWNWARD_ANGLE = Math.toRadians(90); // Terminal down tilt
    private static final double TILT_SPEED = Math.toRadians(465); //? How fast it tilts down per second

    private float headUpTimer = 0.f;
    private boolean startHeadUpTimer;

    private final Map<Integer, Texture> texture = Map.of(
            0, SpriteManager.getTexture("yellowbird-upflap"),
            1, SpriteManager.getTexture("yellowbird-midflap"),
            2, SpriteManager.getTexture("yellowbird-downflap")
    );


    public Bird(ImageObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null.");
        }
        float scale = 1.7f;
        this.position = new Vector2<>(DEFAULT_X, STARTING_Y);
        this.size = new Vector2<>((int)(getTexture().getImage().getWidth() * scale), (int)(getTexture().getImage().getHeight() * scale));
        this.observer = observer;
        bounds = new Rect<>(DEFAULT_X, STARTING_Y,(float)size.x,(float)size.y);
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

    public void renderTexture(Graphics2D g2) {
        Graphics2D g = (Graphics2D) g2.create();
        double width = position.x + size.x /  2.f;
        double height = position.y + size.y /  2.f;
        g.rotate(tiltAngle, width, height);
        g.drawImage(
                getTexture().getImage(),
                this.position.x.intValue(),
                this.position.y.intValue(),
                size.x,
                size.y,
                observer
        );
        g.dispose();
    }
    //!Just rotate the rendering not the actual positions
    public void drawBounds(Graphics2D g2) {
        Graphics2D g = (Graphics2D) g2.create();
        double width = bounds.getPosition().x + bounds.getSize().x /  2.f;
        double height = bounds.getPosition().y + bounds.getSize().y /  2.f;
        g.rotate(tiltAngle, width, height);
        bounds.draw(g);
        g.dispose();
    }

    public double getAngle() {
        return this.tiltAngle;
    }

    private float getFPSForState(GameState state) {
        if (state == GameState.PLAYING) {
            return 25.f;
        }
        return 10.f;
    }

    private void setFrameDuration(float ANIMATION_FPS) {
        this.ANIMATION_FPS = ANIMATION_FPS;
        frameDuration = 1.f / this.ANIMATION_FPS;
    }

    private void animate( float deltaTime) {
        this.animationTimer += deltaTime;

        if(animationTimer >= frameDuration) {
            currentFrame = (currentFrame + 1) % texture.size();
            animationTimer -= frameDuration;
        }
    }

    public void startHeadUpTimer(boolean state) {
        headUpTimer = 0.f;
        this.startHeadUpTimer = state;
    }

    public void reset() {
        startHeadUpTimer = false;
        headUpTimer = 0.f;
        tiltAngle = 0;
        setPosition(DEFAULT_X, STARTING_Y);
    }

    private boolean shouldKeepHeadUp(float deltaTime) {
        boolean keepHeadUp = false;
        final float KEEP_HEAD_UP_THRESHOLD = .50f;
        if(startHeadUpTimer) {
            headUpTimer += deltaTime;
            if(headUpTimer <= KEEP_HEAD_UP_THRESHOLD) {
                keepHeadUp = true;
            } else {
                headUpTimer -= KEEP_HEAD_UP_THRESHOLD;
                startHeadUpTimer = false;
            }
        }
        return keepHeadUp;
    }

    private void tilt(float deltaTime) {
        if(shouldKeepHeadUp(deltaTime)) {
            tiltAngle = MAX_UPWARD_ANGLE;
        } else {
            tiltAngle += TILT_SPEED * deltaTime;
            if(tiltAngle > MAX_DOWNWARD_ANGLE) {
                tiltAngle = MAX_DOWNWARD_ANGLE;
            }
        }
    }
    public void updateAnimation(GameState gamestate, float deltaTime) {
        if(prevGamestate != gamestate) {
            setFrameDuration(getFPSForState(gamestate));
            prevGamestate = gamestate;
        }

        if(gamestate != GameState.GAME_OVER) {
            animate(deltaTime);
        }

        if(gamestate == GameState.PLAYING || gamestate == GameState.GAME_OVER) {
            tilt(deltaTime);
        }
    }

    public Texture getTexture() {
        return texture.get(currentFrame);
    }

    public boolean isCollidedWith(Rect<Float> rect) {
        return bounds.intersects(rect);
    }
}