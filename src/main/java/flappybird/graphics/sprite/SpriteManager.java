package flappybird.graphics.sprite;

import flappybird.core.GameConstants;
import flappybird.graphics.sprite.data.SpriteData;
import flappybird.graphics.sprite.io.SpriteJsonLoader;
import flappybird.graphics.texture.Texture;
import flappybird.graphics.sprite.data.SpriteData.SpriteRect;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private static final Map<String, Texture> textureMap;

    static {
        textureMap = new HashMap<>();
        Texture spritesheet = new Texture(GameConstants.SPRITESHEET_PATH);
        SpriteData data = SpriteJsonLoader.fromJson(GameConstants.JSON_FILE_PATH);
        for(SpriteRect rect : data.getSpriteData()) {
            BufferedImage image = spritesheet.getImage().getSubimage(rect.x, rect.y, rect.width, rect.height);
            Texture texture = new Texture(image);
            textureMap.put(rmPng(rect.fileName), texture);
            System.out.printf("[INFO] - Cached Texture  \"%s\" with id : '%s' .\n",rect.fileName, rmPng(rect.fileName));
        }
    }

    //!might make it rmExtension(String original)
    private static String rmPng(String original) {
        if(original.endsWith(".png")) {
            return original.substring(0, original.lastIndexOf('.'));
        }
        System.out.println("[WARNING] - .png was not found in filename.\nOriginal filename will be returned.");
        return original;
    }

    public static Texture getTexture(String id) {
        Texture texture = textureMap.get(id);
        if(texture == null) {
            throw new RuntimeException("[ERROR] - Texture %s was not loaded.\n" + id);
        }
        return texture;
    }
}
