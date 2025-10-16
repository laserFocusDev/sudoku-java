package sudoku;

import java.util.Arrays;

/**
 * Immutable-ish representation of a 9x9 Sudoku board.
 * Uses 0 to represent empty cells.
 */
public class Board {
    public static final int SIZE = 9;
    private final int[][] grid;

    public Board() {
        grid = new int[SIZE][SIZE];
    }

    public Board(int[][] initial) {
        if (initial.length != SIZE) throw new IllegalArgumentException("Board must be 9x9");
        grid = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            if (initial[r].length != SIZE) throw new IllegalArgumentException("Board must be 9x9");
            System.arraycopy(initial[r], 0, grid[r], 0, SIZE);
        }
    }

    public int get(int r, int c) {
        return grid[r][c];
    }

    public void set(int r, int c, int val) {
        grid[r][c] = val;
    }

    public void clear(int r, int c) {
        grid[r][c] = 0;
    }

    public int[][] getGridCopy() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) System.arraycopy(grid[r], 0, copy[r], 0, SIZE);
        return copy;
    }

    public Board copy() {
        return new Board(getGridCopy());
    }

    public boolean isFull() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) return false;
        return true;
    }

    public void clearAll() {
        for (int r = 0; r < SIZE; r++) Arrays.fill(grid[r], 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                sb.append(grid[r][c]);
                if (c < SIZE-1) sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
