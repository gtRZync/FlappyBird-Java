package flappybird.utils;

import flappybird.core.GameState;
import flappybird.graphics.texture.Texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class Utils {
    public static Texture flipTextureVertically(Texture original) {
        int width = original.getImage().getWidth();
        int height = original.getImage().getHeight();

        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -height);
        Graphics2D g = flipped.createGraphics();

        g.drawImage(original.getImage(), transform, null);
        g.dispose();

        return new Texture(flipped);
    }

    public static boolean notPlaying(GameState state) {
        return !state.equals(GameState.MENU) && !state.equals(GameState.PLAYING);
    }
}
