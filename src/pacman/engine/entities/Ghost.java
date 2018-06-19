package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;

import java.awt.Point;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
        this.pathFinder = new ShortestPathFinder(game.maze);
        final String[] ghostFrameNames = new String[8 + 4 + 4];
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
        setBoundingBox(new Rectangle(this.getX(), this.getY(), 6, 6));
        setMode(Mode.CAGE);
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.TITLE) {
            setVisible(true);
            int frameIndex = 0;
            setX(this.pacman.getX() + 17 + 17 * this.type);
            setY(200);
            if (this.pacman.getDirection() == 0) {
                frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
            } else if (this.pacman.getDirection() == 2) {
                frameIndex = 2 * this.pacman.getDirection() + (int) (System.nanoTime() * 0.00000001) % 2;
            }
            this.setFrame(this.getFrames()[frameIndex]);
        } else if (getGame().getState() == PacmanGame.State.READY) {
            setMode(Mode.CAGE);
            updateAnimation();
            final Point initialPosition = INITIAL_POSITIONS[this.type];
            updatePosition(initialPosition.x, initialPosition.y);
            setX(this.getX() - 4);
        } else if(this.getGame().getState() == State.PACMAN_DIED) {
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
        } else if(getGame().getState() == State.GHOST_CAPTURED) {
            if (this.mode == Mode.DIED) {
                updateGhostDied();
                updateAnimation();
            }
        } else if(getGame().getState() == State.LEVEL_CLEARED) {
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
        } else if(getGame().getState() == State.PLAYING) {
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
        this.getBoundingBox().setLocation(this.getX() + 4, this.getY() + 4);
    }

    int getColumn() {
        return this.col;
    }

    int getRow() {
        return this.row;
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
        modeChanged();
    }

    void updatePosition() {
        this.setX(getTargetX(this.getColumn()));
        this.setY(getTargetY(this.getRow()));
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
        final int sx = targetX - getX();
        final int sy = targetY - getY();
        final int vx = Math.abs(sx) < velocity ? Math.abs(sx) : velocity;
        final int vy = Math.abs(sy) < velocity ? Math.abs(sy) : velocity;
        final int idx = vx * (Integer.compare(sx, 0));
        final int idy = vy * (Integer.compare(sy, 0));
        this.setX(getX() + idx);
        this.setY(getY() + idy);
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
            setX(getTargetX(this.getColumn()));
        } else if (this.getColumn() == 34) {
            setColumn(1);
            setX(getTargetX(this.getColumn()));
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
        this.setFrame(this.getFrames()[frameIndex]);
    }

    private void updateGhostCage() {
        yield:
        while (true) {
            switch (this.getEntityCounter()) {
                case 0:
                    Point initialPosition = INITIAL_POSITIONS[this.type];
                    updatePosition(initialPosition.x, initialPosition.y);
                    this.setX(this.getX() - 4);
                    this.cageUpDownCount = 0;
                    if (this.type == 0) {
                        this.setEntityCounter(6);
                        break;
                    } else if (this.type == 2) {
                        this.setEntityCounter(2);
                        break;
                    }
                    this.setEntityCounter(1);
                case 1:
                    if (moveToTargetPosition(this.getX(), 134 + 4, 1)) {
                        break yield;
                    }
                    this.setEntityCounter(2);
                case 2:
                    if (moveToTargetPosition(this.getX(), 134 - 4, 1)) {
                        break yield;
                    }
                    this.cageUpDownCount++;
                    if (this.cageUpDownCount <= this.type * 2) {
                        this.setEntityCounter(1);
                        break yield;
                    }
                    this.setEntityCounter(3);
                case 3:
                    if (moveToTargetPosition(this.getX(), 134, 1)) {
                        break yield;
                    }
                    this.setEntityCounter(4);
                case 4:
                    if (moveToTargetPosition(105, 134, 1)) {
                        break yield;
                    }
                    this.setEntityCounter(5);
                case 5:
                    if (moveToTargetPosition(105, 110, 1)) {
                        break yield;
                    }
                    if ((int) (2 * Math.random()) == 0) {
                        this.setEntityCounter(7);
                        continue yield;
                    }
                    this.setEntityCounter(6);
                case 6:
                    if (moveToTargetPosition(109, 110, 1)) {
                        break yield;
                    }
                    this.desiredDirection = 0;
                    this.lastDirection = 0;
                    updatePosition(18, 11);
                    this.setEntityCounter(8);
                    continue yield;
                case 7:
                    if (moveToTargetPosition(101, 110, 1)) {
                        break yield;
                    }
                    this.desiredDirection = 2;
                    this.lastDirection = 2;
                    updatePosition(17, 11);
                    this.setEntityCounter(8);
                case 8:
                    setMode(Mode.NORMAL);
                    break yield;
            }
        }
    }

    private void updateGhostNormal() {
        if (checkVulnerableModeTime() && this.markAsVulnerable) {
            setMode(Mode.VULNERABLE);
            this.markAsVulnerable = false;
        }

        final Function<PacmanGame, Void> function = pacmanGame -> {
            pacmanGame.setState(State.PACMAN_DIED);
            return null;
        };

        if (this.type == 0 || this.type == 1) {
            updateGhostMovement(true, this.pacman.getCol(), this.pacman.getRow(), function, 0, 1, 2, 3); // chase movement
        } else {
            updateGhostMovement(false, 0, 0, function, 0, 1, 2, 3); // random movement
        }
    }

    private void updateGhostVulnerable() {
        if (this.markAsVulnerable) {
            this.markAsVulnerable = false;
        }

        final Function<PacmanGame, Void> function = pacmanGame -> {
            pacmanGame.ghostCaught(Ghost.this);
            return null;
        };

        updateGhostMovement(true, this.pacman.getCol(), this.pacman.getRow(), function, 2, 3, 0, 1); // run away movement
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
            switch (this.getEntityCounter()) {
                case 0:
                    this.pathFinder.find(this.getColumn(), this.getRow(), 18, 11);
                    this.setEntityCounter(1);
                case 1:
                    if (!this.pathFinder.hasNext()) {
                        this.setEntityCounter(3);
                        continue yield;
                    }
                    Point nextPosition = this.pathFinder.getNext();
                    this.setColumn(nextPosition.x);
                    this.setRow(nextPosition.y);
                    this.setEntityCounter(2);
                case 2:
                    if (!moveToGridPosition(this.getColumn(), this.getRow(), 4)) {
                        if (this.getRow() == 11 && (this.getColumn() == 17 || this.getColumn() == 18)) {
                            this.setEntityCounter(3);
                            continue yield;
                        }
                        this.setEntityCounter(1);
                        continue yield;
                    }
                    break yield;
                case 3:
                    if (!moveToTargetPosition(105, 110, 4)) {
                        this.setEntityCounter(4);
                        continue yield;
                    }
                    break yield;
                case 4:
                    if (!moveToTargetPosition(105, 134, 4)) {
                        this.setEntityCounter(5);
                        continue yield;
                    }
                    break yield;
                case 5:
                    setMode(Mode.CAGE);
                    this.setEntityCounter(4);
                    break yield;
            }
        }
    }

    private void updateGhostMovement(final boolean useTarget,
                                     final int targetCol,
                                     final int targetRow,
                                     final Function<PacmanGame, Void> function,
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

        yield:
        while (true) {
            switch (this.getEntityCounter()) {
                case 0:
                    if ((this.getRow() == 14 && this.getColumn() == 1 && this.lastDirection == 2)
                            || (this.getRow() == 14 && this.getColumn() == 34 && this.lastDirection == 0)) {
                        adjustHorizontalOutsideMovement();
                    }

                    double angle = Math.toRadians(this.desiredDirection * 90);
                    int dx = (int) Math.cos(angle);
                    int dy = (int) Math.sin(angle);
                    if (useTarget && this.getGame().maze[this.getRow() + dy][this.getColumn() + dx] == 0
                            && this.desiredDirection != backwardDirections[this.lastDirection]) {

                        this.direction = this.desiredDirection;
                    } else {
                        do {
                            this.direction = (int) (4 * Math.random());
                            angle = Math.toRadians(this.direction * 90);
                            dx = (int) Math.cos(angle);
                            dy = (int) Math.sin(angle);
                        }
                        while (this.getGame().maze[this.getRow() + dy][this.getColumn() + dx] == -1
                                || this.direction == backwardDirections[this.lastDirection]);
                    }

                    this.setColumn(this.getColumn() + dx);
                    this.setRow(this.getRow() + dy);
                    this.setEntityCounter(1);
                case 1:
                    if (!moveToGridPosition(this.getColumn(), this.getRow(), 1)) {
                        this.lastDirection = this.direction;
                        this.setEntityCounter(0);
                    }
                    if (checkCollisionWithPacman()) {
                        function.apply(this.getGame());
                    }
                    break yield;
            }
        }
    }

    private boolean checkCollisionWithPacman() {
        return this.pacman.getBoundingBox().intersects(this.getBoundingBox());
    }

    private void modeChanged() {
        this.setEntityCounter(0);
    }

    public void showEntity() {
        this.setVisible(true);
    }

    public void hideEntity() {
        this.setVisible(false);
    }

    public void startGhostVulnerableMode() {
        this.vulnerableModeStartTime = System.currentTimeMillis();
        this.markAsVulnerable = true;
    }

    void died() {
        setMode(Mode.DIED);
    }

}
