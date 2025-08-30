package utils;

import java.util.Map;
import java.util.HashMap;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyBoard extends KeyAdapter implements ResetState {
    private Map<Key, Button> keyStates;

    public KeyBoard(Component ctx) {
        if (ctx == null) return;
        ctx.addKeyListener(this);
        keyStates = new HashMap<>();
        for(Key key : Key.values()) {
            keyStates.put(key, new Button());
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
    public Button key(Key key) {
        return keyStates.get(key);
    }

    public boolean isPressed(Key key) {
        return keyStates.get(key).pressed;
    }
    public boolean isReleased(Key key) {
        return keyStates.get(key).released;
    }
    public boolean isHeld(Key key) {
        return keyStates.get(key).held;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for(Key key : Key.values()) {
            if(e.getKeyCode() == key.vKey()) {
                keyStates.get(key).pressed = true;
                keyStates.get(key).released = false;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for(Key key : Key.values()) {
            if(e.getKeyCode() == key.vKey()) {
                keyStates.get(key).pressed = false;
                keyStates.get(key).released = true;
            }
        }
    }

    @Override
    public void resetInputStateAfter() {
        for(Key key : Key.values()) {
            keyStates.get(key).released = false;
            keyStates.get(key).pressed = false;
        }
    }
}