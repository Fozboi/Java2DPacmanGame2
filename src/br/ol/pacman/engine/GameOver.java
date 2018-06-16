package br.ol.pacman.engine;


import br.ol.pacman.engine.PacmanGame.State;

public class GameOver extends PacmanEntity {
    
    GameOver(final PacmanGame game) {
        super(game);
        this.x = 77;
        this.y = 160;
        loadFrames("/res/gameover.png");
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.GAME_OVER) {
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
                        this.getGame().returnToTitle();
                        break yield;
                }
            }
        }
    }

    @Override
    public void stateChanged() {
        this.setVisible(false);
        if (this.getGame().getState() == State.GAME_OVER) {
            this.setVisible(true);
            this.instructionPointer = 0;
        }
    }

    public void showAll() {
        this.setVisible(true);
    }
        
}
