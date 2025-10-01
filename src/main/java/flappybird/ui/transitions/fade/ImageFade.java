package flappybird.ui.transitions.fade;

import flappybird.graphics.texture.Texture;
import flappybird.math.Vector2;

import java.awt.Graphics2D;

public class ImageFade extends FadeTransition{
    private float delay;
    private float delayAccumulator;
    private boolean fadeImage = false;

    public ImageFade(Texture texture, int x, int y, int width, int height) {
        this.x       = x;
        this.y       = y;
        this.width   = width;
        this.height  = height;
        this.texture = texture;
        fadeImage = true;
    }

    public ImageFade(Texture texture, int x, int y, int width, int height ,float duration) {
        this(texture, x, y, width, height);
        this.transitionDuration = duration;
        this.speed = 1.f / transitionDuration;
        fadeImage = true;
    }

    public ImageFade(Texture texture, int x, int y, int width, int height ,float duration, float delay) {
        this(texture, x, y, width, height, duration);
        this.delay = delay;
        fadeImage = !(delay > 0.f);
    }

    @Override
    public void updateTransition(Graphics2D g2, Vector2<Integer> windowSize, float dt) {
        Graphics2D g = (Graphics2D) g2.create();
        if(fadeIn && !fadeImage) {
            delayAccumulator += dt;
            if(delayAccumulator >= delay) {
                fadeImage = true;
                delayAccumulator -= delay;
            }
        }
        if(fadeImage) {
            super.updateTransition(g, windowSize, dt);
        }
        g.dispose();
        if(alphaValue <= 0.f) fadeImage = false;
    }

    private void fade(boolean in) {
        super.start();
        transitionEvent = TransitionType.IMAGE_FADE;
        fadeIn = in;
    }

    public void fadeIn() {
        fade(true);
    }
    public void fadeOut() {
        fade(false);
    }
}
