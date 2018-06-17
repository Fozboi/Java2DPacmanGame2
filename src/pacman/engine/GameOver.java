package pacman.engine;


import pacman.engine.PacmanGame.State;

public class GameOver extends PacManEntity {

    GameOver(final PacmanGame game) {
        super(game);
        this.setX(77);
        this.setY(160);
        loadFrames("/resources/gameover.png");
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.GAME_OVER) {
            while (true) {
                switch (this.getInstructionPointer()) {
                    case 0:
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(1);
                    case 1:
                        if (System.currentTimeMillis() - this.getStartTime() < 3000) {
                            return;
                        }
                        this.getGame().returnToTitle();
                        return;
                }
            }
        }
    }

    @Override
    public void stateChanged() {
        this.setVisible(false);
        if (this.getGame().getState() == State.GAME_OVER) {
            this.setVisible(true);
            this.setInstructionPointer(0);
        }
    }

}
