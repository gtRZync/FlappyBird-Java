package flappybird.input;

import flappybird.math.Vector2;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter implements ResetState {
    public final ButtonState LEFT_BUTTON;
    public final ButtonState RIGHT_BUTTON;
    private Vector2<Integer> position;

    public Mouse(Component ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("Component context ('ctx') cannot be null when creating a Mouse instance.");
        }
        ctx.addMouseListener(this);
        this.LEFT_BUTTON = new ButtonState();
        this.RIGHT_BUTTON = new ButtonState();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            LEFT_BUTTON.down = true;
            LEFT_BUTTON.up = false;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            LEFT_BUTTON.down = false;
            LEFT_BUTTON.up = true;
        }
    }

    @Override
    public void resetInputStatesAfter() {
        LEFT_BUTTON.down = false;
        LEFT_BUTTON.up = false;
        RIGHT_BUTTON.down = false;
        RIGHT_BUTTON.up = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        position.x = e.getX();
        position.y = e.getY();
    }

    public Vector2<Integer> getPosition() {
        return position;
    }
}