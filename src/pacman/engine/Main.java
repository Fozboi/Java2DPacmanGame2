package pacman.engine;

import javax.swing.*;

class Main {

    public static void main(final String[] args) {
        final PacmanGame game = new PacmanGame();
        final Display display = new Display(game);
        final JFrame frame = new JFrame();
        frame.setTitle("Pacman");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(display);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        display.requestFocus();
        display.start();
    }

}
