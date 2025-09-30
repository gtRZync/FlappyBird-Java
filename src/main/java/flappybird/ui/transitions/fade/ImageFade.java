package flappybird.ui.transitions.fade;

import flappybird.graphics.texture.Texture;
import flappybird.math.Vector2;

import java.awt.Graphics2D;

public class ImageFade extends FadeTransition{
    private float delay;
    private float delayAccumulator;

    public ImageFade(Texture texture, int x, int y, int width, int height) {
        this.x       = x;
        this.y       = y;
        this.width   = width;
        this.height  = height;
        this.texture = texture;
    }

    public ImageFade(Texture texture, int x, int y, int width, int height ,float duration) {
        this(texture, x, y, width, height);
        this.transitionDuration = duration;
        this.speed = 1.f / transitionDuration;
    }

    @Override
    public void updateTransition(Graphics2D g2, Vector2<Integer> windowSize, float dt) {
        Graphics2D g = (Graphics2D) g2.create();
        super.updateTransition(g, windowSize, dt);
        g.dispose();
    }

    private void fade(boolean in) {
        super.start();
        transitionEvent.push(TransitionType.IMAGE_FADE);
        fadeIn = in;
    }

    public void fadeIn() {
        fade(true);
    }
    public void fadeOut() {
        fade(false);
    }
}
