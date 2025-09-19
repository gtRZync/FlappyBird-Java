package flappybird.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class GameConstants {
    public static final int DAYTIME_DURATION = 15; //!seconds
    public static final String GAME_NAME = "FlappyBird Clone";
    public static final String SPRITES_DIR = "/assets/sprites/";
    public static final String JSON_FILE_PATH = "/assets/sprites/sprite_sheet.json";
    public static final String SPRITESHEET_PATH = "/assets/sprites/sprite_sheet.png";
    public static final String AUDIO_DIR = "/assets/audio/";
    public static final int SCREEN_WIDTH = 480;
    public static final int SCREEN_HEIGHT = 650;
    public static final int DELAY = 10;


    public static final int VIRTUAL_FLOOR_HEIGHT = 210;
    public static final int FLOOR_SCALE_OFFSET = 110;
    public static final int FLOOR_HEIGHT = VIRTUAL_FLOOR_HEIGHT - FLOOR_SCALE_OFFSET;
    public static final float GRAVITY = .5f;
    public static final float X_SPEED = -2.f;
    public static final int SPACE_BETWEEN_PIPES = 180;
    public static final float MAX_HEIGHT = -100.f;

    public static BufferedImage BACKGROUND;
    public static BufferedImage FLOOR;
    public static BufferedImage[] BIRD = new BufferedImage[3];
    public static BufferedImage PIPE;
    public static BufferedImage MESSAGE;
    public static BufferedImage TITLE;


    static
    {
        try
        {
            MESSAGE = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"message.png")));
            TITLE = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"FlappyBird.png")));
            BACKGROUND = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"background-day.png")));
            PIPE = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"pipe2.png")));
            BIRD[0] = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"yellowbird-upflap.png")));
            BIRD[1] = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"yellowbird-midflap.png")));
            BIRD[2] = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"yellowbird-downflap.png")));
            FLOOR = ImageIO.read(Objects.requireNonNull(GameConstants.class.getResource(SPRITES_DIR+"floor.png")));
        }catch(IOException e) {
            throw new RuntimeException("Failed to load game assets", e);
        }
    }
}
