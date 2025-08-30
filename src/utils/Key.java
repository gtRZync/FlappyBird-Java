package utils;

import java.awt.event.KeyEvent;

public enum Key {
    ESCAPE(KeyEvent.VK_ESCAPE),
    F3(KeyEvent.VK_F3),
    SPACE(KeyEvent.VK_SPACE);

    private Key(int vKey) {
        this.vKey = vKey;
    }
    public int vKey() {
        return this.vKey;
    }
    private final int vKey;
}