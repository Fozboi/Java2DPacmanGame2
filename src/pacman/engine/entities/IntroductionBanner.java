package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

import java.awt.*;

public class IntroductionBanner extends PacManEntity {

    public IntroductionBanner(final PacmanGame game) {
        super(game);
    }

    @Override
    public void update() {
        if (getGame().getState() == State.INITIALIZING) {
            setVisible(true);
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 3000) {
                        incrementEntityCounter();
                    }
                    break;
                case 2:
                    getGame().setState(State.TITLE);
                    setVisible(false);
                    break;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (isVisible()) {
            getGame().drawText(g, "Presenting", 60, 130);
        }
    }

}
