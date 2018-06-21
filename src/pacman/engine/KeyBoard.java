package pacman.engine;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyBoard extends KeyAdapter {

    private final boolean[] keyPressed;

    private static final KeyBoard INSTANCE = new KeyBoard();

    private KeyBoard() {
        this.keyPressed = new boolean[256];
    }

    public static KeyBoard get() {
        return INSTANCE;
    }

    public boolean[] getKeyPressed() {
        return this.keyPressed;
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        System.out.println("key pressed " +e.getKeyCode());
        this.getKeyPressed()[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        this.getKeyPressed()[e.getKeyCode()] = false;
    }

}
