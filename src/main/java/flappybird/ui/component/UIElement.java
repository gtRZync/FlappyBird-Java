package flappybird.ui.component;

import flappybird.math.Rect;
import flappybird.math.Vector2;

import javax.swing.JComponent;
import java.awt.*;

@FunctionalInterface
interface Drawable {
    void draw(Graphics2D g2);
}

public abstract class UIElement implements  Drawable{
    private final Rect<Integer> bounds;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected JComponent internal;
    protected boolean active;
    protected boolean up = false;
    protected boolean hovered = false;
    protected boolean pressed = false;
    protected boolean wasPressed = false;
    public UIElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        bounds = new Rect<>(x, y, width, height);
        active = true;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isUp() {
        return up;
    }

    public void setActive(boolean active) {
        this.active = active;
        internal.setVisible(active);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isHovered() {
        return hovered;
    }

    public Rect<Integer> getBounds() {
        return this.bounds;
    }

    public Vector2<Integer> getPosition() {
        return new Vector2<>(x, y);
    }

    public Vector2<Integer> getSize() {
        return new Vector2<>(width, height);
    }

    public void setInputStateAfter() {
        up = false;
        pressed = false;
    }

    @Override
    public void draw(Graphics2D g2) {
        System.out.println("Drawing UIElement at (" + x + "," + y + ")");
    }

}


