package flappybird.ui.component;

import flappybird.graphics.texture.Texture;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIButton extends UIElement{

    public UIButton(int x, int y, int width, int height, Texture texture, Texture pressedTexture) {
        super(x, y, width, height);
        internal = new JButton();
        ((JButton)internal).setFocusPainted(false);
        ((JButton)internal).setBorderPainted(false);
        ((JButton)internal).setContentAreaFilled(false);
        internal.setBounds(x, y, width, height);

        ((JButton) internal).setIcon(new ImageIcon(getScaledInstance(texture)));
        ((JButton) internal).setPressedIcon(new ImageIcon(getScaledInstance(pressedTexture)));
        setupMouseListener();
    }

    private Image getScaledInstance(Texture texture) {
        return texture.getImage().getScaledInstance(internal.getWidth(), internal.getHeight(), Image.SCALE_SMOOTH);
    }

    public JComponent getComponent() {
        return internal;
    }

    private void setupMouseListener() {
        internal.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    pressed = true;
                    up = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    pressed = false;
                    up = true;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
            }
        });
    }
}
