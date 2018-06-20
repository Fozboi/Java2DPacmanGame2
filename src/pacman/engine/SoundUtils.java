package pacman.engine;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SoundUtils
{

    private static final BlockingQueue<URL> URL_BLOCKING_QUEUE = new ArrayBlockingQueue<URL>(1);

    public static void playSoundStream(String soundFilePath) {
        Clip clip = null;
        final InputStream in;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(soundFilePath);
            clip = AudioSystem.getClip();
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
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playSound(String soundFilePath) {
        final InputStream in;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(soundFilePath);
            final AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
            System.out.println(AudioPlayer.player.isAlive());
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}