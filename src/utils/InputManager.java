package utils;

import java.awt.Component;

interface ResetState {
    void resetInputStateAfter();
}

public class InputManager implements ResetState {
    public KeyBoard keyBoard;
    public Mouse mouse;

    public InputManager(Component ctx) {
        keyBoard = new KeyBoard(ctx);
        mouse = new Mouse(ctx);
    }

    @Override
    public void resetInputStateAfter() {
        this.keyBoard.resetInputStateAfter();
        this.mouse.resetInputStateAfter();
    }
}