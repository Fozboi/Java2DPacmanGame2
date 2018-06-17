package pacman.engine;

import pacman.engine.PacmanGame.State;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Pacman extends PacManEntity {

    private int col;
    private int row;
    private int desiredDirection;
    private int direction;
    private long diedTime;

    Pacman(final PacmanGame game) {
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
        reset();
        this.setBoundingBox(new Rectangle(0, 0, 6, 6));
    }

    private void reset() {
        this.setCol(18);
        this.setRow(23);
        updatePosition();
        this.setFrame(this.getFrames()[0]);
        this.setDirection(this.desiredDirection = 0);
    }

    void updatePosition() {
        this.setX(this.getCol() * 8 - 4 - 32 - 4);
        this.setY((this.getRow() + 3) * 8 - 4);
    }

    private boolean moveToTargetPosition(final int targetX) {
        final int sx = (int) (targetX - this.getX());
        final int sy = (int) (200 - this.getY());
        final int vx = Math.abs(sx) < 1 ? Math.abs(sx) : 1;
        final int vy = Math.abs(sy) < 1 ? Math.abs(sy) : 1;
        final int idx = vx * (Integer.compare(sx, 0));
        final int idy = vy * (Integer.compare(sy, 0));
        this.setX(this.getX() + idx);
        this.setY(this.getY() + idy);
        return sx != 0 || sy != 0;
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.TITLE) {
            switch (this.getInstructionPointer()) {
                case 0:
                    this.setStartTime(System.currentTimeMillis());
                    this.setInstructionPointer(1);
                case 1:
                    if (System.currentTimeMillis() - this.getStartTime() < 3000) {
                        break;
                    }
                    this.setInstructionPointer(2);
                case 2:
                    this.setDirection(0);
                    if (!moveToTargetPosition(250)) {
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(3);
                    }
                    break;
                case 3:
                    if (System.currentTimeMillis() - this.getStartTime() < 3000) {
                        break;
                    }
                    this.setInstructionPointer(4);
                case 4:
                    this.setDirection(2);
                    if (!moveToTargetPosition(-100)) {
                        this.setInstructionPointer(0);
                    }
                    break;
            }
            updateAnimation();
        } else if (this.getGame().getState() == State.PLAYING) {
            if (!this.isVisible()) {
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

            switch (this.getInstructionPointer()) {
                case 0:
                    double angle = Math.toRadians(this.desiredDirection * 90);
                    int dx = (int) Math.cos(angle);
                    int dy = (int) Math.sin(angle);
                    if (this.getGame().maze[this.getRow() + dy][this.getCol() + dx] == 0) {
                        this.setDirection(this.desiredDirection);
                    }

                    angle = Math.toRadians(this.getDirection() * 90);
                    dx = (int) Math.cos(angle);
                    dy = (int) Math.sin(angle);
                    if (this.getGame().maze[this.getRow() + dy][this.getCol() + dx] == -1) {
                        break;
                    }

                    this.setCol(this.getCol() + dx);
                    this.setRow(this.getRow() + dy);
                    this.setInstructionPointer(1);
                case 1:
                    int targetX = this.getCol() * 8 - 4 - 32;
                    int targetY = (this.getRow() + 3) * 8 - 4;
                    int difX = (targetX - (int) this.getX());
                    int difY = (targetY - (int) this.getY());
                    this.setX(this.getX() + Integer.compare(difX, 0));
                    this.setY(this.getY() + Integer.compare(difY, 0));
                    if (difX == 0 && difY == 0) {
                        this.setInstructionPointer(0);
                        if (this.getCol() == 1) {
                            this.setCol(34);
                            this.setX(this.getCol() * 8 - 4 - 24);
                        } else if (this.getCol() == 34) {
                            this.setCol(1);
                            this.setX(this.getCol() * 8 - 4 - 24);
                        }
                    }
                    break;
            }
            updateAnimation();
            if (this.getGame().isLevelCleared()) {
                this.getGame().levelCleared();
            }
        } else if (this.getGame().getState() == State.PACMAN_DIED) {
            switch (this.getInstructionPointer()) {
                case 0:
                    this.setStartTime(System.currentTimeMillis());
                    this.setInstructionPointer(1);
                case 1:
                    if (System.currentTimeMillis() - this.getStartTime() < 2000) {
                        break;
                    }
                    this.diedTime = System.currentTimeMillis();
                    this.setInstructionPointer(2);
                case 2:
                    final int frameIndex = 16 + (int) ((System.currentTimeMillis() - this.diedTime) * 0.0075);
                    this.setFrame(this.getFrames()[frameIndex]);
                    if (frameIndex == 29) {
                        this.setStartTime(System.currentTimeMillis());
                        this.setInstructionPointer(3);
                    }
                    break;
                case 3:
                    if (System.currentTimeMillis() - this.getStartTime() < 1500) {
                        break;
                    }
                    this.setInstructionPointer(4);
                case 4:
                    this.getGame().nextLife();
                    break;
            }
        }
    }

    private void updateAnimation() {
        final int frameIndex = 4 * this.getDirection() + (int) (System.nanoTime() * 0.00000002) % 4;
        this.setFrame(this.getFrames()[frameIndex]);
    }

    @Override
    public void updateBoundingBox() {
        this.getBoundingBox().setLocation((int) (this.getX() + 4), (int) (this.getY() + 4));
    }

    @Override
    public void stateChanged() {
        if (this.getGame().getState() == PacmanGame.State.TITLE) {
            this.setX(-100);
            this.setY(200);
            this.setInstructionPointer(0);
            this.setVisible(true);
        } else if (this.getGame().getState() == State.READY) {
            this.setVisible(false);
        } else if (this.getGame().getState() == State.READY2) {
            reset();
        } else if (this.getGame().getState() == State.PLAYING) {
            this.setInstructionPointer(0);
        } else if (this.getGame().getState() == State.PACMAN_DIED) {
            this.setInstructionPointer(0);
        } else if (this.getGame().getState() == State.LEVEL_CLEARED) {
            this.setFrame(this.getFrames()[0]);
        }
    }

    public void showAll() {
        this.setVisible(true);
    }

    public void hideAll() {
        this.setVisible(false);
    }

    int getDirection() {
        return this.direction;
    }

    private void setDirection(int direction) {
        this.direction = direction;
    }

    int getRow() {
        return this.row;
    }

    private void setRow(int row) {
        this.row = row;
    }

    int getCol() {
        return this.col;
    }

    private void setCol(int col) {
        this.col = col;
    }
}
