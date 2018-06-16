package br.ol.pacman.engine;


import br.ol.pacman.engine.PacmanGame.State;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Title extends PacmanEntity {
    
    private boolean pushSpaceToStartVisible;

    Title(final PacmanGame game) {
        super(game);
        loadFrames("/res/title.png");
        this.x = 21;
        this.y = 100;
    }

    @Override
    public void update() {
        if(this.getGame().getState() == State.TITLE) {
            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.waitTime < 500) {
                            break yield;
                        }
                        this.instructionPointer = 2;
                    case 2:
                        double dy = 100 - this.y;
                        this.y = this.y + dy * 0.1;
                        if (Math.abs(dy) < 1) {
                            this.waitTime = System.currentTimeMillis();
                            this.instructionPointer = 3;
                        }
                        break yield;
                    case 3:
                        if (System.currentTimeMillis() - this.waitTime < 200) {
                            break yield;
                        }
                        this.instructionPointer = 4;
                    case 4:
                        this.pushSpaceToStartVisible = ((int) (System.nanoTime() * 0.0000000075) % 3) > 0;
                        if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_SPACE]) {
                            this.getGame().startGame();
                        }
                        break yield;
                }
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (!this.isVisible()) {
            return;
        }
        super.draw(g);
        if (this.pushSpaceToStartVisible) {
            this.getGame().drawText(g, "PUSH SPACE TO START", 37, 170);
        }
        this.getGame().drawText(g, "PROGRAMMED BY O.L. 2017", 20, 240);
        this.getGame().drawText(g, "ORIGINAL GAME BY NAMCO 1980", 5, 255);
    }

    @Override
    public void stateChanged() {
        this.setVisible(false);
        if (this.getGame().getState() == State.TITLE) {
            this.y = -150;
            this.setVisible(true);
            this.pushSpaceToStartVisible = false;
            this.instructionPointer = 0;
        }
    }

    public void showAll() {
        this.setVisible(true);
    }
        
}
