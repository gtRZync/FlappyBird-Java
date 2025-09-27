package flappybird.ui.component;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ComponentManager {
    private final List<UIElement> elements = new ArrayList<>();

    //TODO: add get method and use an hashmap to avoid creating UIElement instances(kinda)
    public void addElement(UIElement element) {
        elements.add(element);
    }

    public void updateStates() {
          for (UIElement el : elements) {
              if(el instanceof UIButton){
                  el.setInputStateAfter();
              }
          }
    }

    public void render(Graphics2D g2) {
        for(UIElement el : elements) {
            el.draw(g2);
        }
    }
}