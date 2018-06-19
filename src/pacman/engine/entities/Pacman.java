package pacman.engine.entities;

import pacman.engine.KeyBoard;
import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Pacman extends PacManEntity {

    private int col;
    private int row;
    private int desiredDirection;
    private int direction;
    private long diedTime;

    public Pacman(final PacmanGame game) {
        super(game);
        final String[] pacmanFrameNames = new String[30];
        for (int d = 0; d < 4; d++) {
            for (int i = 0; i < 4; i++) {
                pacmanFrameNames[i + 4 * d] = "/resources/pacman_" + d + "_" + i + ".png";
            }
        }
        for (int i = 0; i < 14; i++) {
            pacmanFrameNames[16 + i] = "/resources/pacman_died_" + i + ".png";
        }
        loadFrames(pacmanFrameNames);
        placePacmanAtStartPosition();
        setBoundingBox(new Rectangle(getX(), getY(), 6, 6));
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
                case 2:
                    setDirection(0);
                    if (!moveToTargetPosition(250)) {
                        startTimer();
                        incrementEntityCounter();
                    }
                    break;
                case 3:
                    if (getElapsedTime() > 3000) {
                        incrementEntityCounter();
                    }
                    break;
                case 4:
                    setDirection(2);
                    if (!moveToTargetPosition(-100)) {
                        resetEntityCounter();
                    }
                    break;
            }
            updateAnimation();
        } else if (this.getGame().getState() == State.READY) {
            placePacmanAtStartPosition();
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

            switch (getEntityCounter()) {
                case 0:
                    double angle = Math.toRadians(this.desiredDirection * 90);
                    int dx = (int) Math.cos(angle);
                    int dy = (int) Math.sin(angle);
                    if (getGame().maze[getRow() + dy][getCol() + dx] == 0) {
                        setDirection(this.desiredDirection);
                    }
                    angle = Math.toRadians(getDirection() * 90);
                    dx = (int) Math.cos(angle);
                    dy = (int) Math.sin(angle);
                    if (getGame().maze[getRow() + dy][getCol() + dx] == -1) {
                        break;
                    }
                    setCol(getCol() + dx);
                    setRow(getRow() + dy);
                    setEntityCounter(1);
                case 1:
                    final int targetX = getCol() * 8 - 4 - 32;
                    final int targetY = (getRow() + 3) * 8 - 4;
                    final int difX = (targetX - getX());
                    final int difY = (targetY - getY());
                    setX(getX() + Integer.compare(difX, 0));
                    setY(getY() + Integer.compare(difY, 0));
                    if (difX == 0 && difY == 0) {
                        resetEntityCounter();
                        if (getCol() == 1) {
                            setCol(34);
                            setX(getCol() * 8 - 4 - 24);
                        } else if (getCol() == 34) {
                            setCol(1);
                            setX(getCol() * 8 - 4 - 24);
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
                        this.diedTime = System.currentTimeMillis();
                        incrementEntityCounter();
                    }
                    break;
                case 2:
                    final int frameIndex = 16 + (int) ((System.currentTimeMillis() - this.diedTime) * 0.0075);
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
        getBoundingBox().setLocation(getX() + 4, getY() + 4);
    }

    @Override
    public void showEntity() {
        this.setVisible(true);
    }

    @Override
    public void hideEntity() {
        this.setVisible(false);
    }

    int getDirection() {
        return this.direction;
    }

    int getRow() {
        return this.row;
    }

    int getCol() {
        return this.col;
    }

    void updatePosition() {
        setX(getCol() * 8 - 4 - 32 - 4);
        setY((getRow() + 3) * 8 - 4);
    }

    private void placePacmanAtStartPosition() {
        setCol(18);
        setRow(23);
        updatePosition();
        setFrame(getFrames()[0]);
        setDirection(this.desiredDirection = 0);
    }

    private boolean moveToTargetPosition(final int targetX) {
        final int sx = targetX - getX();
        final int sy = 200 - getY();
        final int vx = Math.abs(sx) < 1 ? Math.abs(sx) : 1;
        final int vy = Math.abs(sy) < 1 ? Math.abs(sy) : 1;
        final int idx = vx * (Integer.compare(sx, 0));
        final int idy = vy * (Integer.compare(sy, 0));
        setX(getX() + idx);
        setY(getY() + idy);
        return sx != 0 || sy != 0;
    }

    private void updateAnimation() {
        final int frameIndex = 4 * this.getDirection() + (int) (System.nanoTime() * 0.00000002) % 4;
        this.setFrame(this.getFrames()[frameIndex]);
    }

    private void setDirection(int direction) {
        this.direction = direction;
    }

    private void setRow(int row) {
        this.row = row;
    }

    private void setCol(int col) {
        this.col = col;
    }
}
