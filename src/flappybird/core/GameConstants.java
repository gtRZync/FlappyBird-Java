package flappybird.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class GameConstants {
    public static final int DAYTIME_DURATION = 15; //!seconds
    public static final String GAME_NAME = "FlappyBird Clone";
    public static final int SCREEN_WIDTH = 480;
    public static final int SCREEN_HEIGHT = 650;
    public static final int DELAY = 10;

    public static final int FLOOR_HEIGHT = 80;
    public static final float GRAVITY = .5f;
    public static final float JUMP_VELOCITY = -8;
    public static final float TERMINAL_VELOCITY = 10;
    public static final float X_SPEED = -2.f;
    public static final int SPACE_BETWEEN_PIPES = 180;
    public static final float MAX_HEIGHT = -100.f;

    public static BufferedImage BACKGROUND;
    public static BufferedImage FLOOR;
    public static BufferedImage[] BIRD = new BufferedImage[3];
    public static BufferedImage PIPE;
    public static BufferedImage MESSAGE;


    static
    {
        try
        {
            MESSAGE = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource("/assets/sprites/message.png")));
            BACKGROUND = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource("/assets/sprites/background-day.png")));
            PIPE = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource("/assets/sprites/pipe2.png")));
            BIRD[0] = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource("/assets/sprites/yellowbird-upflap.png")));
            BIRD[1] = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource("/assets/sprites/yellowbird-midflap.png")));
            BIRD[2] = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource("/assets/sprites/yellowbird-downflap.png")));
            FLOOR = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource("/assets/sprites/base.png")));
        }catch(IOException e) {
            throw new RuntimeException("Failed to load game assets", e);
        }
    }
}
