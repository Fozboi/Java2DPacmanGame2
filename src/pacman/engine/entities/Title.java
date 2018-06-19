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
        setX(21);
        setY(100);
    }

    @Override
    public void update() {
        if (getGame().getState() == State.TITLE) {
            setVisible(true);
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 500) {
                        incrementEntityCounter();
                    }
                    break;
                case 2:
                    final int dy = 100 - this.getY();
                    setY(this.getY() + dy);
                    if (Math.abs(dy) < 1) {
                        startTimer();
                        incrementEntityCounter();
                    }
                    break;
                case 3:
                    if (getElapsedTime() > 200) {
                        incrementEntityCounter();
                    }
                    break;
                case 4:
                    this.pushSpaceToStart = ((int) (System.nanoTime() * 0.0000000075) % 3) > 0;
                    if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_SPACE]) {
                        getGame().setState(State.READY);
                        setVisible(false);
                    }
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        super.draw(g);
        if (isVisible()) {
            if (this.pushSpaceToStart) {
                getGame().drawText(g, "PUSH SPACE TO START", 37, 170);
            }
            getGame().drawText(g, "PROGRAMMED BY AMIR AFGHANI", 5, 240);
            getGame().drawText(g, "ORIGINAL GAME BY NAMCO 1980", 5, 255);
        }
    }

    public void showEntity() {
        this.setVisible(true);
    }

}
