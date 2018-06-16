package pacman.engine;

import pacman.engine.PacmanGame.State;

public class Ready extends PacmanEntity {

    Ready(final PacmanGame game) {
        super(game);
        this.x = 11 * 8;
        this.y = 20 * 8;
        loadFrames("/resources/ready.png");
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.READY) {
            switch (this.instructionPointer) {
                case 0:
                    this.getGame().restoreCurrentFoodCount();
                    this.startTime = System.currentTimeMillis();
                    this.instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - this.startTime < 2000) {
                        break;
                    }
                    this.getGame().setState(State.READY2);
                    break;
            }
        } else if (this.getGame().getState() == State.READY2) {
            switch (this.instructionPointer) {
                case 0:
                    this.getGame().showAll();
                    this.startTime = System.currentTimeMillis();
                    this.instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - this.startTime < 2000) {
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
            this.instructionPointer = 0;
        }
    }

    public void showAll() {
        this.setVisible(true);
    }

}
