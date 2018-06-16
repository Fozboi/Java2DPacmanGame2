package pacman.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class NormalPellet extends Pellet {

    NormalPellet(final PacmanGame game,
                 final int row,
                 final int col) {
        super(game, row, col);
        loadFrames("/resources/food.png");
        this.x = col * 8 + 3 - 32;
        this.y = (row + 3) * 8 + 3;
        this.boundingBox = new Rectangle(0, 0, 2, 2);
    }

    @Override
    public void update() {
        if (this.getGame().getState() == PacmanGame.State.PLAYING) {
            if (this.getGame().pelletConsumed(this)) {
                this.setVisible(false);
                this.getGame().setCurrentFoodCount(this.getGame().getCurrentFoodCount() - 1);
                this.getGame().addScore(10);
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.isVisible()) {
            g.setColor(Color.WHITE);
            g.fillRect((int) (this.x), (int) (this.y), 2, 2);
        }
    }

    @Override
    public void stateChanged() {
        if (this.getGame().getState() == PacmanGame.State.TITLE) {
            this.setVisible(false);
        } else if (this.getGame().getState() == PacmanGame.State.READY) {
            this.setVisible(true);
        }
    }

    public void hideAll() {
        this.setVisible(false);
    }

    public void showAll() {
        this.setVisible(true);
    }

}
