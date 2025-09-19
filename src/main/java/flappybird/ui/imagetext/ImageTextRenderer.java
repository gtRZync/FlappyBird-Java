package flappybird.ui.imagetext;

import flappybird.graphics.sprite.Sprite;
import flappybird.graphics.sprite.SpriteManager;
import flappybird.math.Vector2;

import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;

//!Might delete it if this its only use
public class ImageTextRenderer {
    private List<Sprite> scoreSprite;
    private int prevScore = -1;
    private final Vector2<Integer> windowSize;
    private final int defaultWidth;

    public ImageTextRenderer(Vector2<Integer> windowSize) {
        this.windowSize = windowSize;
        Sprite zero = new Sprite(SpriteManager.getTexture("0"));
        scoreSprite = new ArrayList<>();
        scoreSprite.add(zero);
        this.defaultWidth = scoreSprite.getFirst().getWidth();
    }

    private List<Sprite> scoreToSprites(int score) {
        char[] arr = Integer.toString(score).toCharArray();
        List<Sprite> sprites = new ArrayList<>();
        for(char c : arr) {
           Sprite sprite = new Sprite(SpriteManager.getTexture(Character.toString(c)));
           sprites.add(sprite);
        }
        return sprites;
    }

    public void renderScore(Graphics2D g2, int score) {
        boolean scoreUpdated = (prevScore != score);
        if(scoreUpdated) {
            scoreSprite = scoreToSprites(score);
            int paddingX = 6;
            int sizeW = 0;
            int sizeH = scoreSprite.getFirst().getHeight(); //?every number have the same height
            int w = windowSize.x;
            int h = windowSize.y;
            for(Sprite s : scoreSprite) {
                sizeW += defaultWidth;
                if(s != scoreSprite.getLast()) {
                    sizeW += paddingX;
                }
            }
            int posX = (w - sizeW) / 2;
            int posY = (h - sizeH) / 10;
            for(int i = 0 ; i < scoreSprite.size(); i++) {
                scoreSprite.get(i).setPosition(posX + (i * (defaultWidth + paddingX )), posY);
            }
            prevScore = score;
        }
        for(Sprite s : scoreSprite)
        {
            s.render(g2);
        }
    }
}
