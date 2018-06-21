package pacman.engine.entities;

import pacman.engine.KeyBoard;
import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;
import pacman.engine.SoundUtils;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Pacman extends PacManEntity {

    private int row;
    private int column;
    private int desiredDirection;
    private int currentDirection;
    private long timeOfDeath;

    public Pacman(final PacmanGame game) {
        super(game);
        loadFrames(initPacFrames());
        placePacmanAtStartPosition();
        setBoundingBox(new Rectangle(getxPosition(), getyPosition(), 6, 6));
    }

    @Override
    public void update() {
        if (getGame().getState() == State.TITLE) {
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 3000) {
                        incrementEntityCounter();
                    }
                    break;
            }
        } else if (this.getGame().getState() == State.READY) {
            placePacmanAtStartPosition();
            setVisible(true);
        } else if (this.getGame().getState() == State.PLAYING) {
            if (!isVisible()) {
                return;
            }

            if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_LEFT]) {
                this.desiredDirection = 2;
            } else if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_RIGHT]) {
                this.desiredDirection = 0;
            } else if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_UP]) {
                this.desiredDirection = 3;
            } else if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_DOWN]) {
                this.desiredDirection = 1;
            }

            System.out.println("desiredDirection = " +this.desiredDirection);

            switch (getEntityCounter()) {
                case 0:
                    double angle = Math.toRadians(this.desiredDirection * 90);
                    int dx = (int) Math.cos(angle);
                    int dy = (int) Math.sin(angle);
                    if (getGame().getGameMaze()[getRow() + dy][getColumn() + dx] == 0) {
                        setCurrentDirection(this.desiredDirection);
                    }
                    angle = Math.toRadians(getCurrentDirection() * 90);
                    dx = (int) Math.cos(angle);
                    dy = (int) Math.sin(angle);
                    if (getGame().getGameMaze()[getRow() + dy][getColumn() + dx] == -1) {
                        break;
                    }
                    setColumn(getColumn() + dx);
                    setRow(getRow() + dy);
                    incrementEntityCounter();
                case 1:
                    final int targetX = getColumn() * 8 - 4 - 32;
                    final int targetY = (getRow() + 3) * 8 - 4;
                    final int difX = (targetX - getxPosition());
                    final int difY = (targetY - getyPosition());
                    setxPosition(getxPosition() + Integer.compare(difX, 0));
                    setyPosition(getyPosition() + Integer.compare(difY, 0));
                    if (difX == 0 && difY == 0) {
                        resetEntityCounter();
                        if (getColumn() == 1) {
                            setColumn(34);
                            setxPosition(getColumn() * 8 - 4 - 24);
                        } else if (getColumn() == 34) {
                            setColumn(1);
                            setxPosition(getColumn() * 8 - 4 - 24);
                        }
                    }
                    break;
            }
            updateAnimation();
            if (getGame().isLevelCleared()) {
                getGame().levelCleared();
            }
        } else if (this.getGame().getState() == State.PACMAN_DIED) {
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 2000) {
                        this.timeOfDeath = System.currentTimeMillis();
                        incrementEntityCounter();
                        SoundUtils.playSoundStream("pacman_death.wav");
                    }
                    break;
                case 2:
                    final int frameIndex = 16 + (int) ((System.currentTimeMillis() - this.timeOfDeath) * 0.0075);
                    setFrame(getFrames()[frameIndex]);
                    if (frameIndex == 29) {
                        startTimer();
                        incrementEntityCounter();
                    }
                    break;
                case 3:
                    if (getElapsedTime() > 1500) {
                        incrementEntityCounter();
                    }
                    break;
                case 4:
                    getGame().nextLife();
                    resetEntityCounter();
                    break;
            }
        } else if (this.getGame().getState() == State.LEVEL_CLEARED) {
            setFrame(getFrames()[0]);
        }
        getBoundingBox().setLocation(getxPosition() + 4, getyPosition() + 4);
    }

    int getCurrentDirection() {
        return this.currentDirection;
    }

    int getRow() {
        return this.row;
    }

    int getColumn() {
        return this.column;
    }

    void updatePosition() {
        setxPosition(getColumn() * 8 - 4 - 32 - 4);
        setyPosition((getRow() + 3) * 8 - 4);
    }

    private void placePacmanAtStartPosition() {
        setRow(23);
        setColumn(18);
        updatePosition();
        setFrame(getFrames()[0]);
        this.desiredDirection = 0;
        this.currentDirection = 0;
    }

    private void updateAnimation() {
        final int frameIndex = 4 * this.getCurrentDirection() + (int) (System.nanoTime() * 0.00000002) % 4;
        setFrame(this.getFrames()[frameIndex]);
    }

    private void setCurrentDirection(int currentDirection) {
        this.currentDirection = currentDirection;
    }

    private void setRow(int row) {
        this.row = row;
    }

    private void setColumn(int column) {
        this.column = column;
    }

    private String[] initPacFrames() {
        final String[] pacFrames = new String[30];
        for (int d = 0; d < 4; d++) {
            for (int i = 0; i < 4; i++) {
                pacFrames[i + 4 * d] = "pacman_" + d + "_" + i + ".png";
            }
        }
        for (int i = 0; i < 14; i++) {
            pacFrames[16 + i] = "pacman_died_" + i + ".png";
        }
        return pacFrames;
    }
}
