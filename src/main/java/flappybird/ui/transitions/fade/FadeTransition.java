package flappybird.ui.transitions.fade;

import flappybird.math.Vector2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Stack;

import static flappybird.math.Lerp.lerp;

public abstract class FadeTransition {
    protected boolean fadeIn;
    protected float fadeTimer = 0.f;
    protected int transition;
    protected float alphaValue = 0;
    protected float transitionDuration = 1.f;
    protected boolean startTransition = false;
    protected static Stack<Integer> transitionEvent = new Stack<>();
    protected Color prevColor;
    protected boolean transitioning = false;
    protected float speed = 1.f;

    public void start() {
        if(!startTransition && !transitioning) {
            startTransition = true;
            fadeIn = false;
        }
    }

    public void updateTransition(Graphics2D g2, Vector2<Integer> windowSize, float dt) {

        if(startTransition) {
            //!do one time
            transition = transitionEvent.pop();
            prevColor = g2.getColor();
            startTransition = false;
            transitioning = true;
        }
        if(transitioning) {

            switch (transition) {
                case TransitionType.DIP_TO_BLACK -> g2.setColor(new Color(0, 0, 0, (int)alphaValue));
                case TransitionType.DIP_TO_WHITE -> g2.setColor(new Color(255, 255, 255, (int)alphaValue));
            }
            fadeTimer += speed * dt;
            fadeTimer = Math.min(fadeTimer, 1.f);
            if (fadeIn) {
                alphaValue = lerp(alphaValue, 0, fadeTimer);
                if (alphaValue <= 1) {
                    alphaValue = 0;
                    g2.setColor(prevColor);
                    transitioning = false;
                    fadeIn = false;
                    fadeTimer = 0;
                }
            } else {
                alphaValue = lerp(alphaValue, 225, fadeTimer);
                if (alphaValue >= 224) {
                    fadeIn = true;
                    fadeTimer = 0;
                }
            }

            g2.fillRect(0, 0, windowSize.x, windowSize.y);
            System.out.println("Alpha value : " + alphaValue);
            System.out.printf("fadeTimer : %.2f%n", fadeTimer);
        }
    }

    static class TransitionType {
        public static final int DIP_TO_BLACK = 0;
        public static final int DIP_TO_WHITE = 1;
    }

}
