package pacman.engine.entities;

import pacman.engine.PacmanGame;

import java.awt.*;

public class HUD extends PacManEntity {

    public HUD(final PacmanGame game) {
        super(game);
        loadFrames("pacman_life.png");
    }

    @Override
    public void update() {
        setVisible(getGame().getState() != PacmanGame.State.INITIALIZING);
    }

    @Override
    public void draw(final Graphics2D g) {
        if (isVisible()) {
            getGame().drawText(g, "SCORE", 10, 1);
            getGame().drawText(g, getGame().getScore(), 10, 10);
            getGame().drawText(g, "HIGH SCORE ", 78, 1);
            getGame().drawText(g, getGame().getHighScore(), 90, 10);
            getGame().drawText(g, "LIVES: ", 10, 274);
            for (int lives = 0; lives < getGame().getLives(); lives++) {
                g.drawImage(getFrame(), 60 + 20 * lives, 272, null);
            }
        }
    }

}
