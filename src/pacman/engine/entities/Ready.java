package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

public class Ready extends PacManEntity {

    public Ready(final PacmanGame game) {
        super(game);
        this.setX(11 * 8);
        this.setY(20 * 8);
        loadFrames("/resources/ready.png");
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.READY) {
            switch (this.getInstructionPointer()) {
                case 0:
                    this.getGame().restoreCurrentFoodCount();
                    this.setStartTime(System.currentTimeMillis());
                    this.setInstructionPointer(1);
                    break;
                case 1:
                    if (System.currentTimeMillis() - this.getStartTime() < 2000) {
                        break;
                    }
                    this.getGame().setState(State.READY2);
                    break;
            }
        } else if (this.getGame().getState() == State.READY2) {
            switch (this.getInstructionPointer()) {
                case 0:
                    this.getGame().showAll();
                    this.setStartTime(System.currentTimeMillis());
                    this.setInstructionPointer(1);
                case 1:
                    if (System.currentTimeMillis() - this.getStartTime() < 2000) {
                        break;
                    }
                    this.getGame().setState(State.PLAYING);
                    break;
            }
        }
    }

    @Override
    public void stateChanged() {
        this.setVisible(false);
        if (this.getGame().getState() == PacmanGame.State.READY ||
                this.getGame().getState() == PacmanGame.State.READY2) {
            this.setVisible(true);
            this.setInstructionPointer(0);
        }
    }

    public void showAll() {
        this.setVisible(true);
    }

}
