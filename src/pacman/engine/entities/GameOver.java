package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

public class GameOver extends PacManEntity {

    public GameOver(final PacmanGame game) {
        super(game);
        this.setX(77);
        this.setY(160);
        loadFrames("/resources/gameover.png");
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.GAME_OVER) {
            switch (this.getInstructionPointer()) {
                case 0:
                    this.setStartTime(System.currentTimeMillis());
                    this.setInstructionPointer(1);
                    break;
                case 1:
                    if (System.currentTimeMillis() - this.getStartTime() < 3000) {
                        break;
                    }
                    this.getGame().returnToTitle();
                    ;
            }
        }
    }

    @Override
    public void stateChanged() {
        if (this.getGame().getState() == State.GAME_OVER) {
            this.setVisible(true);
            this.setInstructionPointer(0);
        } else {
            this.setVisible(false);
        }
    }

}
