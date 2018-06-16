package pacman.engine;

import java.awt.Graphics2D;

public class HUD extends PacmanEntity {

    HUD(final PacmanGame game) {
        super(game);
        loadFrames("/resources/pacman_life.png");
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(final Graphics2D g) {
        if (!this.isVisible()) {
            return;
        }
        this.getGame().drawText(g, "SCORE", 10, 1);
        this.getGame().drawText(g, this.getGame().getScore(), 10, 10);
        this.getGame().drawText(g, "HIGH SCORE ", 78, 1);
        this.getGame().drawText(g, this.getGame().getHighScore(), 90, 10);
        this.getGame().drawText(g, "LIVES: ", 10, 274);
        for (int lives = 0; lives < this.getGame().getLives(); lives++) {
            g.drawImage(this.frame, 60 + 20 * lives, 272, null);
        }
    }

    @Override
    public void stateChanged() {
        this.setVisible((this.getGame().getState() != PacmanGame.State.INITIALIZING)
                && (this.getGame().getState() !=PacmanGame.State.OL_PRESENTS));
    }

    public void showAll() {
        this.setVisible(true);
    }

}
