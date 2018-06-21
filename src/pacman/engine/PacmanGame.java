package pacman.engine;

import pacman.engine.entities.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class PacmanGame {

    private final List<PacManEntity> allPacmanEntities = new ArrayList<>();
    private final List<Ghost> pacmanGhosts = new ArrayList<>();
    private final Pacman pacman;
    private final BitmapFontRenderer bitmapFontRenderer = new BitmapFontRenderer();
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

    public int[][] getGameMaze() {
        return this.gameMaze;
    }

    public enum State {
        INITIALIZING,
        TITLE,
        READY,
        PLAYING,
        PACMAN_DIED,
        GHOST_CAPTURED,
        LEVEL_CLEARED,
        GAME_OVER
    }

    private final int[][] gameMaze = {
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
        setState(State.INITIALIZING);
        this.lives = 3;
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

    public int getLives() {
        return this.lives;
    }

    public boolean isLevelCleared() {
        return this.currentFoodCount == this.foodCount;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public void addScore(final int point) {
        this.score += point;
        if (this.score > this.highScore) {
            this.highScore = this.score;
        }
    }

    public void setCaughtGhostScoreTableIndex(final int caughtGhostScoreTableIndex) {
        this.caughtGhostScoreTableIndex = caughtGhostScoreTableIndex;
    }

    public void incrementFoodCount() {
        this.currentFoodCount++;
    }

    public void startGhostVulnerableMode() {
        this.setCaughtGhostScoreTableIndex(0);
        for(final Ghost ghost : this.pacmanGhosts) {
            ghost.startGhostVulnerableMode();
        }
    }

    public void ghostCaught(final Ghost ghost) {
        this.caughtGhost = ghost;
        setState(State.GHOST_CAPTURED);
    }

    public void nextLife() {
        this.lives = this.lives--;
        if (this.getLives() == 0) {
            setState(State.GAME_OVER);
        }
        else {
            setState(State.READY);
        }
    }

    public void levelCleared() {
        setState(State.LEVEL_CLEARED);
    }

    public void nextLevel() {
        setState(State.READY);
    }

    public void returnToTitle() {
        this.lives = 3;
        this.score = 0;
        this.currentFoodCount = 0;
        setState(State.TITLE);
    }

    public boolean pacmanEatsPellet(final Pellet pellet) {
        return pellet.isVisible() && pellet.getBoundingBox().intersects(this.pacman.getBoundingBox());
    }

    public void drawText(final Graphics2D g,
                         final String text,
                         final int x,
                         final int y) {
        this.bitmapFontRenderer.drawText(g, text, x, y);
    }

    Dimension getScreenSize() {
        return SCREEN_SIZE;
    }

    Point2D getScreenScale() {
        return SCREEN_SCALE;
    }

    void initializeGameObjects() {
        this.allPacmanEntities.add(new IntroductionBanner(this));
        this.allPacmanEntities.add(new Title(this));
        this.allPacmanEntities.add(new Background(this));
        this.foodCount = 0;
        for (int row = 0; row < 31; row++) {
            for (int col = 0; col < 36; col++) {
                if (this.getGameMaze()[row][col] == 1) {
                    this.getGameMaze()[row][col] = -1;
                }
                else if (this.getGameMaze()[row][col] == 2) {
                    this.getGameMaze()[row][col] = 0;
                    this.allPacmanEntities.add(new NormalPellet(this, row, col));
                    this.foodCount++;
                }
                else if (this.getGameMaze()[row][col] == 3) {
                    this.getGameMaze()[row][col] = 0;
                    this.allPacmanEntities.add(new PowerPellet(this, col, row));
                    this.foodCount++;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            final Ghost ghost = new Ghost(this, this.pacman, i);
            this.allPacmanEntities.add(ghost);
            this.pacmanGhosts.add(ghost);
        }

        this.allPacmanEntities.add(this.pacman);
        this.allPacmanEntities.add(new PointMonitor(this));
        this.allPacmanEntities.add(new Ready(this));
        this.allPacmanEntities.add(new GameOverBanner(this));
        this.allPacmanEntities.add(new HUD(this));
    }

    void update() {
        for (final PacManEntity actor : this.allPacmanEntities) {
            actor.update();
            //System.out.println(actor + " isVisible = " +actor.isVisible());
        }
    }

    void draw(final Graphics2D g) {
        for (final PacManEntity actor : this.allPacmanEntities) {
            actor.draw(g);
        }
    }

}
