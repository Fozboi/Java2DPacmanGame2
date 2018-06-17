package pacman.engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

class Display extends Canvas {

    private final PacmanGame game;
    private boolean running;
    private BufferStrategy bs;

    Display(final PacmanGame game) {
        this.game = game;
        final int sx = (int) (game.getScreenSize().getWidth() * game.getScreenScale().getX());
        final int sy = (int) (game.getScreenSize().getHeight() * game.getScreenScale().getY());
        setPreferredSize(new Dimension(sx, sy));
        addKeyListener(KeyBoard.get());
    }

    private PacmanGame getGame() {
        return this.game;
    }

    private boolean isRunning() {
        return this.running;
    }

    void start() {
        if (!this.isRunning()) {
            createBufferStrategy(3);
            this.setDisplayBuffer(getBufferStrategy());
            this.getGame().initializeGameObjects();
            this.running = true;
            Thread thread = new Thread(new MainLoop(this));
            thread.start();
        }
    }

    private BufferStrategy getDisplayBuffer() {
        return this.bs;
    }

    private void setDisplayBuffer(BufferStrategy bs) {
        this.bs = bs;
    }

    private class MainLoop implements Runnable {

        final Display display;

        MainLoop(final Display display) {
            this.display = display;
        }

        @Override
        public void run() {
            long desiredFrameRateTime = 1000 / 60;
            long currentTime = System.currentTimeMillis();
            long lastTime = currentTime - desiredFrameRateTime;
            long unprocessedTime = 0;
            boolean needsRender = false;
            while (this.display.isRunning()) {
                currentTime = System.currentTimeMillis();
                unprocessedTime += currentTime - lastTime;
                lastTime = currentTime;
                while (unprocessedTime >= desiredFrameRateTime) {
                    unprocessedTime -= desiredFrameRateTime;
                    this.display.getGame().update();
                    needsRender = true;
                }
                if (needsRender) {
                    final Graphics2D g = (Graphics2D) this.display.getDisplayBuffer().getDrawGraphics();
                    g.setBackground(Color.BLACK);
                    g.clearRect(0, 0, getWidth(), getHeight());
                    g.scale(this.display.getGame().getScreenScale().getX(), this.display.getGame().getScreenScale().getY());
                    this.display.getGame().draw(g);
                    g.dispose();
                    this.display.getDisplayBuffer().show();
                    needsRender = false;
                }
            }
        }

    }

}
