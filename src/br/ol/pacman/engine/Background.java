package br.ol.pacman.engine;

import br.ol.pacman.engine.PacmanGame.State;
import java.awt.Color;
import java.awt.Graphics2D;

class Background extends PacmanEntity {
    
    private final boolean showBlockedCellColor = false;
    private final Color blockedCellColor = new Color(255, 0, 0, 128);
    private int frameCount;
    
    Background(final PacmanGame game) {
        super(game);
        loadFrames("/res/background_0.png", "/res/background_1.png");
    }

    @Override
    public void update() {
        if(this.getGame().getState() == State.LEVEL_CLEARED) {
            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.frameCount = 0;
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.waitTime < 1500) {
                            break yield;
                        }
                        this.instructionPointer = 2;
                    case 2:
                        this.frame = this.frames[1];
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 3;
                    case 3:
                        if (System.currentTimeMillis() - this.waitTime < 200) {
                            break yield;
                        }
                        this.frame = this.frames[0];
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 4;
                    case 4:
                        if (System.currentTimeMillis() - this.waitTime < 200) {
                            break yield;
                        }
                        this.frameCount++;
                        if (this.frameCount < 5) {
                            this.instructionPointer = 2;
                            continue yield;
                        }
                        this.getGame().broadcastMessage("hideAll");
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 5;
                    case 5:
                        if (System.currentTimeMillis() - this.waitTime < 500) {
                            break yield;
                        }
                        this.setVisible(true);
                        this.getGame().nextLevel();
                        break yield;
                }
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        super.draw(g);
        if (this.showBlockedCellColor) {
            g.setColor(this.blockedCellColor);
            for (int row=0; row<31; row++) {
                for (int col=0; col<36; col++) {
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
            this.instructionPointer = 0;
        }
    }

    public void hideAll() {
        this.setVisible(false);
    }

    public void showAll() {
        this.setVisible(true);
    }
    
}
