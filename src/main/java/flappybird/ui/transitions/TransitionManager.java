package flappybird.ui.transitions;

import flappybird.math.Vector2;
import flappybird.ui.transitions.fade.FadeTransition;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

//TODO: add base class Transition that every abstract transition will inherit of
public class TransitionManager {
    private final List<FadeTransition> transitionList;
    public TransitionManager() {
        transitionList = new ArrayList<>();
    }

    public void push(FadeTransition transition) {
        if(transitionList.contains(transition)) {
            System.out.println("[WARNING] - transition already present in manager.");
            return;
        }
        transitionList.add(transition);
    }

    public void update(Graphics2D g2, Vector2<Integer> windowSize, float dt) {
        for(FadeTransition t : transitionList) {
            t.updateTransition(g2, windowSize, dt);
        }
    }
}
