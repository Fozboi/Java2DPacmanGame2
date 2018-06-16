package pacman.engine;


import pacman.engine.PacmanGame.State;

public class GameOver extends PacmanEntity {

    GameOver(final PacmanGame game) {
        super(game);
        this.x = 77;
        this.y = 160;
        loadFrames("/resources/gameover.png");
    }

    @Override
    public void update() {
        System.out.println("visible = " +this.isVisible());
        if (this.getGame().getState() == State.GAME_OVER) {
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.waitTime < 3000) {
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
            this.instructionPointer = 0;
        }
    }

    public void showAll() {
    }

}
