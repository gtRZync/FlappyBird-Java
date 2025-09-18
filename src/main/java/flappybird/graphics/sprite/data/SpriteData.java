package flappybird.graphics.sprite.data;

import java.util.List;

public class SpriteData {
    private List<SpriteRect> sprites;

    public static class SpriteRect {
        public String fileName;
        public int x;
        public int y;
        public int width;
        public int height;

        @Override
        public String toString() {
            return """
                    \t\t{
                    \t\t  "filename": "%s",
                    \t\t  "x":          %d,
                    \t\t  "y":          %d,
                    \t\t  "width":      %d,
                    \t\t  "height":     %d
                    \t\t}""".formatted(fileName, x, y, width, height);
        }
    }

    public List<SpriteRect> getSpriteData() {
        return sprites;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n");
        if(sprites != null) {
            sb.append("\t\"sprites\": [\n");
            for(SpriteRect rect : sprites) {
                sb.append(rect).append((rect != sprites.getLast()) ? ",\n" : "");
            }
            sb.append("\n\t]");
        }else {
            sb.append("data = null\n");
        }
        sb.append("\n}");
        return sb.toString();
    }
}
