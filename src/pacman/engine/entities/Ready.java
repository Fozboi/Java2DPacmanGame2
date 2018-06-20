package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

public class Ready extends PacManEntity {

    public Ready(final PacmanGame game) {
        super(game);
        setX(11 * 8);
        setY(20 * 8);
        loadFrames("resources/ready.png");
    }

    @Override
    public void update() {
        if (getGame().getState() == State.READY) {
            setVisible(true);
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 2000) {
                        incrementEntityCounter();
                    }
                    break;
                case 2:
                    getGame().showAll();
                    startTimer();
                    incrementEntityCounter();
                case 3:
                    if (getElapsedTime() > 2000) {
                        setVisible(false);
                        resetEntityCounter();
                        getGame().setState(State.PLAYING);
                    }
                    break;
            }
        }
    }

    @Override
    public void showEntity() {
        setVisible(true);
    }

}
