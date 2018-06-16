package br.ol.pacman.engine;


import br.ol.pacman.engine.PacmanGame.State;

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
        yield:
        while (true) {
            switch (this.instructionPointer) {
                case 0:
                    this.waitTime = System.currentTimeMillis();
                    this.instructionPointer = 1;
                case 1:
                    while (System.currentTimeMillis() - this.waitTime < 100) {
                        break yield;
                    }
                    this.textIndex++;
                    if (this.textIndex < this.text.length()) {
                        this.instructionPointer = 0;
                        break yield;
                    }
                    this.waitTime = System.currentTimeMillis();
                    this.instructionPointer = 2;
                case 2:
                    while (System.currentTimeMillis() - this.waitTime < 3000) {
                        break yield;
                    }
                    this.setVisible(false);
                    this.waitTime = System.currentTimeMillis();
                    this.instructionPointer = 3;
                case 3:
                    while (System.currentTimeMillis() - this.waitTime < 1500) {
                        break yield;
                    }
                    this.getGame().setState(State.TITLE);
                    break yield;
            }
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
