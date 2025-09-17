package flappybird.sprite.data;

import java.util.List;

public class SpriteData {
    private List<SpriteRect> sprites;

    private static class SpriteRect {
        String fileName;
        int x;
        int y;
        int width;
        int height;

        @Override
        public String toString() {
            return """
                    {
                      "filename": '%s',
                      "x":          %d,
                      "y":          %d,
                      "width":      %d,
                      "height":     %d
                    }""".formatted(fileName, x, y, width, height);
        }
    }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("SpriteData\n{");

            return sb.toString();
        }

}
