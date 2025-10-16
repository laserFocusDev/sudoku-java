package sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 * Backtracking Sudoku solver with basic optimizations:
 * - keep track of used numbers per row/col/box with boolean arrays
 * - choose next empty cell with minimum possible candidates (simple heuristic)
 */
public class Solver {

    private final boolean[][] rowUsed = new boolean[9][10];
    private final boolean[][] colUsed = new boolean[9][10];
    private final boolean[][] boxUsed = new boolean[9][10];
    private int solutionsFound = 0;
    private final int solutionLimit; // when >1 used for uniqueness checks. if <=0 then no limit

    public Solver() {
        this(0);
    }

    public Solver(int solutionLimit) {
        this.solutionLimit = solutionLimit;
    }

    public boolean solve(Board board) {
        initialize(board);
        return backtrack(board);
    }

    /**
     * Find number of solutions up to the solutionLimit (if solutionLimit>0).
     * Returns number of solutions found (may be >1 or 0).
     */
    public int countSolutions(Board board) {
        solutionsFound = 0;
        initialize(board);
        backtrackCount(board);
        return solutionsFound;
    }

    private void initialize(Board board) {
        for (int i = 0; i < 9; i++) {
            for (int d = 1; d <= 9; d++) {
                rowUsed[i][d] = false;
                colUsed[i][d] = false;
                boxUsed[i][d] = false;
            }
        }
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int v = board.get(r, c);
                if (v != 0) {
                    if (!isValidPlacementNoCheck(board, r, c, v)) {
                        // invalid initial board: we still mark but must be careful
                    }
                    mark(r, c, v, true);
                }
            }
        }
    }

    private int boxIndex(int r, int c) {
        return (r / 3) * 3 + (c / 3);
    }

    private void mark(int r, int c, int v, boolean on) {
        rowUsed[r][v] = on;
        colUsed[c][v] = on;
        boxUsed[boxIndex(r, c)][v] = on;
    }

    private boolean isSafe(int r, int c, int v) {
        return !rowUsed[r][v] && !colUsed[c][v] && !boxUsed[boxIndex(r,c)][v];
    }

    // used during initialization -- doesn't consult board contents
    private boolean isValidPlacementNoCheck(Board board, int r, int c, int v) {
        return !rowUsed[r][v] && !colUsed[c][v] && !boxUsed[boxIndex(r,c)][v];
    }

    private boolean backtrack(Board board) {
        // find empty cell with fewest candidates
        int bestR = -1, bestC = -1, bestCount = 10;
        int[] candidates = null;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.get(r,c) == 0) {
                    List<Integer> list = new ArrayList<>();
                    for (int v = 1; v <= 9; v++) if (isSafe(r,c,v)) list.add(v);
                    if (list.isEmpty()) return false; // dead end
                    if (list.size() < bestCount) {
                        bestCount = list.size();
                        bestR = r;
                        bestC = c;
                        candidates = list.stream().mapToInt(Integer::intValue).toArray();
                        if (bestCount == 1) break;
                    }
                }
            }
            if (bestCount == 1) break;
        }

        if (bestR == -1) return true; // solved

        for (int v : candidates) {
            board.set(bestR, bestC, v);
            mark(bestR, bestC, v, true);
            boolean solved = backtrack(board);
            if (solved) return true;
            mark(bestR, bestC, v, false);
            board.clear(bestR, bestC);
        }
        return false;
    }

    private void backtrackCount(Board board) {
        if (solutionLimit > 0 && solutionsFound >= solutionLimit) return;

        // find empty cell with fewest candidates
        int bestR = -1, bestC = -1, bestCount = 10;
        int[] candidates = null;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.get(r,c) == 0) {
                    List<Integer> list = new ArrayList<>();
                    for (int v = 1; v <= 9; v++) if (isSafe(r,c,v)) list.add(v);
                    if (list.isEmpty()) return;
                    if (list.size() < bestCount) {
                        bestCount = list.size();
                        bestR = r;
                        bestC = c;
                        candidates = list.stream().mapToInt(Integer::intValue).toArray();
                        if (bestCount == 1) break;
                    }
                }
            }
            if (bestCount == 1) break;
        }

        if (bestR == -1) {
            solutionsFound++;
            return;
        }

        for (int v : candidates) {
            board.set(bestR, bestC, v);
            mark(bestR, bestC, v, true);
            backtrackCount(board);
            mark(bestR, bestC, v, false);
            board.clear(bestR, bestC);
            if (solutionLimit > 0 && solutionsFound >= solutionLimit) return;
        }
    }

    /** Quick validity check for a full or partial board (no heavy search). */
    public static boolean isValidBoard(Board b) {
        boolean[][] r = new boolean[9][10];
        boolean[][] c = new boolean[9][10];
        boolean[][] box = new boolean[9][10];
        for (int i=0;i<9;i++) for (int d=1;d<=9;d++) { r[i][d]=false; c[i][d]=false; box[i][d]=false; }
        for (int i=0;i<9;i++) {
            for (int j=0;j<9;j++) {
                int val = b.get(i,j);
                if (val==0) continue;
                int bi = (i/3)*3 + (j/3);
                if (r[i][val] || c[j][val] || box[bi][val]) return false;
                r[i][val] = c[j][val] = box[bi][val] = true;
            }
        }
        return true;
    }
}
