package pacman.engine.entities;

import pacman.engine.KeyBoard;
import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;
import pacman.engine.SoundUtils;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Title extends PacManEntity {

    private boolean pushSpaceToStart;

    public Title(final PacmanGame game) {
        super(game);
        loadFrames(new String[]{"title.png"});
        setXPosition(21);
        setYPosition(100);
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
                    final int dy = 100 - this.getYPosition();
                    setYPosition(this.getYPosition() + dy);
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
                    this.pushSpaceToStart = true;
                    if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_SPACE]) {
                        resetEntityCounter();
                        getGame().setState(State.READY);
                        setVisible(false);
                        SoundUtils.playSoundStream("pacman_beginning.wav");
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
        }
    }

}
