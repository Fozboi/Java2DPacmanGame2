package pacman.engine.entities;

import pacman.engine.PacmanGame;

public abstract class Pellet extends PacManEntity {

    boolean eaten;

    Pellet(final PacmanGame game) {
        super(game);
    }

}
