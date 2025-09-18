package flappybird.graphics.sprite.io;

import com.google.gson.Gson;
import flappybird.graphics.sprite.data.SpriteData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

//!felt cute might delete later
public class SpriteJsonLoader {
    private static final Gson parser = new Gson();

    public static SpriteData fromJson(String jsonPath) {
        SpriteData data;
        URL url = SpriteJsonLoader.class.getResource(jsonPath);
        if(url == null) {
            throw new IllegalArgumentException("[ERROR] - Unable to locate json file: " + jsonPath);
        }
        try(InputStreamReader reader = new InputStreamReader(url.openStream()))
        {
            data = parser.fromJson(reader, SpriteData.class);
        } catch (IOException e) {
            throw new RuntimeException("[ERROR] - Unable to locate json file : " +e);
        }
        return data;
    }
}
