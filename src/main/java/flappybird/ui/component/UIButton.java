package flappybird.ui.component;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class UIButton extends UIElement{
    private String text;
    private BufferedImage icon;

    public UIButton(int x, int y, int width, int height, BufferedImage icon) {
        super(x, y, width, height);
        this.icon = icon;
    }

    private void renderImage(Graphics2D g2) {
        g2.drawImage(icon, x, y , width, height, null);
    }

    public void setIcon(BufferedImage icon) {
        this.icon = icon;
    }

    @Override
    public void draw(Graphics2D g2) {
        renderImage(g2);
    }
}
