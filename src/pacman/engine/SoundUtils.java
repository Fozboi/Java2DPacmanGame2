package pacman.engine;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SoundUtils
{

    private static final BlockingQueue<URL> URL_BLOCKING_QUEUE = new ArrayBlockingQueue<>(1);

    public static void playSoundStream(String soundFilePath) {
        try {
            final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(soundFilePath);
            final Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(in);
            clip.open(inputStream);
            clip.start();
            LineListener listener = event -> {
                if (event.getType() != LineEvent.Type.STOP) {
                    return;
                }
                try {
                    URL_BLOCKING_QUEUE.take();
                } catch (InterruptedException e) {
                    //ignore this
                }
            };
            clip.addLineListener(listener);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

}