package pacman.engine;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class KeyBoard extends KeyAdapter {

    private final boolean[] keyPressed;

    private static final KeyBoard INSTANCE = new KeyBoard();

    private KeyBoard() {
        this.keyPressed = new boolean[256];
    }

    static KeyBoard get() {
        return INSTANCE;
    }

    boolean[] getKeyPressed() {
        return this.keyPressed;
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        this.getKeyPressed()[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        this.getKeyPressed()[e.getKeyCode()] = false;
    }

}
