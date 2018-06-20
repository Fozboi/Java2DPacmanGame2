package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.PacmanGame.State;
import pacman.engine.SoundUtils;

import java.awt.*;

public class PointMonitor extends PacManEntity {

    public PointMonitor(final PacmanGame game) {
        super(game);
        loadFrames("resources/point_0.png",
                              "resources/point_1.png",
                              "resources/point_2.png",
                              "resources/point_3.png");
        this.setBoundingBox(new Rectangle(0, 0, 4, 4));
    }

    @Override
    public void update() {
        if (getGame().getState() == State.GHOST_CAPTURED) {
            setVisible(true);
            switch (getEntityCounter()) {
                case 0:
                    updatePosition(getGame().getCaughtGhost().getColumn(), getGame().getCaughtGhost().getRow());
                    getGame().getPacman().setVisible(false);
                    getGame().getCaughtGhost().setVisible(false);
                    final int frameIndex = getGame().getCaughtGhostScoreTableIndex();
                    setFrame(getFrames()[frameIndex]);
                    getGame().addScore(PacmanGame.getCapturedGhostScoreTable()[frameIndex]);
                    getGame().setCaughtGhostScoreTableIndex(getGame().getCaughtGhostScoreTableIndex() + 1);
                    startTimer();
                    incrementEntityCounter();
                    break;
                case 1:
                    if (getElapsedTime() > 500) {
                        SoundUtils.playSoundStream("resources/pacman_eatghost.wav");
                        setVisible(false);
                        getGame().getPacman().setVisible(true);
                        getGame().getPacman().updatePosition();
                        getGame().getCaughtGhost().setVisible(true);
                        getGame().getCaughtGhost().updatePosition();
                        getGame().getCaughtGhost().died();
                        resetEntityCounter();
                        getGame().setState(State.PLAYING);
                    }
                    break;
            }
        }

    }

    @Override
    public void hideEntity() {
        this.setVisible(false);
    }

    @Override
    public void showEntity() {
        this.setVisible(true);
    }

    private void updatePosition(final int col,
                                final int row) {
        this.setX(col * 8 - 4 - 32);
        this.setY((row + 3) * 8 + 1);
    }

}
