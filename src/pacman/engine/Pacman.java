package pacman.engine;

import pacman.engine.PacmanGame.State;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Pacman extends PacmanEntity {

    int col;
    int row;
    private int desiredDirection;
    int direction;
    private int dx;
    private int dy;
    private long diedTime;

    Pacman(final PacmanGame game) {
        super(game);
        final String[] pacmanFrameNames = new String[30];
        for (int d = 0; d < 4; d++) {
            for (int i = 0; i < 4; i++) {
                pacmanFrameNames[i + 4 * d] = "/resources/pacman_" + d + "_" + i + ".png";
            }
        }
        for (int i=0; i<14; i++) {
            pacmanFrameNames[16 + i] = "/resources/pacman_died_" + i + ".png";
        }
        loadFrames(pacmanFrameNames);
        reset();
        this.boundingBox = new Rectangle(0, 0, 6, 6);
    }

    private void reset() {
        this.col = 18;
        this.row = 23;
        updatePosition();
        this.frame = this.frames[0];
        this.direction = this.desiredDirection = 0;
    }

    void updatePosition() {
        this.x = this.col * 8 - 4 - 32 - 4;
        this.y = (this.row + 3) * 8 - 4;
    }

    private boolean moveToTargetPosition(final int targetX,
                                         final int targetY,
                                         final int velocity) {
        int sx = (int) (targetX - this.x);
        int sy = (int) (targetY - this.y);
        int vx = Math.abs(sx) < velocity ? Math.abs(sx) : velocity;
        int vy = Math.abs(sy) < velocity ? Math.abs(sy) : velocity;
        int idx = vx * (Integer.compare(sx, 0));
        int idy = vy * (Integer.compare(sy, 0));
        this.x += idx;
        this.y += idy;
        return sx != 0 || sy != 0;
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
                        if (System.currentTimeMillis() - this.waitTime < 3000) {
                            break yield;
                        }
                        this.instructionPointer = 2;
                    case 2:
                        this.direction = 0;
                        if (!moveToTargetPosition(250, 200, 1)) {
                            this.waitTime = System.currentTimeMillis();
                            this.instructionPointer = 3;
                        }
                        break yield;
                    case 3:
                        if (System.currentTimeMillis() - this.waitTime < 3000) {
                            break yield;
                        }
                        this.instructionPointer = 4;
                    case 4:
                        this.direction = 2;
                        if (!moveToTargetPosition(-100, 200, 1)) {
                            this.instructionPointer = 0;
                        }
                        break yield;
                }
            }
            updateAnimation();
        } else if(this.getGame().getState() == State.PLAYING) {
            if (!this.isVisible()) {
                return;
            }

            if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_LEFT]) {
                this.desiredDirection = 2;
            }
            else if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_RIGHT]) {
                this.desiredDirection = 0;
            }
            else if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_UP]) {
                this.desiredDirection = 3;
            }
            else if (KeyBoard.get().getKeyPressed()[KeyEvent.VK_DOWN]) {
                this.desiredDirection = 1;
            }

            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        double angle = Math.toRadians(this.desiredDirection * 90);
                        this.dx = (int) Math.cos(angle);
                        this.dy = (int) Math.sin(angle);
                        if (this.getGame().maze[this.row + this.dy][this.col + this.dx] == 0) {
                            this.direction = this.desiredDirection;
                        }

                        angle = Math.toRadians(this.direction * 90);
                        this.dx = (int) Math.cos(angle);
                        this.dy = (int) Math.sin(angle);
                        if (this.getGame().maze[this.row + this.dy][this.col + this.dx] == -1) {
                            break yield;
                        }

                        this.col += this.dx;
                        this.row += this.dy;
                        this.instructionPointer = 1;
                    case 1:
                        int targetX = this.col * 8 - 4 - 32;
                        int targetY = (this.row + 3) * 8 - 4;
                        int difX = (targetX - (int) this.x);
                        int difY = (targetY - (int) this.y);
                        this.x += Integer.compare(difX, 0);
                        this.y += Integer.compare(difY, 0);
                        if (difX == 0 && difY == 0) {
                            this.instructionPointer = 0;
                            if (this.col == 1) {
                                this.col = 34;
                                this.x = this.col * 8 - 4 - 24;
                            }
                            else if (this.col == 34) {
                                this.col = 1;
                                this.x = this.col * 8 - 4 - 24;
                            }
                        }
                        break yield;
                }
            }
            updateAnimation();
            if (this.getGame().isLevelCleared()) {
                this.getGame().levelCleared();
            }
        } else if(this.getGame().getState() == State.PACMAN_DIED) {
            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.waitTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.waitTime < 2000) {
                            break yield;
                        }
                        this.diedTime = System.currentTimeMillis();
                        this.instructionPointer = 2;
                    case 2:
                        final int frameIndex = 16 + (int) ((System.currentTimeMillis() - this.diedTime) * 0.0075);
                        this.frame = this.frames[frameIndex];
                        if (frameIndex == 29) {
                            this.waitTime = System.currentTimeMillis();
                            this.instructionPointer = 3;
                        }
                        break yield;
                    case 3:
                        if (System.currentTimeMillis() - this.waitTime < 1500) {
                            break yield;
                        }
                        this.instructionPointer = 4;
                    case 4:
                        this.getGame().nextLife();
                        break yield;
                }
            }
        }
    }

    private void updateAnimation() {
        final int frameIndex = 4 * this.direction + (int) (System.nanoTime() * 0.00000002) % 4;
        this.frame = this.frames[frameIndex];
    }

    @Override
    public void updateBoundingBox() {
        this.boundingBox.setLocation((int) (this.x + 4), (int) (this.y + 4));
    }

    @Override
    public void stateChanged() {
        if (this.getGame().getState() == PacmanGame.State.TITLE) {
            this.x = -100;
            this.y = 200;
            this.instructionPointer = 0;
            this.setVisible(true);
        }
        else if (this.getGame().getState() == State.READY) {
            this.setVisible(false);
        }
        else if (this.getGame().getState() == State.READY2) {
            reset();
        }
        else if (this.getGame().getState() == State.PLAYING) {
            this.instructionPointer = 0;
        }
        else if (this.getGame().getState() == State.PACMAN_DIED) {
            this.instructionPointer = 0;
        }
        else if (this.getGame().getState() == State.LEVEL_CLEARED) {
            this.frame = this.frames[0];
        }
    }

    public void showAll() {
        this.setVisible(true);
    }

    public void hideAll() {
        this.setVisible(false);
    }

}
