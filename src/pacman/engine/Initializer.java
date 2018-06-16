package pacman.engine;

import pacman.engine.PacmanGame.State;

import java.awt.*;

class Initializer extends PacmanEntity {

    Initializer(final PacmanGame game) {
        super(game);
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.INITIALIZING) {
            this.setVisible(true);
            switch (this.instructionPointer) {
                case 0:
                    this.startTime = System.currentTimeMillis();
                    this.instructionPointer = 1;
                    break;
                case 1:
                    if (System.currentTimeMillis() - this.startTime < 3000) {
                        break;
                    }
                    this.instructionPointer = 2;
                case 2:
                    this.getGame().setState(State.OL_PRESENTS);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.isVisible()) {
            this.getGame().drawText(g, "Initializer!", 60, 130);
        }
    }

    @Override
    public void stateChanged() {
        //do nothing
    }

    public void showAll() {
        this.setVisible(true);
    }

}
