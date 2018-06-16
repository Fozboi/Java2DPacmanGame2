package br.ol.pacman.engine;

abstract class Pellet extends PacmanEntity {

    Pellet(final PacmanGame game,
           final int col,
           final int row) {
        super(game);
    }

}
