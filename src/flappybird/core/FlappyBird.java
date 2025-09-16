package flappybird.core;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FlappyBird {
    public static void main(String[] args) {
        ImageIcon icon = new ImageIcon("icon.png");

        JFrame frame = new JFrame(GameConstants.GAME_NAME);
        frame.setIconImage(icon.getImage());
        frame.setSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(new GamePanel(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);

                new Thread(() -> {
                    long s = System.currentTimeMillis();
                    SoundManager.shutdown();
                    long end = System.currentTimeMillis();
                    System.out.println("\n[INFO] - Sound cleanup took "+ (end - s)/1000+"s.");
                    frame.dispose();
                    System.exit(0);
                }).start();
            }
        });
    }
}

