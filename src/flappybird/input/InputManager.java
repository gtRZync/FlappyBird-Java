package flappybird.input;

import java.awt.Component;
@FunctionalInterface
interface ResetState {
    void resetInputStatesAfter();
}

public class InputManager implements ResetState {
    public KeyBoard keyBoard;
    public Mouse mouse;

    public InputManager(Component ctx) {
        if(ctx == null) {
            throw new IllegalArgumentException("Component context ('ctx') cannot be null when creating an InputManager instance.");
        }
        keyBoard = new KeyBoard(ctx);
        mouse = new Mouse(ctx);
    }

    @Override
    public void resetInputStatesAfter() {
        this.keyBoard.resetInputStatesAfter();
        this.mouse.resetInputStatesAfter();
    }
}