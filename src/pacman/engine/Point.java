package pacman.engine;

import pacman.engine.PacmanGame.State;

import java.awt.Rectangle;

public class Point extends PacManEntity {

    Point(final PacmanGame game) {
        super(game);
        loadFrames("/resources/point_0.png",
                "/resources/point_1.png"
                , "/resources/point_2.png",
                "/resources/point_3.png");
        this.setBoundingBox(new Rectangle(0, 0, 4, 4));
    }

    private void updatePosition(final int col,
                                final int row) {
        this.setX(col * 8 - 4 - 32);
        this.setY((row + 3) * 8 + 1);
    }

    @Override
    public void update() {
        if (this.getGame().getState() == State.GHOST_CAPTURED) {
            switch (this.getInstructionPointer()) {
                case 0:
                    updatePosition(this.getGame().getCaughtGhost().getCol(), this.getGame().getCaughtGhost().getRow());
                    this.getGame().getPacman().setVisible(false);
                    this.getGame().getCaughtGhost().setVisible(false);
                    int frameIndex = this.getGame().getCaughtGhostScoreTableIndex();
                    this.setFrame(this.getFrames()[frameIndex]);
                    this.getGame().addScore(PacmanGame.getCapturedGhostScoreTable()[frameIndex]);
                    this.getGame().setCaughtGhostScoreTableIndex(this.getGame().getCaughtGhostScoreTableIndex() + 1);
                    this.setStartTime(System.currentTimeMillis());
                    this.setInstructionPointer(1);
                    break;
                case 1:
                    this.getGame().getPacman().setVisible(true);
                    this.getGame().getPacman().updatePosition();
                    this.getGame().getCaughtGhost().setVisible(true);
                    this.getGame().getCaughtGhost().updatePosition();
                    this.getGame().getCaughtGhost().died();
                    this.getGame().setState(State.PLAYING);
                    break;
            }
        }
    }

    @Override
    public void stateChanged() {
        this.setVisible(false);
        if (this.getGame().getState() == State.GHOST_CAPTURED) {
            this.setVisible(true);
            this.setInstructionPointer(0);
        }
    }

    public void hideAll() {
        this.setVisible(false);
    }

    public void showAll() {
        this.setVisible(true);
    }

}
