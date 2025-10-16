package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Simple generator: fill board randomly via backtracking, then remove cells
 * while ensuring uniqueness (by counting solutions up to 2).
 */
public class Generator {

    private final Random rnd = new Random();

    public Board generate(int blanksTarget) {
        Board b = new Board();
        fillFull(b);
        // remove numbers randomly until we reach blanksTarget, ensuring uniqueness
        List<int[]> cells = new ArrayList<>();
        for (int r=0;r<9;r++) for (int c=0;c<9;c++) cells.add(new int[]{r,c});
        Collections.shuffle(cells, rnd);

        int removed = 0;
        for (int[] rc : cells) {
            if (removed >= blanksTarget) break;
            int r = rc[0], c = rc[1];
            int backup = b.get(r,c);
            b.clear(r,c);

            Solver s = new Solver(2); // stop if more than 1 solution
            int count = s.countSolutions(b.copy());
            if (count != 1) {
                // not unique -> revert
                b.set(r,c, backup);
            } else {
                removed++;
            }
        }
        return b;
    }

    /** Fill a board completely with a valid solution using randomized backtracking. */
    private boolean fillFull(Board b) {
        List<int[]> cells = new ArrayList<>();
        for (int r=0;r<9;r++) for (int c=0;c<9;c++) cells.add(new int[]{r,c});
        return fillBacktrack(b, 0, cells);
    }

    private boolean fillBacktrack(Board b, int idx, List<int[]> cells) {
        if (idx >= cells.size()) return true;
        int r = cells.get(idx)[0], c = cells.get(idx)[1];
        if (b.get(r,c) != 0) return fillBacktrack(b, idx+1, cells);

        List<Integer> vals = new ArrayList<>();
        for (int v=1; v<=9; v++) vals.add(v);
        Collections.shuffle(vals, rnd);
        Solver quick = new Solver();
        // We will attempt using quick checking by applying values and checking conflicts
        for (int v : vals) {
            // quick check using temporary marks
            b.set(r,c,v);
            if (Solver.isValidBoard(b)) {
                if (fillBacktrack(b, idx+1, cells)) return true;
            }
            b.clear(r,c);
        }
        return false;
    }

    public static int difficultyToBlanks(String diff) {
        switch ((diff==null?"":diff.toLowerCase())) {
            case "easy": return 35;   // ~46 filled
            case "medium": return 45; // ~36 filled
            case "hard": return 55;   // ~26 filled
            default: return 45;
        }
    }
}
