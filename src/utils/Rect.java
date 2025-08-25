package utils;

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
    Rect(vector2<T> position, vector2<T> size) {
        this(position.x, position.y, size.x, size.y);
    }
    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        Stroke stroke = g.getStroke();
        g.setStroke(new BasicStroke(2));
        g.drawLine(left.intValue(), top.intValue(),  left.intValue() + width.intValue(), top.intValue());
        g.drawLine(left.intValue(), top.intValue(),  left.intValue(), top.intValue() + height.intValue());
        g.drawLine(left.intValue() + width.intValue(), top.intValue(),  left.intValue() + width.intValue(), top.intValue() + height.intValue());
        g.drawLine(left.intValue(), top.intValue() + height.intValue(),  left.intValue() + width.intValue(), top.intValue() + height.intValue());
        g.setStroke(stroke);
    }
    public void setPosition(vector2<T> newPos) {
        this.left = newPos.x;
        this.top = newPos.y;
    }
    public void setPosition(T left, T top) {
        this.left = left;
        this.top = top;
    }
    public boolean intersects(Rect<T> other) {
        int this_right = this.left.intValue() + this.width.intValue();
        int this_bottom = this.top.intValue() + this.height.intValue();
        int other_right = other.left.intValue() + other.width.intValue();
        int other_bottom = other.top.intValue() + other.height.intValue();
        return (
                this.left.intValue() < other_right&&
                        this_right > other.left.intValue() &&
                        this.top.intValue() < other_bottom &&
                        this_bottom > other.top.intValue()
        );
    }

}