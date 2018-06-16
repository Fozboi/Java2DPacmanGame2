package pacman.engine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class PacmanGame {

    private final Dimension screenSize;
    private final Point2D screenScale;
    private final List<PacmanEntity> allPacmanEntities = new ArrayList<>();
    private final List<Ghost> pacmanGhosts = new ArrayList<>();
    private final Pacman pacman;
    private final BitmapFontRenderer bitmapFontRenderer = new BitmapFontRenderer("/resources/font8x8.png", 16, 16);
    private State state;
    private int lives;
    private int score;
    private int highScore;
    private Ghost caughtGhost;
    private int caughtGhostScoreTableIndex = 0;
    private int foodCount;
    private int currentFoodCount;

    final static int[] CAUGHT_GHOST_SCORE_TABLE = { 200, 400, 800, 1600 };

    Ghost getCaughtGhost() {
        return this.caughtGhost;
    }

    private void setCaughtGhost(Ghost caughtGhost) {
        this.caughtGhost = caughtGhost;
    }

    int getCaughtGhostScoreTableIndex() {
        return this.caughtGhostScoreTableIndex;
    }

    void setCaughtGhostScoreTableIndex(int caughtGhostScoreTableIndex) {
        this.caughtGhostScoreTableIndex = caughtGhostScoreTableIndex;
    }

    int getCurrentFoodCount() {
        return this.currentFoodCount;
    }

    void setCurrentFoodCount(int currentFoodCount) {
        this.currentFoodCount = currentFoodCount;
    }

    public enum State {
        INITIALIZING,
        OL_PRESENTS,
        TITLE,
        READY,
        READY2,
        PLAYING,
        PACMAN_DIED,
        GHOST_CATCHED,
        LEVEL_CLEARED,
        GAME_OVER
    }

    final int[][] maze = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,3,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,3,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,2,2,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,2,2,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,0,1,1,0,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,0,1,1,0,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,1,1,0,0,0,0,1,1,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,2,2,2,2,2,2,2,0,0,0,1,1,0,0,0,0,1,1,0,0,0,2,2,2,2,2,2,2,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,3,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,3,1,1,1,1,1},
        {1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1},
        {1,1,1,1,1,2,2,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,2,2,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1},
        {1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    public PacmanGame() {
        this.screenSize = new Dimension(224, 288);
        this.screenScale = new Point2D.Double(2, 2);
        this.setState(State.INITIALIZING);
        this.setLives(3);
        this.pacman = new Pacman(this);
    }

    Dimension getScreenSize() {
        return this.screenSize;
    }

    Point2D getScreenScale() {
        return this.screenScale;
    }

    private List<PacmanEntity> getAllPacmanEntities() {
        return this.allPacmanEntities;
    }

    int getLives() {
        return this.lives;
    }

    private void setLives(final int lives) {
        this.lives = lives;
    }

    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        if (this.state != state) {
            this.state = state;
            for(final PacmanEntity entity : this.getAllPacmanEntities()) {
                entity.stateChanged();
            }
        }
    }

    void addScore(final int point) {
        this.score += point;
        if (this.score > this.highScore) {
            this.highScore = this.score;
        }
    }

    String getScore() {
        String scoreStr = "0000000" + this.score;
        scoreStr = scoreStr.substring(scoreStr.length() - 7, scoreStr.length());
        return scoreStr;
    }

    String getHighScore() {
        String hiscoreStr = "0000000" + this.highScore;
        hiscoreStr = hiscoreStr.substring(hiscoreStr.length() - 7, hiscoreStr.length());
        return hiscoreStr;
    }

    void init() {
        addAllObjs();
    }

    private void addAllObjs() {
        this.getAllPacmanEntities().add(new Initializer(this));
        this.getAllPacmanEntities().add(new OLPresents(this));
        this.getAllPacmanEntities().add(new Title(this));
        this.getAllPacmanEntities().add(new Background(this));
        this.foodCount = 0;
        for (int row=0; row<31; row++) {
            for (int col=0; col<36; col++) {
                if (this.maze[row][col] == 1) {
                    this.maze[row][col] = -1; // wall convert to -1 for ShortestPathFinder
                }
                else if (this.maze[row][col] == 2) {
                    this.maze[row][col] = 0;
                    this.getAllPacmanEntities().add(new NormalPellet(this, row, col));
                    this.foodCount++;
                }
                else if (this.maze[row][col] == 3) {
                    this.maze[row][col] = 0;
                    this.getAllPacmanEntities().add(new PowerPellet(this, col, row));
                }
            }
        }

        for (int i=0; i<4; i++) {
            final Ghost ghost = new Ghost(this, this.pacman, i);
            this.allPacmanEntities.add(ghost);
            this.pacmanGhosts.add(ghost);
        }

        this.getAllPacmanEntities().add(this.pacman);
        this.getAllPacmanEntities().add(new Point(this, this.pacman));
        this.getAllPacmanEntities().add(new Ready(this));
        this.getAllPacmanEntities().add(new GameOver(this));
        this.getAllPacmanEntities().add(new HUD(this));
    }

    void restoreCurrentFoodCount() {
        this.setCurrentFoodCount(this.foodCount);
    }

    boolean isLevelCleared() {
        return this.getCurrentFoodCount() == 0;
    }

    void startGame() {
        setState(State.READY);
    }

    void startGhostVulnerableMode() {
        this.setCaughtGhostScoreTableIndex(0);
        for(final Ghost ghost : this.pacmanGhosts) {
            ghost.startGhostVulnerableMode();
        }
    }

    void ghostCatched(Ghost ghost) {
        this.setCaughtGhost(ghost);
        setState(State.GHOST_CATCHED);
    }

    void nextLife() {
        this.setLives(this.getLives() - 1);
        if (this.getLives() == 0) {
            setState(State.GAME_OVER);
        }
        else {
            setState(State.READY2);
        }
    }

    void levelCleared() {
        setState(State.LEVEL_CLEARED);
    }

    void nextLevel() {
        setState(State.READY);
    }

    void returnToTitle() {
        this.setLives(3);
        this.score = 0;
        setState(State.TITLE);
    }

    void update() {
        for (final PacmanEntity actor : this.getAllPacmanEntities()) {
            actor.update();
        }
    }

    void draw(final Graphics2D g) {
        for (final PacmanEntity actor : this.getAllPacmanEntities()) {
            actor.draw(g);
        }
    }

    boolean pelletConsumed(final Pellet pellet) {
        pellet.updateBoundingBox();
        this.pacman.updateBoundingBox();
        return pellet.boundingBox != null && this.pacman.boundingBox != null
                && pellet.isVisible() && this.pacman.isVisible()
                && pellet.boundingBox.intersects(this.pacman.boundingBox);
    }

    void broadcastMessage(String message) {
        for (final PacmanEntity obj : this.getAllPacmanEntities()) {
            try {
                final Method method = obj.getClass().getMethod(message);
                if (method != null) {
                    method.invoke(obj);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void showAll() {
        for (final PacmanEntity obj : this.getAllPacmanEntities()) {
            obj.showAll();
        }
    }

    void hideAll() {
        for (final PacmanEntity obj : this.getAllPacmanEntities()) {
            obj.hideAll();
        }
    }

    void drawText(Graphics2D g, String text, int x, int y) {
        this.bitmapFontRenderer.drawText(g, text, x, y);
    }

}
