package flappybird.utils;

import flappybird.core.GameState;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class Utils {
    public static BufferedImage flipVertical(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -height);
        Graphics2D g = flipped.createGraphics();

        g.drawImage(original, transform, null);
        g.dispose();

        return flipped;
    }

    public static boolean notPlaying(GameState state) {
        return !state.equals(GameState.MENU) && !state.equals(GameState.PLAYING);
    }
}
