package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

public class Background extends PacManEntity {

    private int frameCount;

    public Background(final PacmanGame game) {
        super(game);
        loadFrames("/resources/background_0.png", "/resources/background_1.png");
    }

    @Override
    public void update() {

        if (getGame().getState() == State.TITLE) {
            setVisible(false);
        } else if (getGame().getState() == State.READY) {
            setVisible(true);
        } else if(getGame().getState() == State.LEVEL_CLEARED) {
                switch (getEntityCounter()) {
                    case 0:
                        this.frameCount = 0;
                        startTimer();
                        incrementEntityCounter();
                        break;
                    case 1:
                        if (getElapsedTime() > 1500) {
                            incrementEntityCounter();
                        }
                        break;
                    case 2:
                        setFrame(getFrames()[1]);
                        startTimer();
                        incrementEntityCounter();
                        break;
                    case 3:
                        if (getElapsedTime() > 200) {
                            setFrame(getFrames()[0]);
                            startTimer();
                            incrementEntityCounter();
                        }
                        break;
                    case 4:
                        if (getElapsedTime() > 200) {
                            this.frameCount++;
                            if (this.frameCount < 5) {
                                setEntityCounter(2);
                                return;
                            }
                        }
                        getGame().hideAll();
                        startTimer();
                        incrementEntityCounter();
                        break;
                    case 5:
                        if (getElapsedTime() > 500) {
                            setVisible(true);
                            getGame().nextLevel();
                        }
                        break;
                }
        }
    }

    @Override
    public void hideEntity() {
        this.setVisible(false);
    }

    @Override
    public void showEntity() {
        this.setVisible(true);
    }

}
