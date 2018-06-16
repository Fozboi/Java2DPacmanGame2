package br.ol.pacman.engine;

import br.ol.pacman.engine.PacmanGame.State;
import java.awt.Rectangle;

public class PowerPellet extends Pellet {

    private boolean eaten;
    
    PowerPellet(final PacmanGame game,
                final int col,
                final int row) {
        super(game, col, row);
        loadFrames("/res/powerBall.png");
        this.x = col * 8 + 1 - 32;
        this.y = (row + 3) * 8 + 1;
        this.boundingBox = new Rectangle(0, 0, 4, 4);
        this.eaten = true;
    }

    @Override
    public void update() {
        this.setVisible(!this.eaten && (int) (System.nanoTime() * 0.0000000075) % 2 == 0);
        if (this.eaten || this.getGame().getState() == State.PACMAN_DIED) {
            return;
        }
        if (this.getGame().pelletConsumed(this)) {
            this.eaten = true;
            this.setVisible(false);
            this.getGame().addScore(50);
            this.getGame().startGhostVulnerableMode();
        }
    }

    @Override
    public void stateChanged() {
        if (this.getGame().getState() == PacmanGame.State.TITLE
                || this.getGame().getState() == State.LEVEL_CLEARED
                || this.getGame().getState() == State.GAME_OVER) {
            this.eaten = true;
        }
        else if (this.getGame().getState() == PacmanGame.State.READY) {
            this.eaten = false;
            this.setVisible(true);
        }
    }

    public void hideAll() {
        this.setVisible(false);
    }

    public void showAll() {
        this.setVisible(true);
    }
    
}
