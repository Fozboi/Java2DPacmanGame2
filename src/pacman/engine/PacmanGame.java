package pacman.engine;

import pacman.engine.entities.*;
import pacman.engine.entities.Point;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class PacmanGame {

    private final List<PacManEntity> allPacmanEntities = new ArrayList<>();
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

    private static final int[] CAPTURED_GHOST_SCORE_TABLE = { 200, 400, 800, 1600 };
    private static final Dimension SCREEN_SIZE = new Dimension(224, 288);
    private static final Point2D SCREEN_SCALE = new Point2D.Double(2, 2);

    public PacmanGame() {
        this.setState(State.INITIALIZING);
        this.setLives(3);
        this.pacman = new Pacman(this);
    }

    public static int[] getCapturedGhostScoreTable() {
        return CAPTURED_GHOST_SCORE_TABLE;
    }

    public Pacman getPacman() {
        return this.pacman;
    }

    public int getCaughtGhostScoreTableIndex() {
        return this.caughtGhostScoreTableIndex;
    }

    public int getCurrentFoodCount() {
        return this.currentFoodCount;
    }

    public Ghost getCaughtGhost() {
        return this.caughtGhost;
    }

    public State getState() {
        return this.state;
    }

    public String getScore() {
        String scoreStr = "0000000" + this.score;
        scoreStr = scoreStr.substring(scoreStr.length() - 7, scoreStr.length());
        return scoreStr;
    }

    public String getHighScore() {
        String hiscoreStr = "0000000" + this.highScore;
        hiscoreStr = hiscoreStr.substring(hiscoreStr.length() - 7, hiscoreStr.length());
        return hiscoreStr;
    }

    private void setCaughtGhost(final Ghost caughtGhost) {
        this.caughtGhost = caughtGhost;
    }

    public void setCaughtGhostScoreTableIndex(final int caughtGhostScoreTableIndex) {
        this.caughtGhostScoreTableIndex = caughtGhostScoreTableIndex;
    }

    public void setCurrentFoodCount(final int currentFoodCount) {
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
        GHOST_CAPTURED,
        LEVEL_CLEARED,
        GAME_OVER
    }

    public final int[][] maze = {
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

    Dimension getScreenSize() {
        return SCREEN_SIZE;
    }

    Point2D getScreenScale() {
        return SCREEN_SCALE;
    }

    private List<PacManEntity> getAllPacmanEntities() {
        return this.allPacmanEntities;
    }

    public int getLives() {
        return this.lives;
    }

    public boolean isLevelCleared() {
        return this.getCurrentFoodCount() == 0;
    }

    private void setLives(final int lives) {
        this.lives = lives;
    }

    public void setState(final State state) {
        if (this.state != state) {
            this.state = state;
            for(final PacManEntity entity : this.allPacmanEntities) {
                entity.stateChanged();
            }
        }
    }

    public void addScore(final int point) {
        this.score += point;
        if (this.score > this.highScore) {
            this.highScore = this.score;
        }
    }

    void initializeGameObjects() {
        this.allPacmanEntities.add(new InitializationBanner(this));
        this.allPacmanEntities.add(new PresentBanner(this));
        this.allPacmanEntities.add(new Title(this));
        this.allPacmanEntities.add(new Background(this));
        this.foodCount = 0;
        for (int row = 0; row < 31; row++) {
            for (int col = 0; col < 36; col++) {
                if (this.maze[row][col] == 1) {
                    this.maze[row][col] = -1;
                }
                else if (this.maze[row][col] == 2) {
                    this.maze[row][col] = 0;
                    this.allPacmanEntities.add(new NormalPellet(this, row, col));
                    this.foodCount++;
                }
                else if (this.maze[row][col] == 3) {
                    this.maze[row][col] = 0;
                    this.allPacmanEntities.add(new PowerPellet(this, col, row));
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            final Ghost ghost = new Ghost(this, this.pacman, i);
            this.allPacmanEntities.add(ghost);
            this.pacmanGhosts.add(ghost);
        }

        this.allPacmanEntities.add(this.pacman);
        this.allPacmanEntities.add(new Point(this));
        this.allPacmanEntities.add(new Ready(this));
        this.allPacmanEntities.add(new GameOver(this));
        this.allPacmanEntities.add(new HUD(this));
    }

    public void restoreCurrentFoodCount() {
        this.setCurrentFoodCount(this.foodCount);
    }

    public void startGame() {
        setState(State.READY);
    }

    public void startGhostVulnerableMode() {
        this.setCaughtGhostScoreTableIndex(0);
        for(final Ghost ghost : this.pacmanGhosts) {
            ghost.startGhostVulnerableMode();
        }
    }

    public void ghostCaught(final Ghost ghost) {
        this.setCaughtGhost(ghost);
        setState(State.GHOST_CAPTURED);
    }

    public void nextLife() {
        this.setLives(this.getLives() - 1);
        if (this.getLives() == 0) {
            setState(State.GAME_OVER);
        }
        else {
            setState(State.READY2);
        }
    }

    public void levelCleared() {
        setState(State.LEVEL_CLEARED);
    }

    public void nextLevel() {
        setState(State.READY);
    }

    public void returnToTitle() {
        this.setLives(3);
        this.score = 0;
        setState(State.TITLE);
    }

    void update() {
        for (final PacManEntity actor : this.allPacmanEntities) {
            actor.update();
        }
    }

    void draw(final Graphics2D g) {
        for (final PacManEntity actor : this.getAllPacmanEntities()) {
            actor.draw(g);
        }
    }

    public boolean consumePellet(final Pellet pellet) {
        return pellet.getBoundingBox() != null && this.pacman.getBoundingBox() != null
                && pellet.isVisible() && this.pacman.isVisible()
                && pellet.getBoundingBox().intersects(this.pacman.getBoundingBox());
    }

    public void showAll() {
        for (final PacManEntity obj : this.getAllPacmanEntities()) {
            obj.showAll();
        }
    }

    public void hideAll() {
        for (final PacManEntity obj : this.getAllPacmanEntities()) {
            obj.hideAll();
        }
    }

    public void drawText(final Graphics2D g,
                         final String text,
                         final int x,
                         final int y) {
        this.bitmapFontRenderer.drawText(g, text, x, y);
    }

}
