package flappybird.math;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

public class Rect<T extends Number> {
    T left;
    T top;
    T width;
    T height;

    public Rect(T x, T y, T width, T height) {
        if (x == null || y == null || width == null || height == null) {
            throw new IllegalArgumentException("None of the parameters can be null.");
        }

        if (width.doubleValue() <= 0 || height.doubleValue() <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero.");
        }

        this.left = x;
        this.top = y;
        this.width = width;
        this.height = height;
    }
    Rect(Vector2<T> position, Vector2<T> size) {
        this(position.x, position.y, size.x, size.y);
    }
    public void draw(Graphics2D g) {
        int x = left.intValue();
        int y = top.intValue();
        int w = width.intValue();
        int h = height.intValue();

        Color prevColor = g.getColor();
        Stroke prevStroke = g.getStroke();

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));

        g.drawRect(x, y, w, h);

        g.setStroke(prevStroke);
        g.setColor(prevColor);
    }

    public void setPosition(Vector2<T> newPos) {
        this.left = newPos.x;
        this.top = newPos.y;
    }
    public void setPosition(T left, T top) {
        this.left = left;
        this.top = top;
    }
    public Vector2<T> getPosition() {
        return new Vector2<T>(left, top);
    }

    public Vector2<T> getSize() {
        return new Vector2<T>(width, height);
    }

    public boolean intersects(Rect<T> other) {
        int leftA = this.left.intValue();
        int topA = this.top.intValue();
        int rightA = leftA + this.width.intValue();
        int bottomA = topA + this.height.intValue();

        int leftB = other.left.intValue();
        int topB = other.top.intValue();
        int rightB = leftB + other.width.intValue();
        int bottomB = topB + other.height.intValue();

        return (
                leftA < rightB &&
                rightA > leftB &&
                topA < bottomB &&
                bottomA > topB
        );
    }

    public boolean contains(T x, T y) {
        int left = this.left.intValue();
        int top = this.top.intValue();
        int width = this.width.intValue();
        int height = this.height.intValue();

        int right = left + width;
        int bottom = top + height;

        int xVal = x.intValue();
        int yVal = y.intValue();

        return (xVal > left && xVal < right && yVal > top && yVal < bottom);
    }
}