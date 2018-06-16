package pacman.engine;


import pacman.engine.PacmanGame.State;

import java.awt.*;

public class OLPresents extends PacmanEntity {

    private final String text = "O.L. PRESENTS";
    private int textIndex;

    OLPresents(final PacmanGame game) {
        super(game);
    }

    @Override
    public void update() {
        if (this.getGame().getState() != State.OL_PRESENTS) {
            return;
        }
        switch (this.instructionPointer) {
            case 0:
                this.waitTime = System.currentTimeMillis();
                this.instructionPointer = 1;
            case 1:
                if (System.currentTimeMillis() - this.waitTime < 100) {
                    break;
                }
                this.textIndex++;
                if (this.textIndex < this.text.length()) {
                    this.instructionPointer = 0;
                    break;
                }
                this.waitTime = System.currentTimeMillis();
                this.instructionPointer = 2;
            case 2:
                if (System.currentTimeMillis() - this.waitTime < 3000) {
                    break;
                }
                this.setVisible(false);
                this.waitTime = System.currentTimeMillis();
                this.instructionPointer = 3;
            case 3:
                if (System.currentTimeMillis() - this.waitTime < 1500) {
                    break;
                }
                this.getGame().setState(State.TITLE);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (this.isVisible()) {
            this.getGame().drawText(g, this.text.substring(0, this.textIndex), 60, 130);
        }
    }

    @Override
    public void stateChanged() {
        this.setVisible(false);
        if (this.getGame().getState() == State.OL_PRESENTS) {
            this.setVisible(true);
            this.textIndex = 0;
        }
    }

    public void showAll() {
        this.setVisible(true);
    }

}
