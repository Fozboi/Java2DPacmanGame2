package pacman.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


abstract class PacManEntity {

    private final PacmanGame game;
    private double x;
    private double y;
    private boolean visible;
    private BufferedImage frame;
    private BufferedImage[] frames;
    private Rectangle boundingBox;
    private int instructionPointer;
    private long startTime;

    private static final boolean DRAW_BOUNDING_BOX = true;

    PacManEntity(final PacmanGame game) {
        this.game = game;
    }

    double getX() {
        return this.x;
    }

    double getY() {
        return this.y;
    }

    BufferedImage getFrame() {
        return this.frame;
    }

    BufferedImage[] getFrames() {
        return this.frames;
    }

    int getInstructionPointer() {
        return this.instructionPointer;
    }

    Rectangle getBoundingBox() {
        return this.boundingBox;
    }

    public abstract void update();

    public abstract void stateChanged();

    public void draw(final Graphics2D g) {
        if (this.isVisible()) {
            if (this.getFrame() != null) {
                g.drawImage(this.getFrame(), (int) this.getX(), (int) this.getY(), this.getFrame().getWidth(), this.getFrame().getHeight(), null);
            }
            if (DRAW_BOUNDING_BOX && this.getBoundingBox() != null) {
                updateBoundingBox();
                g.setColor(Color.RED);
                g.draw(this.getBoundingBox());
            }
        }
    }

    void loadFrames(final String... framesRes) {
        try {
            this.setFrames(new BufferedImage[framesRes.length]);
            for (int i = 0; i < framesRes.length; i++) {
                this.getFrames()[i] = ImageIO.read(getClass().getResourceAsStream(framesRes[i]));
            }
            this.setFrame(this.getFrames()[0]);
        } catch (final IOException ex) {
            Logger.getLogger(PacManEntity.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public void updateBoundingBox() {
        if (this.getBoundingBox() != null) {
            this.getBoundingBox().setLocation((int) this.getX(), (int) this.getY());
        }
    }

    PacmanGame getGame() {
        return this.game;
    }

    boolean isVisible() {
        return this.visible;
    }

    void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public void showAll() {
    }

    public void hideAll() {
    }

    void setX(final double x) {
        this.x = x;
    }

    void setY(final double y) {
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

    void setInstructionPointer(final int instructionPointer) {
        this.instructionPointer = instructionPointer;
    }

    long getStartTime() {
        return this.startTime;
    }

    void setStartTime(final long startTime) {
        this.startTime = startTime;
    }
}
