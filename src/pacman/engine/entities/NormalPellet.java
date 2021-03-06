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
        loadFrames(new String[]{"food.png"});
        setXPosition(col * 8 + 3 - 32);
        setYPosition((row + 3) * 8 + 3);
        setBoundingBox(new Rectangle(getXPosition(), getYPosition(), 2, 2));
    }

    @Override
    public void update() {
        if (getGame().getState() == PacmanGame.State.READY) {
            setVisible(!this.eaten);
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
            g.fillRect(getXPosition(), getYPosition(), 2, 2);
        }
    }

}
