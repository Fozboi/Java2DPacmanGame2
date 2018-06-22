package pacman.engine;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundUtils
{

    public static void playSoundStream(String soundFilePath) {
        try {
            final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(soundFilePath);
            final Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(in);
            clip.open(inputStream);
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

}