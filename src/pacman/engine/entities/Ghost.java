package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ghost extends PacManEntity {

    private final Pacman pacman;
    private final int type;
    private int col;
    private int row;
    private int cageUpDownCount;
    private Mode mode;
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

    public Ghost(final PacmanGame game,
                 final Pacman pacman,
                 final int type) {
        super(game);
        this.pacman = pacman;
        this.type = type;
        this.pathFinder = new ShortestPathFinder(game.getGameMaze());
        loadFrames(initGhostFrames());
        setBoundingBox(new Rectangle(this.getxPosition(), this.getyPosition(), 6, 6));
        setMode(Mode.CAGE);
        final Point initialPosition = INITIAL_POSITIONS[type];
        updatePosition(initialPosition.x, initialPosition.y);
    }

    @Override
    public void update() {
        if (getGame().getState() == State.TITLE) {
            setVisible(false);
        } else if (getGame().getState() == PacmanGame.State.READY) {
            setMode(Mode.CAGE);
            setVisible(true);
        } else if (getGame().getState() == State.PACMAN_DIED) {
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 1500) {
                        setVisible(false);
                        setMode(Mode.CAGE);
                        updateAnimation();
                    }
                    break;
            }
            updateAnimation();
        } else if (getGame().getState() == State.GHOST_CAPTURED) {
            if (this.mode == Mode.DIED) {
                updateGhostDied();
                updateAnimation();
            }
        } else if (getGame().getState() == State.LEVEL_CLEARED) {
            switch (getEntityCounter()) {
                case 0:
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 1500) {
                        setVisible(false);
                        setMode(Mode.CAGE);
                        updateAnimation();
                        incrementEntityCounter();
                    }
                    break;
                case 2:
                    break;
            }
        } else if (getGame().getState() == State.PLAYING) {
            switch (this.mode) {
                case CAGE:
                    updateGhostCage();
                    break;
                case NORMAL:
                    updateGhostNormal();
                    break;
                case VULNERABLE:
                    updateGhostVulnerable();
                    break;
                case DIED:
                    updateGhostDied();
                    break;
            }
            updateAnimation();
        } else if (getGame().getState() == PacmanGame.State.LEVEL_CLEARED) {
            resetEntityCounter();
        }
        getBoundingBox().setLocation(this.getxPosition() + 4, this.getyPosition() + 4);
    }

    public void startGhostVulnerableMode() {
        this.vulnerableModeStartTime = System.currentTimeMillis();
        this.markAsVulnerable = true;
    }

    int getColumn() {
        return this.col;
    }

    int getRow() {
        return this.row;
    }

    void updatePosition() {
        setxPosition(getTargetX(getColumn()));
        setyPosition(getTargetY(getRow()));
    }

    void died() {
        setMode(Mode.DIED);
    }

    private void setColumn(final int col) {
        this.col = col;
    }

    private void setRow(final int row) {
        this.row = row;
    }

    private enum Mode {
        CAGE,
        NORMAL,
        VULNERABLE,
        DIED
    }

    private int getTargetX(final int col) {
        return col * 8 - 3 - 32;
    }

    private int getTargetY(final int row) {
        return (row + 3) * 8 - 2;
    }

    private void setMode(final Mode mode) {
        this.mode = mode;
        resetEntityCounter();
    }

    private void updatePosition(final int col,
                                final int row) {
        setColumn(col);
        setRow(row);
        updatePosition();
    }

    private boolean moveToTargetPosition(final int targetX,
                                         final int targetY,
                                         final int velocity) {
        final int sx = targetX - getxPosition();
        final int sy = targetY - getyPosition();
        final int vx = Math.abs(sx) < velocity ? Math.abs(sx) : velocity;
        final int vy = Math.abs(sy) < velocity ? Math.abs(sy) : velocity;
        final int idx = vx * (Integer.compare(sx, 0));
        final int idy = vy * (Integer.compare(sy, 0));
        setxPosition(getxPosition() + idx);
        setyPosition(getyPosition() + idy);
        return sx != 0 || sy != 0;
    }

    private boolean moveToGridPosition(final int col,
                                       final int row,
                                       final int velocity) {
        final int targetX = getTargetX(col);
        final int targetY = getTargetY(row);
        return moveToTargetPosition(targetX, targetY, velocity);
    }

    private void adjustHorizontalOutsideMovement() {
        if (getColumn() == 1) {
            setColumn(34);
            setxPosition(getTargetX(this.getColumn()));
        } else if (this.getColumn() == 34) {
            setColumn(1);
            setxPosition(getTargetX(this.getColumn()));
        }
    }

    private void updateAnimation() {
        int frameIndex = 0;
        switch (this.mode) {
            case CAGE:
            case NORMAL:
                frameIndex = 2 * this.direction + (int) (System.nanoTime() * 0.00000001) % 2;
                break;
            case VULNERABLE:
                if (elapsedVulnerableTime() > 5000) {
                    frameIndex = 8 + (int) (System.nanoTime() * 0.00000002) % 4;
                } else {
                    frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
                }
                break;
            case DIED:
                frameIndex = 12 + this.direction;
                break;
        }
        setFrame(getFrames()[frameIndex]);
    }

    private void updateGhostCage() {
        switch (getEntityCounter()) {
            case 0:
                final Point initialPosition = INITIAL_POSITIONS[this.type];
                updatePosition(initialPosition.x, initialPosition.y);
                setxPosition(getxPosition() - 4);
                this.cageUpDownCount = 0;
                if (this.type == 0) {
                    setEntityCounter(6);
                    break;
                } else if (this.type == 2) {
                    setEntityCounter(2);
                    break;
                }
                incrementEntityCounter();
                break;
            case 1:
                if (moveToTargetPosition(getxPosition(), 134 + 4, 1)) {
                    break;
                }
                incrementEntityCounter();
            case 2:
                if (moveToTargetPosition(getxPosition(), 134 - 4, 1)) {
                    break;
                }
                this.cageUpDownCount++;
                if (this.cageUpDownCount <= this.type * 2) {
                    setEntityCounter(1);
                    break;
                }
                incrementEntityCounter();
                break;
            case 3:
                if (moveToTargetPosition(this.getxPosition(), 134, 1)) {
                    break;
                }
                incrementEntityCounter();
                break;
            case 4:
                if (moveToTargetPosition(105, 134, 1)) {
                    break;
                }
                incrementEntityCounter();
                break;
            case 5:
                if (moveToTargetPosition(105, 110, 1)) {
                    break;
                }
                if ((int) (2 * Math.random()) == 0) {
                    setEntityCounter(7);
                    break;
                }
                this.setEntityCounter(6);
                break;
            case 6:
                if (moveToTargetPosition(109, 110, 1)) {
                    break;
                }
                this.desiredDirection = 0;
                this.lastDirection = 0;
                updatePosition(18, 11);
                setEntityCounter(8);
                break;
            case 7:
                if (moveToTargetPosition(101, 110, 1)) {
                    break;
                }
                this.desiredDirection = 2;
                this.lastDirection = 2;
                updatePosition(17, 11);
                setEntityCounter(8);
                break;
            case 8:
                setMode(Mode.NORMAL);
                break;
        }
    }

    private void updateGhostNormal() {
        if (elapsedVulnerableTime() <= 8000 && this.markAsVulnerable) {
            setMode(Mode.VULNERABLE);
            this.markAsVulnerable = false;
        }

        final EntityCapturedAction action = pacmanGame -> pacmanGame.setState(State.PACMAN_DIED);

        if (this.type == 0 || this.type == 1) {
            updateGhostMovement(true, this.pacman.getColumn(), this.pacman.getRow(), action, 0, 1, 2, 3);
        } else {
            updateGhostMovement(false, 0, 0, action, 0, 1, 2, 3);
        }
    }

    private void updateGhostVulnerable() {
        if (this.markAsVulnerable) {
            this.markAsVulnerable = false;
        }

        final EntityCapturedAction action = pacmanGame -> pacmanGame.ghostCaught(Ghost.this);

        updateGhostMovement(true, this.pacman.getColumn(), this.pacman.getRow(), action, 2, 3, 0, 1);
        // return to normal mode after 8 seconds
        if (elapsedVulnerableTime() > 8000) {
            setMode(Mode.NORMAL);
        }
    }

    private long elapsedVulnerableTime() {
        return System.currentTimeMillis() - this.vulnerableModeStartTime;
    }

    private void updateGhostDied() {
        switch (getEntityCounter()) {
            case 0:
                this.pathFinder.find(getColumn(), getRow());
                incrementEntityCounter();
                break;
            case 1:
                final boolean hasNext = this.pathFinder.hasNext();
                if (!hasNext) {
                    setEntityCounter(3);
                } else {
                    final Point nextPosition = this.pathFinder.getNext();
                    setColumn(nextPosition.x);
                    setRow(nextPosition.y);
                    incrementEntityCounter();
                }
                break;
            case 2:
                final boolean tryToMoveToGridPosition = moveToGridPosition(getColumn(), getRow(), 4);
                if (!tryToMoveToGridPosition) {
                    if (getRow() == 11 && (getColumn() == 17 || getColumn() == 18)) {
                        incrementEntityCounter();
                    } else {
                        setEntityCounter(1);
                    }
                }
                break;
            case 3:
                final boolean tryToMoveToTargetPosition2 = moveToTargetPosition(105, 110, 4);
                if (!tryToMoveToTargetPosition2) {
                    incrementEntityCounter();
                }
                break;
            case 4:
                final boolean tryToMoveToTargetPosition3 = moveToTargetPosition(105, 134, 4);
                if (!tryToMoveToTargetPosition3) {
                    incrementEntityCounter();
                }
                break;
            case 5:
                setMode(Mode.CAGE);
                setEntityCounter(4);
                break;
        }
    }

    private void updateGhostMovement(final boolean useTarget,
                                     final int targetCol,
                                     final int targetRow,
                                     final EntityCapturedAction capturedAction,
                                     final int... desiredDirectionsMap) {

        this.desiredDirections.clear();
        if (useTarget) {
            if (targetCol - this.getColumn() > 0) {
                this.desiredDirections.add(desiredDirectionsMap[0]);
            } else if (targetCol - this.getColumn() < 0) {
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

        switch (getEntityCounter()) {
            case 0:
                if ((getRow() == 14 && getColumn() == 1 && this.lastDirection == 2) ||
                        (getRow() == 14 && getColumn() == 34 && this.lastDirection == 0)) {
                    adjustHorizontalOutsideMovement();
                }

                double angle = Math.toRadians(this.desiredDirection * 90);
                int dx = (int) Math.cos(angle);
                int dy = (int) Math.sin(angle);
                if (useTarget && getGame().getGameMaze()[getRow() + dy][getColumn() + dx] == 0
                        && this.desiredDirection != backwardDirections[this.lastDirection]) {
                    this.direction = this.desiredDirection;
                } else {
                    do {
                        this.direction = (int) (4 * Math.random());
                        angle = Math.toRadians(this.direction * 90);
                        dx = (int) Math.cos(angle);
                        dy = (int) Math.sin(angle);
                    }
                    while (this.getGame().getGameMaze()[getRow() + dy][getColumn() + dx] == -1 ||
                            this.direction == backwardDirections[this.lastDirection]);
                }
                setColumn(getColumn() + dx);
                setRow(getRow() + dy);
                incrementEntityCounter();
                break;
            case 1:
                if (!moveToGridPosition(this.getColumn(), this.getRow(), 1)) {
                    this.lastDirection = this.direction;
                    resetEntityCounter();
                }
                if (checkCollisionWithPacman()) {
                    capturedAction.execute(getGame());
                }
                break;
        }
    }

    private boolean checkCollisionWithPacman() {
        return this.pacman.getBoundingBox().intersects(getBoundingBox());
    }

    private String[] initGhostFrames() {
        final String[] ghostFrameNames = new String[8 + 4 + 4];
        for (int i = 0; i < 8; i++) {
            ghostFrameNames[i] = "ghost_" + this.type + "_" + i + ".png";
        }
        for (int i = 0; i < 4; i++) {
            ghostFrameNames[8 + i] = "ghost_vulnerable_" + i + ".png";
        }
        for (int i = 0; i < 4; i++) {
            ghostFrameNames[12 + i] = "ghost_died_" + i + ".png";
        }
        return ghostFrameNames;
    }

}
