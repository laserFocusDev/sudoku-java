package sudoku;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Simple Swing GUI: 9x9 grid of JTextFields, buttons: Solve | Clear | Generate | Check
 * Also provides step-by-step visualization toggle.
 */
public class GUIController extends JFrame {

    private final JTextField[][] cells = new JTextField[9][9];
    private final Board board = new Board();
    private final Solver solver = new Solver();
    private final Generator generator = new Generator();

    private final JButton solveBtn = new JButton("Solve");
    private final JButton clearBtn = new JButton("Clear");
    private final JButton genBtn = new JButton("Generate (Medium)");
    private final JButton checkBtn = new JButton("Check");
    private final JCheckBox visualize = new JCheckBox("Visualize Step-by-step");

    public GUIController() {
        setTitle("Sudoku Solver & Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        top.add(solveBtn);
        top.add(clearBtn);
        top.add(genBtn);
        top.add(checkBtn);
        top.add(visualize);
        add(top, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(3,3,4,4));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        for (int br=0;br<3;br++) {
            for (int bc=0; bc<3; bc++) {
                JPanel box = new JPanel(new GridLayout(3,3));
                box.setBorder(new LineBorder(Color.BLACK, 1));
                for (int r=0;r<3;r++) {
                    for (int c=0;c<3;c++) {
                        int rr = br*3 + r;
                        int cc = bc*3 + c;
                        JTextField tf = new JTextField();
                        tf.setHorizontalAlignment(JTextField.CENTER);
                        tf.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
                        tf.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                        tf.setDocument(new Util.IntDocument(1));
                        tf.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                syncFromUI();
                            }
                        });
                        cells[rr][cc] = tf;
                        box.add(tf);
                    }
                }
                gridPanel.add(box);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        // Hook buttons
        solveBtn.addActionListener(e -> onSolve());
        clearBtn.addActionListener(e -> onClear());
        genBtn.addActionListener(e -> onGenerate());
        checkBtn.addActionListener(e -> onCheck());

        // initial sample
        loadSample(BoardSamples.sampleEasy());
        pack();
        setResizable(false);
    }

    private void syncFromUI() {
        for (int r=0;r<9;r++) for (int c=0;c<9;c++) {
            String s = cells[r][c].getText().trim();
            if (s.isEmpty()) board.set(r,c,0);
            else {
                try {
                    int v = Integer.parseInt(s);
                    if (v<1 || v>9) {
                        cells[r][c].setText("");
                        board.set(r,c,0);
                    } else {
                        board.set(r,c,v);
                    }
                } catch (NumberFormatException ex) {
                    cells[r][c].setText("");
                    board.set(r,c,0);
                }
            }
        }
    }

    private void renderToUI(Board b) {
        for (int r=0;r<9;r++) for (int c=0;c<9;c++) {
            int v = b.get(r,c);
            cells[r][c].setText(v==0 ? "" : Integer.toString(v));
        }
    }

    private void onSolve() {
        syncFromUI();
        if (!Solver.isValidBoard(board)) {
            JOptionPane.showMessageDialog(this, "Board appears invalid (conflicting numbers).", "Invalid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        solveBtn.setEnabled(false);
        genBtn.setEnabled(false);
        SwingWorker<Boolean, Board> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                Board copy = board.copy();
                boolean ok = solver.solve(copy);
                if (ok) publish(copy);
                return ok;
            }
            @Override
            protected void process(java.util.List<Board> chunks) {
                if (!chunks.isEmpty()) renderToUI(chunks.get(chunks.size()-1));
            }
            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (!ok) JOptionPane.showMessageDialog(GUIController.this, "No solution found.", "Result", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(GUIController.this, "Error during solving: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    solveBtn.setEnabled(true);
                    genBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void onClear() {
        board.clearAll();
        renderToUI(board);
    }

    private void onGenerate() {
        int blanks = Generator.difficultyToBlanks("medium");
        String[] options = {"Easy", "Medium", "Hard"};
        String sel = (String) JOptionPane.showInputDialog(this, "Select difficulty:", "Generate", JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (sel != null) {
            blanks = Generator.difficultyToBlanks(sel.toLowerCase());
        } else return;

        solveBtn.setEnabled(false);
        genBtn.setEnabled(false);
        SwingWorker<Board, Void> worker = new SwingWorker<>() {
            @Override
            protected Board doInBackground() {
                Board b = generator.generate(blanks);
                return b;
            }
            @Override
            protected void done() {
                try {
                    Board b = get();
                    board.clearAll();
                    // copy values
                    for (int r=0;r<9;r++) for (int c=0;c<9;c++) board.set(r,c,b.get(r,c));
                    renderToUI(board);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    solveBtn.setEnabled(true);
                    genBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void onCheck() {
        syncFromUI();
        boolean ok = Solver.isValidBoard(board);
        JOptionPane.showMessageDialog(this, ok ? "Board is valid so far." : "Invalid board: conflicts exist.", "Check", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void loadSample(Board b) {
        board.clearAll();
        for (int r=0;r<9;r++) for (int c=0;c<9;c++) board.set(r,c,b.get(r,c));
        renderToUI(board);
    }
}
