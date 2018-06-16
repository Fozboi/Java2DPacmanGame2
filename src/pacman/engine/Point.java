package pacman.engine;

import pacman.engine.PacmanGame.State;
import java.awt.Rectangle;

public class Point extends PacmanEntity {

    private final Pacman pacman;

    Point(final PacmanGame game,
          final Pacman pacman) {
        super(game);
        this.pacman = pacman;
        loadFrames("/resources/point_0.png",
                "/resources/point_1.png"
                , "/resources/point_2.png", "/resources/point_3.png");
        this.boundingBox = new Rectangle(0, 0, 4, 4);
    }

    private void updatePosition(final int col,
                                final int row) {
        this.x = col * 8 - 4 - 32;
        this.y = (row + 3) * 8 + 1;
    }

    @Override
    public void update() {
        if(this.getGame().getState() == State.GHOST_CAPTURED) {
                switch (this.instructionPointer) {
                    case 0:
                        updatePosition(this.getGame().getCaughtGhost().getCol(), this.getGame().getCaughtGhost().getRow());
                        this.pacman.setVisible(false);
                        this.getGame().getCaughtGhost().setVisible(false);
                        int frameIndex = this.getGame().getCaughtGhostScoreTableIndex();
                        this.frame = this.frames[frameIndex];
                        this.getGame().addScore(PacmanGame.CAPTURED_GHOST_SCORE_TABLE[frameIndex]);
                        this.getGame().setCaughtGhostScoreTableIndex(this.getGame().getCaughtGhostScoreTableIndex() + 1);
                        this.startTime = System.currentTimeMillis();
                        this.instructionPointer = 1;
                    case 1:
                        if (System.currentTimeMillis() - this.startTime < 500) {
                            break;
                        }
                        this.pacman.setVisible(true);
                        this.pacman.updatePosition();
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
            this.instructionPointer = 0;
        }
    }

    public void hideAll() {
        this.setVisible(false);
    }

    public void showAll() {
        this.setVisible(true);
    }

}
