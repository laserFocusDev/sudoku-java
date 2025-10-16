import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SolveTest {

    @Test
    public void testEasyPuzzle() {
        int[][] board = SudokuFileLoader.load("samples/easy.txt");
        SudokuSolver solver = new SudokuSolver(board);
        assertTrue(solver.solve(), "Easy puzzle should be solvable");
        assertTrue(solver.isSolved(), "Board should be fully solved");
    }

    @Test
    public void testInvalidPuzzle() {
        int[][] invalidBoard = {
            {1,1,0,0,0,0,0,0,0}, // duplicate 1 in row
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0}
        };
        SudokuSolver solver = new SudokuSolver(invalidBoard);
        assertFalse(solver.solve(), "Invalid puzzle should not be solvable");
    }

    @Test
    public void testAlreadySolved() {
        int[][] board = SudokuFileLoader.load("samples/easy.txt");
        SudokuSolver solver = new SudokuSolver(board);
        solver.solve();
        assertTrue(solver.isSolved());
        // Solve again, should remain solved
        assertTrue(solver.solve());
    }
}
