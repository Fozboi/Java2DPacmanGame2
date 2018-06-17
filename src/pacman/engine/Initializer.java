package pacman.engine;

import pacman.engine.PacmanGame.State;

import java.awt.*;

class Initializer extends PacManEntity {

    Initializer(final PacmanGame game) {
        super(game);
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.INITIALIZING) {
            this.setVisible(true);
            switch (this.getInstructionPointer()) {
                case 0:
                    this.setStartTime(System.currentTimeMillis());
                    this.setInstructionPointer(1);
                    break;
                case 1:
                    if (System.currentTimeMillis() - this.getStartTime() < 3000) {
                        break;
                    }
                    this.setInstructionPointer(2);
                    break;
                case 2:
                    this.getGame().setState(State.OL_PRESENTS);
                    this.setVisible(false);
                    break;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.isVisible()) {
            this.getGame().drawText(g, "Hi Jasmine!", 60, 130);
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
