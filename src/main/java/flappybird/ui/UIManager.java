package flappybird.ui;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import flappybird.ui.component.UIElement;

public class UIManager {
    private final List<UIElement> elements = new ArrayList<>();

    public void addElement(UIElement element) {
        elements.add(element);
    }

    public void render(Graphics2D g2) {
        for(UIElement el : elements) {
            el.draw(g2);
        }
    }
}