package pacman.engine.entities;

import pacman.engine.KeyBoard;
import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Title extends PacManEntity {

    private boolean pushSpaceToStart;

    public Title(final PacmanGame game) {
        super(game);
        loadFrames("/resources/title.png");
        this.setX(21);
        this.setY(100);
    }

    @Override
    public void update() {
        if(this.getGame().getState() == State.TITLE) {
                switch (this.getInstructionPointer()) {
                    case 0:
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(1);
                    case 1:
                        if (System.currentTimeMillis() - this.getStartTime() < 500) {
                            break;
                        }
                        this.setInstructionPointer(2);
                    case 2:
                        int dy = 100 - this.getY();
                        this.setY(this.getY() + dy);
                        if (Math.abs(dy) < 1) {
                            this.setStartTime(System.currentTimeMillis());
                            this.setInstructionPointer(3);
                        }
                        break;
                    case 3:
                        if (System.currentTimeMillis() - this.getStartTime() < 200) {
                            break;
                        }
                        this.setInstructionPointer(4);
                    case 4:
                        this.pushSpaceToStart = ((int) (System.nanoTime() * 0.0000000075) % 3) > 0;
                        if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_SPACE]) {
                            this.getGame().startGame();
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
        if (this.pushSpaceToStart) {
            this.getGame().drawText(g, "PUSH SPACE TO START", 37, 170);
        }
        this.getGame().drawText(g, "PROGRAMMED BY O.L. 2017", 20, 240);
        this.getGame().drawText(g, "ORIGINAL GAME BY NAMCO 1980", 5, 255);
    }

    @Override
    public void stateChanged() {
        this.setVisible(false);
        if (this.getGame().getState() == State.TITLE) {
            this.setY(-150);
            this.setVisible(true);
            this.pushSpaceToStart = false;
            this.setInstructionPointer(0);
        }
    }

    public void showAll() {
        this.setVisible(true);
    }

}
