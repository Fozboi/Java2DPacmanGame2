package pacman.engine.entities;


import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

import java.awt.*;

public class PresentBanner extends PacManEntity {

    private final String text = "Presenting ...";
    private int textIndex;

    public PresentBanner(final PacmanGame game) {
        super(game);
    }

    @Override
    public void update() {
        if (this.getGame().getState() != State.OL_PRESENTS) {
            return;
        }
        switch (this.getInstructionPointer()) {
            case 0:
                this.setStartTime(System.currentTimeMillis());
                this.setInstructionPointer(1);
                break;
            case 1:
                if (System.currentTimeMillis() - this.getStartTime() < 100) {
                    break;
                }
                this.textIndex++;
                if (this.textIndex < this.text.length()) {
                    this.setInstructionPointer(0);
                    break;
                }
                this.setStartTime(System.currentTimeMillis());
                this.setInstructionPointer(2);
                break;
            case 2:
                if (System.currentTimeMillis() - this.getStartTime() < 3000) {
                    break;
                }
                this.setVisible(false);
                this.setStartTime(System.currentTimeMillis());
                this.setInstructionPointer(3);
                break;
            case 3:
                if (System.currentTimeMillis() - this.getStartTime() < 1500) {
                    break;
                }
                this.getGame().setState(State.TITLE);
                break;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
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
