package br.ol.pacman.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


abstract class PacmanEntity {

    private final PacmanGame game;
    double x;
    double y;
    private boolean visible;
    BufferedImage frame;
    BufferedImage[] frames;
    Rectangle boundingBox;
    int instructionPointer;
    long waitTime;

    private static final boolean DRAW_BOUNDING_BOX = true;

    PacmanEntity(final PacmanGame game) {
        this.game = game;
    }

    public abstract void update();

    public abstract void stateChanged();

    public void draw(final Graphics2D g) {
        if (this.isVisible()) {
            if (this.frame != null) {
                g.drawImage(this.frame, (int) this.x, (int) this.y, this.frame.getWidth(), this.frame.getHeight(), null);
            }
            if (DRAW_BOUNDING_BOX && this.boundingBox != null) {
                updateBoundingBox();
                g.setColor(Color.RED);
                g.draw(this.boundingBox);
            }
        }
    }

    void loadFrames(final String... framesRes) {
        try {
            this.frames = new BufferedImage[framesRes.length];
            for (int i = 0; i < framesRes.length; i++) {
                this.frames[i] = ImageIO.read(getClass().getResourceAsStream(framesRes[i]));
            }
            this.frame = this.frames[0];
        } catch (final IOException ex) {
            Logger.getLogger(PacmanEntity.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public void updateBoundingBox() {
        if (this.boundingBox != null) {
            this.boundingBox.setLocation((int) this.x, (int) this.y);
        }
    }

    public PacmanGame getGame() {
        return this.game;
    }

    boolean isVisible() {
        return this.visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
}
