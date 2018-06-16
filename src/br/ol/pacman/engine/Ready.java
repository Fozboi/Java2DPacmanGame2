package br.ol.pacman.engine;

import br.ol.pacman.engine.PacmanGame.State;

public class Ready extends PacmanEntity {

    Ready(final PacmanGame game) {
        super(game);
        this.x = 11 * 8;
        this.y = 20 * 8;
        loadFrames("/res/ready.png");
    }

    @Override
    public void update() {
        if(this.getGame().getState() == State.READY) {
        yield:
        while (true) {
            switch (this.instructionPointer) {
                case 0:
                    this.getGame().restoreCurrentFoodCount();
                    this.waitTime = System.currentTimeMillis();
                    this.instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - this.waitTime < 2000) { // || game.sounds.get("start").isPlaying()) {
                        break yield;
                    }
                    this.getGame().setState(State.READY2);
                    break yield;
            }
        }
        } else if(this.getGame().getState() == State.READY2) {
            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.getGame().broadcastMessage("showAll");
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.waitTime < 2000) { // || game.sounds.get("start").isPlaying()) {
                            break yield;
                        }
                        this.getGame().setState(State.PLAYING);
                        break yield;
                }
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
