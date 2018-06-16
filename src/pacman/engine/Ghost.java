package pacman.engine;

import pacman.engine.PacmanGame.State;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Ghost extends PacmanEntity {

    private final Pacman pacman;
    private final int type;
    private int col;
    private int row;
    private int cageUpDownCount;
    private Mode mode = Mode.CAGE;
    private int direction = 0;
    private int lastDirection;
    private final List<Integer> desiredDirections = new ArrayList<>();
    private int desiredDirection;
    private static final int[] backwardDirections = {2, 3, 0, 1};
    private long vulnerableModeStartTime;
    private boolean markAsVulnerable;
    private final ShortestPathFinder pathFinder;

    private static final Point[] INITIAL_POSITIONS = {
            new Point(18, 11), new Point(16, 14),
            new Point(18, 14), new Point(20, 14)
    };

    int getCol() {
        return this.col;
    }

    void setCol(final int col) {
        this.col = col;
    }

    int getRow() {
        return this.row;
    }

    void setRow(final int row) {
        this.row = row;
    }

    public enum Mode {
        CAGE,
        NORMAL,
        VULNERABLE,
        DIED
    }

    Ghost(final PacmanGame game,
          final Pacman pacman,
          final int type) {
        super(game);
        this.pacman = pacman;
        this.type = type;
        this.pathFinder = new ShortestPathFinder(game.maze);
        String[] ghostFrameNames = new String[8 + 4 + 4];
        for (int i = 0; i < 8; i++) {
            ghostFrameNames[i] = "/resources/ghost_" + this.type + "_" + i + ".png";
        }
        for (int i = 0; i < 4; i++) {
            ghostFrameNames[8 + i] = "/resources/ghost_vulnerable_" + i + ".png";
        }
        for (int i = 0; i < 4; i++) {
            ghostFrameNames[12 + i] = "/resources/ghost_died_" + i + ".png";
        }
        loadFrames(ghostFrameNames);
        this.boundingBox = new Rectangle(0, 0, 6, 6);
        setMode(Mode.CAGE);
    }

    private void setMode(final Mode mode) {
        this.mode = mode;
        modeChanged();
    }

    private int getTargetX(int col) {
        return col * 8 - 3 - 32;
    }

    private int getTargetY(int row) {
        return (row + 3) * 8 - 2;
    }

    void updatePosition() {
        this.x = getTargetX(this.getCol());
        this.y = getTargetY(this.getRow());
    }

    private void updatePosition(int col, int row) {
        this.setCol(col);
        this.setRow(row);
        updatePosition();
    }

    private boolean moveToTargetPosition(int targetX, int targetY, int velocity) {
        final int sx = (int) (targetX - this.x);
        final int sy = (int) (targetY - this.y);
        final int vx = Math.abs(sx) < velocity ? Math.abs(sx) : velocity;
        final int vy = Math.abs(sy) < velocity ? Math.abs(sy) : velocity;
        final int idx = vx * (Integer.compare(sx, 0));
        final int idy = vy * (Integer.compare(sy, 0));
        this.x += idx;
        this.y += idy;
        return sx != 0 || sy != 0;
    }

    private boolean moveToGridPosition(int col, int row, int velocity) {
        int targetX = getTargetX(col);
        int targetY = getTargetY(row);
        return moveToTargetPosition(targetX, targetY, velocity);
    }

    private void adjustHorizontalOutsideMovement() {
        if (this.getCol() == 1) {
            this.setCol(34);
            this.x = getTargetX(this.getCol());
        } else if (this.getCol() == 34) {
            this.setCol(1);
            this.x = getTargetX(this.getCol());
        }
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.TITLE) {
            int frameIndex = 0;
            this.x = this.pacman.x + 17 + 17 * this.type;
            this.y = 200;
            if (this.pacman.direction == 0) {
                frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
            } else if (this.pacman.direction == 2) {
                frameIndex = 2 * this.pacman.direction + (int) (System.nanoTime() * 0.00000001) % 2;
            }
            this.frame = this.frames[frameIndex];
        } else if(this.getGame().getState() == State.PACMAN_DIED) {
            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.startTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.startTime < 1500) {
                            break yield;
                        }
                        this.setVisible(false);
                        setMode(Mode.CAGE);
                        updateAnimation();
                        break yield;
                }
            }
            updateAnimation();
        } else if(this.getGame().getState() == State.GHOST_CAPTURED) {
            if (this.mode == Mode.DIED) {
                updateGhostDied();
                updateAnimation();
            }
        } else if(this.getGame().getState() == State.LEVEL_CLEARED) {
            yield:
            while (true) {
                switch (this.instructionPointer) {
                    case 0:
                        this.startTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.startTime < 1500) {
                            break yield;
                        }
                        this.setVisible(false);
                        setMode(Mode.CAGE);
                        updateAnimation();
                        this.instructionPointer = 2;
                    case 2:
                        break yield;
                }
            }
        } else if(this.getGame().getState() == State.PLAYING) {
            switch (this.mode) {
                case CAGE: updateGhostCage(); break;
                case NORMAL: updateGhostNormal(); break;
                case VULNERABLE: updateGhostVulnerable(); break;
                case DIED: updateGhostDied(); break;
            }
            updateAnimation();
        }
    }

    private void updateAnimation() {
        int frameIndex = 0;
        switch (this.mode) {
            case CAGE:
            case NORMAL:
                frameIndex = 2 * this.direction + (int) (System.nanoTime() * 0.00000001) % 2;
                if (!this.markAsVulnerable) {
                    break;
                }
            case VULNERABLE:
                if (System.currentTimeMillis() - this.vulnerableModeStartTime > 5000) {
                    frameIndex = 8 + (int) (System.nanoTime() * 0.00000002) % 4;
                } else {
                    frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
                }
                break;
            case DIED:
                frameIndex = 12 + this.direction;
                break;
        }
        this.frame = this.frames[frameIndex];
    }

    private void updateGhostCage() {
        yield:
        while (true) {
            switch (this.instructionPointer) {
                case 0:
                    Point initialPosition = INITIAL_POSITIONS[this.type];
                    updatePosition(initialPosition.x, initialPosition.y);
                    this.x -= 4;
                    this.cageUpDownCount = 0;
                    if (this.type == 0) {
                        this.instructionPointer = 6;
                        break;
                    } else if (this.type == 2) {
                        this.instructionPointer = 2;
                        break;
                    }
                    this.instructionPointer = 1;
                case 1:
                    if (moveToTargetPosition((int) this.x, 134 + 4, 1)) {
                        break yield;
                    }
                    this.instructionPointer = 2;
                case 2:
                    if (moveToTargetPosition((int) this.x, 134 - 4, 1)) {
                        break yield;
                    }
                    this.cageUpDownCount++;
                    if (this.cageUpDownCount <= this.type * 2) {
                        this.instructionPointer = 1;
                        break yield;
                    }
                    this.instructionPointer = 3;
                case 3:
                    if (moveToTargetPosition((int) this.x, 134, 1)) {
                        break yield;
                    }
                    this.instructionPointer = 4;
                case 4:
                    if (moveToTargetPosition(105, 134, 1)) {
                        break yield;
                    }
                    this.instructionPointer = 5;
                case 5:
                    if (moveToTargetPosition(105, 110, 1)) {
                        break yield;
                    }
                    if ((int) (2 * Math.random()) == 0) {
                        this.instructionPointer = 7;
                        continue yield;
                    }
                    this.instructionPointer = 6;
                case 6:
                    if (moveToTargetPosition(109, 110, 1)) {
                        break yield;
                    }
                    this.desiredDirection = 0;
                    this.lastDirection = 0;
                    updatePosition(18, 11);
                    this.instructionPointer = 8;
                    continue yield;
                case 7:
                    if (moveToTargetPosition(101, 110, 1)) {
                        break yield;
                    }
                    this.desiredDirection = 2;
                    this.lastDirection = 2;
                    updatePosition(17, 11);
                    this.instructionPointer = 8;
                case 8:
                    setMode(Mode.NORMAL);
                    break yield;
            }
        }
    }

    private final PacmanCatchedAction pacmanCatchedAction = new PacmanCatchedAction();

    private class PacmanCatchedAction implements Runnable {
        @Override
        public void run() {
            Ghost.this.getGame().setState(State.PACMAN_DIED);
        }
    }

    private void updateGhostNormal() {
        if (checkVulnerableModeTime() && this.markAsVulnerable) {
            setMode(Mode.VULNERABLE);
            this.markAsVulnerable = false;
        }

        if (this.type == 0 || this.type == 1) {
            updateGhostMovement(true, this.pacman.col, this.pacman.row, this.pacmanCatchedAction, 0, 1, 2, 3); // chase movement
        } else {
            updateGhostMovement(false, 0, 0, this.pacmanCatchedAction, 0, 1, 2, 3); // random movement
        }
    }

    private final GhostCatchedAction ghostCatchedAction = new GhostCatchedAction();

    private class GhostCatchedAction implements Runnable {
        @Override
        public void run() {
            Ghost.this.getGame().ghostCatched(Ghost.this);
        }
    }

    private void updateGhostVulnerable() {
        if (this.markAsVulnerable) {
            this.markAsVulnerable = false;
        }

        updateGhostMovement(true, this.pacman.col, this.pacman.row, this.ghostCatchedAction, 2, 3, 0, 1); // run away movement
        // return to normal mode after 8 seconds
        if (!checkVulnerableModeTime()) {
            setMode(Mode.NORMAL);
        }
    }

    private boolean checkVulnerableModeTime() {
        return System.currentTimeMillis() - this.vulnerableModeStartTime <= 8000;
    }

    private void updateGhostDied() {
        yield:
        while (true) {
            switch (this.instructionPointer) {
                case 0:
                    this.pathFinder.find(this.getCol(), this.getRow(), 18, 11);
                    this.instructionPointer = 1;
                case 1:
                    if (!this.pathFinder.hasNext()) {
                        this.instructionPointer = 3;
                        continue yield;
                    }
                    Point nextPosition = this.pathFinder.getNext();
                    this.setCol(nextPosition.x);
                    this.setRow(nextPosition.y);
                    this.instructionPointer = 2;
                case 2:
                    if (!moveToGridPosition(this.getCol(), this.getRow(), 4)) {
                        if (this.getRow() == 11 && (this.getCol() == 17 || this.getCol() == 18)) {
                            this.instructionPointer = 3;
                            continue yield;
                        }
                        this.instructionPointer = 1;
                        continue yield;
                    }
                    break yield;
                case 3:
                    if (!moveToTargetPosition(105, 110, 4)) {
                        this.instructionPointer = 4;
                        continue yield;
                    }
                    break yield;
                case 4:
                    if (!moveToTargetPosition(105, 134, 4)) {
                        this.instructionPointer = 5;
                        continue yield;
                    }
                    break yield;
                case 5:
                    setMode(Mode.CAGE);
                    this.instructionPointer = 4;
                    break yield;
            }
        }
    }

    private void updateGhostMovement(final boolean useTarget,
                                     final int targetCol,
                                     final int targetRow,
                                     final Runnable collisionWithPacmanAction,
                                     final int... desiredDirectionsMap) {

        this.desiredDirections.clear();
        if (useTarget) {
            if (targetCol - this.getCol() > 0) {
                this.desiredDirections.add(desiredDirectionsMap[0]);
            } else if (targetCol - this.getCol() < 0) {
                this.desiredDirections.add(desiredDirectionsMap[2]);
            }
            if (targetRow - this.getRow() > 0) {
                this.desiredDirections.add(desiredDirectionsMap[1]);
            } else if (targetRow - this.getRow() < 0) {
                this.desiredDirections.add(desiredDirectionsMap[3]);
            }
        }
        if (this.desiredDirections.size() > 0) {
            int selectedChaseDirection = (int) (this.desiredDirections.size() * Math.random());
            this.desiredDirection = this.desiredDirections.get(selectedChaseDirection);
        }

        yield:
        while (true) {
            switch (this.instructionPointer) {
                case 0:
                    if ((this.getRow() == 14 && this.getCol() == 1 && this.lastDirection == 2)
                            || (this.getRow() == 14 && this.getCol() == 34 && this.lastDirection == 0)) {
                        adjustHorizontalOutsideMovement();
                    }

                    double angle = Math.toRadians(this.desiredDirection * 90);
                    int dx = (int) Math.cos(angle);
                    int dy = (int) Math.sin(angle);
                    if (useTarget && this.getGame().maze[this.getRow() + dy][this.getCol() + dx] == 0
                            && this.desiredDirection != backwardDirections[this.lastDirection]) {

                        this.direction = this.desiredDirection;
                    } else {
                        do {
                            this.direction = (int) (4 * Math.random());
                            angle = Math.toRadians(this.direction * 90);
                            dx = (int) Math.cos(angle);
                            dy = (int) Math.sin(angle);
                        }
                        while (this.getGame().maze[this.getRow() + dy][this.getCol() + dx] == -1
                                || this.direction == backwardDirections[this.lastDirection]);
                    }

                    this.setCol(this.getCol() + dx);
                    this.setRow(this.getRow() + dy);
                    this.instructionPointer = 1;
                case 1:
                    if (!moveToGridPosition(this.getCol(), this.getRow(), 1)) {
                        this.lastDirection = this.direction;
                        this.instructionPointer = 0;
                        // adjustHorizontalOutsideMovement();
                    }
                    if (collisionWithPacmanAction != null && checkCollisionWithPacman()) {
                        collisionWithPacmanAction.run();
                    }
                    break yield;
            }
        }
    }

    private boolean checkCollisionWithPacman() {
        this.pacman.updateBoundingBox();
        updateBoundingBox();
        return this.pacman.boundingBox.intersects(this.boundingBox);
    }

    @Override
    public void updateBoundingBox() {
        this.boundingBox.setLocation((int) (this.x + 4), (int) (this.y + 4));
    }

    private void modeChanged() {
        this.instructionPointer = 0;
    }

    @Override
    public void stateChanged() {
        if (this.getGame().getState() == PacmanGame.State.TITLE) {
            update();
            //updateTitle();
            this.setVisible(true);
        } else if (this.getGame().getState() == PacmanGame.State.READY) {
            this.setVisible(false);
        } else if (this.getGame().getState() == PacmanGame.State.READY2) {
            setMode(Mode.CAGE);
            updateAnimation();
            Point initialPosition = INITIAL_POSITIONS[this.type];
            updatePosition(initialPosition.x, initialPosition.y); // col, row
            this.x -= 4;
        } else if (this.getGame().getState() == PacmanGame.State.PLAYING && this.mode != Mode.CAGE) {
            this.instructionPointer = 0;
        } else if (this.getGame().getState() == PacmanGame.State.PACMAN_DIED) {
            this.instructionPointer = 0;
        } else if (this.getGame().getState() == PacmanGame.State.LEVEL_CLEARED) {
            this.instructionPointer = 0;
        }
    }

    public void showAll() {
        this.setVisible(true);
    }

    public void hideAll() {
        this.setVisible(false);
    }

    void startGhostVulnerableMode() {
        this.vulnerableModeStartTime = System.currentTimeMillis();
        this.markAsVulnerable = true;
    }

    void died() {
        setMode(Mode.DIED);
    }

}
