package pacman.engine.entities;

import pacman.engine.PacmanGame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class PacManEntity {

    private final PacmanGame game;
    private int x;
    private int y;
    private boolean visible;
    private BufferedImage frame;
    private BufferedImage[] frames;
    private Rectangle boundingBox;
    private int entityCounter;
    private long startTime;

    private static final boolean DRAW_BOUNDING_BOX = true;

    PacManEntity(final PacmanGame game) {
        this.game = game;
        this.entityCounter = 0;
    }

    public abstract void update();

    public void draw(final Graphics2D g) {
        if (isVisible()) {
            if (getFrame() != null) {
                g.drawImage(getFrame(), getX(), getY(), getFrame().getWidth(), getFrame().getHeight(), null);
            }
            if (DRAW_BOUNDING_BOX && this.getBoundingBox() != null) {
                g.setColor(Color.RED);
                g.draw(this.getBoundingBox());
            }
        }
    }

    public Rectangle getBoundingBox() {
        return this.boundingBox;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
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

    void loadFrames(final String... framesRes) {
        try {
            this.setFrames(new BufferedImage[framesRes.length]);
            for (int i = 0; i < framesRes.length; i++) {
                FileInputStream in = new FileInputStream(framesRes[i]);
                this.getFrames()[i] = ImageIO.read(in);
                in.close();
            }
            this.setFrame(this.getFrames()[0]);
        } catch (final IOException ex) {
            Logger.getLogger(PacManEntity.class.getName()).log(Level.SEVERE, null, ex);
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

    public void showEntity() {
    }

    public void hideEntity() {
    }

    void setX(final int x) {
        this.x = x;
    }

    void setY(final int y) {
        this.y = y;
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
