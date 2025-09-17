package flappybird.ui.component;

import flappybird.math.Rect;
import flappybird.math.Vector2;

import java.awt.Graphics2D;

@FunctionalInterface
interface Drawable {
    void draw(Graphics2D g2);
}

public abstract class UIElement implements  Drawable{
    private final Rect<Integer> bounds;
    int x;
    int y;
    int width;
    int height;
    public UIElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        bounds = new Rect<>(x, y, width, height);
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

    @Override
    public void draw(Graphics2D g2) {
        System.out.println("Drawing UIElement at (" + x + "," + y + ")");
    }

}


