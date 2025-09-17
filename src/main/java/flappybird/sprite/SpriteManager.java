package flappybird.sprite;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class SpriteManager {
    private Map<String, Sprite> spriteMap;

    public SpriteManager(String spritesheetPath) {

    }

    public Sprite getSprite(String id) {
        return new Sprite();
    }
}
