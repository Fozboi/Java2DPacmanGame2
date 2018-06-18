package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;
import java.awt.Rectangle;

public class PowerPellet extends Pellet {

    public PowerPellet(final PacmanGame game,
                       final int col,
                       final int row) {
        super(game);
        loadFrames("/resources/powerBall.png");
        this.setX(col * 8 + 1 - 32);
        this.setY((row + 3) * 8 + 1);
        this.setBoundingBox(new Rectangle(0, 0, 4, 4));
        this.eaten = true;
    }

    @Override
    public void update() {
        this.setInstructionPointer((this.getInstructionPointer() + 1) % 50);
        this.setVisible(!this.eaten &&  this.getInstructionPointer() > 25);
        if (this.eaten || this.getGame().getState() == State.PACMAN_DIED) {
            return;
        }
        if (this.getGame().consumePellet(this)) {
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
