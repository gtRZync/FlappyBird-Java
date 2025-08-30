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
            keyStates.put(key, new Button(key));
        }
    }

    public Button key(Key key) {
        return keyStates.get(key);
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