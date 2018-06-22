package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

public class Ready extends PacManEntity {

    public Ready(final PacmanGame game) {
        super(game);
        setXPosition(11 * 8);
        setYPosition(20 * 8);
        loadFrames(new String[]{"ready.png"});
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

}
