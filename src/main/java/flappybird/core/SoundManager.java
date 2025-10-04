package flappybird.core;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SoundManager {
    private static final Map<String, List<Clip>> soundsPools = new HashMap<>();
    private static final Map<String, Clip> soundInstances = new HashMap<>();
    private static final int POOL_SIZE = 5;

    public static void init() {
        try
        {
            //?Use of a random audio file to warm up the audio mixer
            URL url = SoundManager.class.getResource(GameConstants.AUDIO_DIR+"die.wav");
            if(url == null) {
                throw new IllegalArgumentException();
            }
            AudioInputStream tempAudioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(tempAudioIn);
            tempAudioIn.close();
            clip.setFramePosition(clip.getFrameLength());
            clip.start();
            clip.stop();
            clip.close();
            System.out.println("[INFO] - Audio Mixer successfully warmed up.\n");
        }catch (LineUnavailableException e) {
            throw new RuntimeException("[ERROR] - Audio line is unavailable "+e);
        }catch(UnsupportedAudioFileException e) {
            throw new RuntimeException("[ERROR] - Unsupported audio file format "+e);
        }catch(IOException e) {
            throw new RuntimeException("[ERROR] - An I/O exception has occurred "+e);
        }
    }

    public static void loadSound(String id, String path) {
        try
        {
            URL url = SoundManager.class.getResource(path);
            if(url == null) {
                throw new IllegalArgumentException("[ERROR] - Unable to find file");
            }
            List<Clip> pool = new ArrayList<>();
            for(int i = 0; i < POOL_SIZE ; ++i) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                audioIn.close();
                pool.add(clip);
            }
            soundsPools.put(id, pool);
            System.out.printf("[INFO] - Sound %s was successfully loaded.\n", id);
        }
        catch (UnsupportedAudioFileException e) {
            throw new RuntimeException("[ERROR] - Unsupported audio file format "+e);
        } catch (IOException e) {
            throw new RuntimeException("[ERROR] - An I/O exception has occurred "+e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException("[ERROR] - Audio line is unavailable "+e);
        }
    }

    public static void loadSingleInstance(String id, String path) {
        try
        {
            URL url = SoundManager.class.getResource(path);
            if(url == null) {
                throw new IllegalArgumentException();
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            audioIn.close();

            clip.setFramePosition(clip.getFrameLength());
            clip.start();
            clip.stop();
            clip.setFramePosition(0);
            soundInstances.put(id, clip);
            System.out.printf("[INFO] - Sound %s was successfully loaded.\n", id);
        }
        catch(IOException e) {
            throw new RuntimeException("[ERROR] - Unable to locate file " +e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException("[ERROR] - Unsupported audio file"+e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException("[ERROR] - Line unavailable "+e);
        }
    }

    public static void playInstance(String id) {
        Clip clip = soundInstances.get(id);
        if(clip == null) {
            System.out.printf("[ERROR] - Sound %s not loaded.\n", id);
            return;
        }
        if(!clip.isRunning()){
            clip.setFramePosition(0);
            clip.start();
        }
    }
    public static void stopInstance(String id) {
        Clip clip = soundInstances.get(id);
        if(clip == null) return;
        if(clip.isOpen()) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }
    /**
     * Loads and registers all sounds provided in the map.
     * <p>
     * Each entry in the {@code sounds} map should contain:
     * <ul>
     *   <li><b>Key (String):</b> The unique sound identifier.</li>
     *   <li><b>Value (String):</b> The file path to the sound resource.</li>
     * </ul>
     *
     * @param sounds A map of sound IDs to their corresponding file paths.
     */
    public static void loadAll(HashMap<String, String> sounds) {

    }

    public static void play(String id) {
        List<Clip> pool = soundsPools.get(id);
        if(pool == null) {
            System.out.printf("[ERROR] - Sound %s was not loaded.", id);
            return;
        }
        for(Clip clip : pool) {
            if(!clip.isRunning()) {
                clip.setFramePosition(0);
                clip.start();
                return;
            }
        }
        Clip clip = pool.getFirst();
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public static void stop(String id) {
        List<Clip> pool = soundsPools.get(id);
        if(pool == null) return;

        for(Clip clip : pool) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    public static boolean isDone(String id) {
        List<Clip> pool = soundsPools.get(id);

        if(pool == null) {
            System.out.printf("[ERROR] - Sound %s was not loaded.", id);
            return false;
        }

        for(Clip clip : pool) {
            if(clip.isRunning()) {
                return false;
            }
        }
        return true;
    }

    public static void shutdown() {
        for(List<Clip> pool : soundsPools.values()) {
            for(Clip clip : pool) {
                clip.stop();
                clip.close();
            }
        }
        soundsPools.clear();
        for(Clip clip : soundInstances.values()) {
            clip.stop();
            clip.close();
        }
        soundInstances.clear();
    }
}
