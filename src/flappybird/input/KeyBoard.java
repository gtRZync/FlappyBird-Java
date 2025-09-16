package flappybird.input;

import java.util.Map;
import java.util.HashMap;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyBoard extends KeyAdapter implements ResetState {
    private final Map<Key, ButtonState> keyStates;

    public KeyBoard(Component ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("Component context ('ctx') cannot be null when creating a KeyBoard instance.");
        }
        ctx.addKeyListener(this);
        keyStates = new HashMap<>();
        for(Key key : Key.values()) {
            keyStates.put(key, new ButtonState());
        }
    }

    /**
     * @deprecated Prefer using simpler methods like {@link #isPressed(Key)} for clarity.
     * Use this only if you need full button state (e.g., {@code heldDuration()}, {@code wasReleasedRecently()}).
     *
     * @apiNote Useful when {@code Button} provides extended input state beyond a simple boolean,
     * such as {@code heldDuration()}, {@code wasReleasedRecently(int duration)}, etc.
     */
    @Deprecated
    public ButtonState key(Key key) {
        return keyStates.get(key);
    }

    public boolean isPressed(Key key) {
        return keyStates.get(key).down;
    }
    public boolean isReleased(Key key) {
        return keyStates.get(key).up;
    }
    public boolean isHeld(Key key) {
        return keyStates.get(key).pressed;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for(Key key : Key.values()) {
            if(e.getKeyCode() == key.vKey()) {
                keyStates.get(key).down = true;
                keyStates.get(key).up = false;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for(Key key : Key.values()) {
            if(e.getKeyCode() == key.vKey()) {
                keyStates.get(key).down = false;
                keyStates.get(key).up = true;
            }
        }
    }

    @Override
    public void resetInputStatesAfter() {
        for(Key key : Key.values()) {
            keyStates.get(key).up = false;
            keyStates.get(key).down = false;
        }
    }
}