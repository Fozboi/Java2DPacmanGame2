package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

public class GameOverBanner extends PacManEntity {

    public GameOverBanner(final PacmanGame game) {
        super(game);
        setX(77);
        setY(160);
        loadFrames("/resources/gameover.png");
    }

    @Override
    public void update() {
        if (getGame().getState() == State.GAME_OVER) {
            setVisible(true);
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 3000) {
                        getGame().returnToTitle();
                    }
                    break;
            }
        } else {
            setVisible(false);
        }
    }

}
