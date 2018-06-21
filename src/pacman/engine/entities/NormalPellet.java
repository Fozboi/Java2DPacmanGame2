package pacman.engine.entities;

import pacman.engine.PacmanGame;
import pacman.engine.SoundUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class NormalPellet extends Pellet {

    public NormalPellet(final PacmanGame game,
                        final int row,
                        final int col) {
        super(game);
        loadFrames("food.png");
        setxPosition(col * 8 + 3 - 32);
        setyPosition((row + 3) * 8 + 3);
        setBoundingBox(new Rectangle(getxPosition(), getyPosition(), 2, 2));
    }

    @Override
    public void update() {
        if (getGame().getState() == PacmanGame.State.READY) {
            setVisible(true);
        } else if (this.getGame().getState() == PacmanGame.State.PLAYING) {
            if (getGame().pacmanEatsPellet(this)) {
                this.eaten = true;
                getGame().incrementFoodCount();
                setVisible(false);
                getGame().addScore(10);
                SoundUtils.playSoundStream("pacman_chomp.wav");
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (isVisible()) {
            g.setColor(Color.WHITE);
            g.fillRect(getxPosition(), getyPosition(), 2, 2);
        }
    }

}
