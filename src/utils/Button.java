package utils;

public class Button {
    private Key key;
    public boolean held = false;
    public boolean pressed = false;
    public boolean released = false;

    public Button() {
    }
    public Button(Key key) {
        this.key = key;
    }
}