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
        if(this.getGame().getState() == State.LEVEL_CLEARED) {
                switch (this.getInstructionPointer()) {
                    case 0:
                        this.frameCount = 0;
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(1);
                        break;
                    case 1:
                        if (System.currentTimeMillis() - this.getStartTime() < 1500) {
                            return;
                        }
                        this.setInstructionPointer(2);
                        break;
                    case 2:
                        this.setFrame(this.getFrames()[1]);
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(3);
                    case 3:
                        if (System.currentTimeMillis() - this.getStartTime() < 200) {
                            return;
                        }
                        this.setFrame(this.getFrames()[0]);
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(4);
                        break;
                    case 4:
                        if (System.currentTimeMillis() - this.getStartTime() < 200) {
                            return;
                        }
                        this.frameCount++;
                        if (this.frameCount < 5) {
                            this.setInstructionPointer(2);
                            return;
                        }
                        this.getGame().hideAll();
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(5);
                        break;
                    case 5:
                        if (System.currentTimeMillis() - this.getStartTime() < 500) {
                            return;
                        }
                        this.setVisible(true);
                        this.getGame().nextLevel();
                        break;
                }
        }
    }

    @Override
    public void stateChanged() {
        if (this.getGame().getState() == State.TITLE) {
            this.setVisible(false);
        }
        else if (this.getGame().getState() == State.READY) {
            this.setVisible(true);
        }
        else if (this.getGame().getState() == State.LEVEL_CLEARED) {
            this.setInstructionPointer(0);
        }
    }

    public void hideAll() {
        this.setVisible(false);
    }

    public void showAll() {
        this.setVisible(true);
    }

}
