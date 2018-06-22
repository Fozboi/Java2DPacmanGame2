package pacman.engine.entities;

import pacman.engine.PacmanGame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


public abstract class PacManEntity {

    private final PacmanGame game;
    private int xPosition;
    private int yPosition;
    private boolean visible;
    private BufferedImage frame;
    private BufferedImage[] frames;
    private Rectangle boundingBox;
    private int entityCounter;
    private long startTime;

    PacManEntity(final PacmanGame game) {
        this.game = game;
        this.xPosition = 0;
        this.yPosition = 0;
        this.visible = false;
        this.entityCounter = 0;
        this.startTime = 0;
    }

    public abstract void update();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public void draw(final Graphics2D g) {
        if (isVisible()) {
            g.drawImage(getFrame(), getXPosition(), getYPosition(), getFrame().getWidth(), getFrame().getHeight(), null);
        }
    }

    public Rectangle getBoundingBox() {
        return this.boundingBox;
    }

    int getXPosition() {
        return this.xPosition;
    }

    int getYPosition() {
        return this.yPosition;
    }

    BufferedImage getFrame() {
        return this.frame;
    }

    BufferedImage[] getFrames() {
        return this.frames;
    }

    int getEntityCounter() {
        return this.entityCounter;
    }

    void loadFrames(final String[] framesRes) {
        try {
            this.setFrames(new BufferedImage[framesRes.length]);
            for (int i = 0; i < framesRes.length; i++) {
                final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(framesRes[i]);
                getFrames()[i] = ImageIO.read(in);
                in.close();
            }
            this.setFrame(getFrames()[0]);
        } catch (final IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    PacmanGame getGame() {
        return this.game;
    }

    public boolean isVisible() {
        return this.visible;
    }

    void setVisible(final boolean visible) {
        this.visible = visible;
    }

    void setXPosition(final int xPosition) {
        this.xPosition = xPosition;
    }

    void setYPosition(final int yPosition) {
        this.yPosition = yPosition;
    }

    void setFrame(final BufferedImage frame) {
        this.frame = frame;
    }

    private void setFrames(final BufferedImage[] frames) {
        this.frames = frames;
    }

    void setBoundingBox(final Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    void setEntityCounter(final int entityCounter) {
        this.entityCounter = entityCounter;
    }

    void resetEntityCounter() {
        this.entityCounter = 0;
    }

    void incrementEntityCounter() {
        this.entityCounter++;
    }

    void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    long getElapsedTime() {
        return System.currentTimeMillis() - this.startTime;
    }
}
