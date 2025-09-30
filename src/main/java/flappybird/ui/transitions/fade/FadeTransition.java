package flappybird.ui.transitions.fade;

import flappybird.graphics.texture.Texture;
import flappybird.math.Vector2;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.Stack;

import static flappybird.math.Lerp.lerp;

public abstract class FadeTransition {
    protected boolean fadeIn;
    protected float fadeTimer = 0.f;
    protected int transition;
    protected float alphaValue = 0;
    protected float transitionDuration = 1.f;
    protected boolean startTransition = false;
    protected Stack<Integer> transitionEvent = new Stack<>();
    protected Color prevColor;
    protected boolean transitioning = false;
    protected float speed = 1.f;

    //!Only used by ImageFade, they're here cuz ion want to add @updateTransition
    //!impl for every classes
    protected Texture texture;
    protected int x, y, width, height;

    public void start() {
        if(!startTransition && !transitioning) {
            startTransition = true;
            fadeIn = true;
        }
    }

    public boolean isFadingIn() {
        return fadeIn;
    }

    //TODO: add isFadingIn method for better UI transition
    public void updateTransition(Graphics2D g2, Vector2<Integer> windowSize, float dt) {

        if(startTransition) {
            transition = transitionEvent.pop();
            prevColor = g2.getColor();
            startTransition = false;
            transitioning = true;
        }
        if(transitioning) {

            switch (transition) {
                case TransitionType.DIP_TO_BLACK -> {
                    g2.setColor(new Color(0, 0, 0, Math.round(alphaValue * 255)));
                    g2.fillRect(0, 0, windowSize.x, windowSize.y);
                }
                case TransitionType.DIP_TO_WHITE -> {
                    g2.setColor(new Color(255, 255, 255, Math.round(alphaValue * 255)));
                    g2.fillRect(0, 0, windowSize.x, windowSize.y);
                }
                case TransitionType.IMAGE_FADE -> {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
                    g2.drawImage(texture.getImage(), x, y, width, height, null);
                }
            }
            fadeTimer += speed * dt;
            fadeTimer = Math.min(fadeTimer, 1.f);
            if (fadeIn) {
                alphaValue = lerp(alphaValue, 1.f, fadeTimer);
                if (alphaValue >= .9f) {
                    fadeIn = false;
                    fadeTimer = 0;
                }
            } else {
                alphaValue = lerp(alphaValue, 0.f, fadeTimer);
                if (alphaValue <= .1f) {
                    g2.setColor(prevColor);
                    transitioning = false;
                    fadeTimer = 0;
                    transition = TransitionType.NONE;
                }
            }
        }
    }

    protected static String typeToString(int type) {
        switch (type) {
            case TransitionType.DIP_TO_BLACK -> {
                return "DIP_TO_BLACK";
            }
            case TransitionType.DIP_TO_WHITE -> {
                return "DIP_TO_WHITE";
            }
            case TransitionType.IMAGE_FADE -> {
                return "IMAGE_FADE";
            }
            default -> {
                return "NONE";
            }
        }
    }

    static class TransitionType {
        public static final int NONE = -1;
        public static final int DIP_TO_BLACK = 0;
        public static final int DIP_TO_WHITE = 1;
        public static final int IMAGE_FADE = 2;
    }
}
