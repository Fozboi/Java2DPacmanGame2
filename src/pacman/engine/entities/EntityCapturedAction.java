package pacman.engine.entities;

import pacman.engine.PacmanGame;

@FunctionalInterface
public interface EntityCapturedAction {
    void execute(final PacmanGame game);
}
