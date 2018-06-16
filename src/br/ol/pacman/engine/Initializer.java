package br.ol.pacman.engine;

import br.ol.pacman.engine.PacmanGame.State;

class Initializer extends PacmanEntity {

    Initializer(final PacmanGame game) {
        super(game);
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.INITIALIZING) {
            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.waitTime < 3000) {
                            break yield;
                        }
                        this.instructionPointer = 2;
                    case 2:
                        this.getGame().setState(State.OL_PRESENTS);
                        break yield;
                }
            }
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
