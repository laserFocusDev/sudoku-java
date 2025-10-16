# Sudoku Solver & Generator (Java)

A compact Java Swing project demonstrating:
- Backtracking recursion with basic optimization
- Sudoku puzzle generation with unique-solution checking
- A Swing GUI to input, validate, solve and generate puzzles

## What’s included
- `src/sudoku/` — all Java source code (Board, Solver, Generator, GUI)
- `samples/` — sample puzzles (easy/medium/hard)
- `tests/SolveTest.java` — quick console test
- `build.sh` / `run.sh` — optional convenience scripts

## Features
- Solve any 9×9 Sudoku (backtracking with heuristic)
- Generate puzzles (difficulty: easy/medium/hard)
- GUI: enter puzzle, Solve, Clear, Generate, Check validity
- Solver validates inputs and shows "No solution" when appropriate

## How to build & run (plain javac)
```bash
# from project root
javac -d out src/sudoku/*.java
java -cp out sudoku.Main
