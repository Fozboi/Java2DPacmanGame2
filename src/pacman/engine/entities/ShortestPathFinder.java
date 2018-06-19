package pacman.engine.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class ShortestPathFinder {

    private final int[][] map;
    private final List<Integer> path = new ArrayList<>();
    private int pathIndex;
    private final Point pathPosition = new Point();

    ShortestPathFinder(final int[][] originalMap) {
        this.map = new int[originalMap.length][originalMap[0].length];
        for (int y = 0; y < this.map.length; y++) {
            System.arraycopy(originalMap[y], 0, this.map[y], 0, this.map[0].length);
        }
    }

    private void clearMap() {
        for (int y = 0; y < this.map.length; y++) {
            for (int x = 0; x < this.map[0].length; x++) {
                this.map[y][x] = 0;
            }
        }
    }

    private int getMapScore(final int x,
                            final int y) {
        if (x < 0 || x > this.map[0].length - 1 || y < 0 || y > this.map.length - 1) {
            return -1;
        }
        return this.map[y][x];
    }

    private final int[] neighbors = {1, 0, -1, 0, 0, 1, 0, -1};

    void find(final int srcX,
              final int srcY,
              final int destX,
              final int destY) {
        this.path.clear();
        clearMap();
        int score = 1;
        this.map[destY][destX] = score;
        found:
        while (true) {
            boolean foundAtLeastOne = false;
            for (int y = 0; y < this.map.length; y++) {
                for (int x = 0; x < this.map[0].length; x++) {
                    if (getMapScore(x, y) == score) {
                        foundAtLeastOne = true;
                        for (int n = 0; n < this.neighbors.length; n += 2) {
                            int dx = x + this.neighbors[n];
                            int dy = y + this.neighbors[n + 1];
                            if (getMapScore(dx, dy) == 0) {
                                this.map[dy][dx] = score + 1;
                                if (dx == srcX && dy == srcY) {
                                    fillPath(this.path, score + 1, dx, dy);
                                    this.pathIndex = 0;
                                    break found;
                                }
                            }
                        }
                    }
                }
            }
            if (!foundAtLeastOne) {
                break;
            }
            score++;
        }
    }

    private void fillPath(List<Integer> path, int score, int dx, int dy) {
        int direction = 10;
        while (score > 0) {
            int ax = (direction & 3) - 2;
            int ay = ((direction >> 2) & 3) - 2;
            direction >>= 4;
            if (getMapScore(dx + ax, dy + ay) == score) {
                path.add(dx += ax);
                path.add(dy += ay);
                int k = 4 * (int) (32 * Math.random());
                direction = (28315 >> k) | (28315 << (32 - k));
                score--;
            }
        }
    }

    boolean hasNext() {
        return this.pathIndex < this.path.size() - 1;
    }

    Point getNext() {
        if (!hasNext()) {
            return null;
        }
        this.pathPosition.setLocation(this.path.get(this.pathIndex), this.path.get(this.pathIndex + 1));
        this.pathIndex += 2;
        return this.pathPosition;
    }

}
