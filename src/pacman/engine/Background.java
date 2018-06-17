package pacman.engine;

import pacman.engine.PacmanGame.State;
import java.awt.Color;
import java.awt.Graphics2D;

class Background extends PacManEntity {

    private final boolean showBlockedCellColor = true;
    private final Color blockedCellColor = new Color(255, 0, 0, 128);
    private int frameCount;

    Background(final PacmanGame game) {
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
    public void draw(final Graphics2D g) {
        super.draw(g);
        if (this.showBlockedCellColor) {
            g.setColor(this.blockedCellColor);
            for (int row = 0; row < 31; row++) {
                for (int col = 0; col < 36; col++) {
                    if (this.getGame().maze[row][col] == 1) {
                        g.fillRect(col * 8 - 32, (row + 3) * 8, 8, 8);
                    }
                }
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
