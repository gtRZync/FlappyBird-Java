package flappybird;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.Objects;

public enum Audio {
    DIE("/assets/audio/die.wav"),
    SWOOSH("/assets/audio/swoosh.wav"),
    POINT("/assets/audio/point.wav"),
    HIT("/assets/audio/hit.wav");

    private Audio(String filename) {
        try
        {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(Audio.class.getResource(filename)));
            audio = AudioSystem.getClip();
            audio.open(audioStream);
            audioStream.close();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException("[ERROR] - Unsupported audio file", e);
        } catch (IOException e) {
            throw new RuntimeException("[ERROR] - an IOException has occurred", e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException("[ERROR] - Line unavailable", e);
        }
    }
    private final Clip audio;

    public void play() {
        audio.start();
        audio.setMicrosecondPosition(0);
    }
    public void close() {
        if(audio != null && audio.isOpen()) {
            audio.close();
        }
    }
}
