package utils;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter implements ResetState {
    public Button LEFT_BUTTON;
    public Button RIGHT_BUTTON;

    public Mouse(Component ctx) {
        if(ctx == null) return;
        ctx.addMouseListener(this);
        this.LEFT_BUTTON = new Button();
        this.RIGHT_BUTTON = new Button();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            LEFT_BUTTON.pressed = true;
            LEFT_BUTTON.released = false;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            LEFT_BUTTON.pressed = false;
            LEFT_BUTTON.released = true;
        }
    }

    @Override
    public void resetInputStateAfter() {
        LEFT_BUTTON.pressed = false;
        LEFT_BUTTON.released = false;
        RIGHT_BUTTON.pressed = false;
        RIGHT_BUTTON.released = false;
    }
}