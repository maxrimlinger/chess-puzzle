# Chess Puzzle
This is a chess puzzle game with a built in solver/hint system. It can be run on the command line with a plain text user interface (PTUI) or with a graphical user interface (GUI). It was built as a class project for Computer Science II, so certain parts of files are authored by the Rochester Institute of Technology Computer Science department. The implementation and most of the source code was authored by me.

The objective of the game is to capture pieces until there is only piece remaining. Every move must result in a capture and pieces can only move according to the rules of chess.
## How to Use
### PTUI
The plain text user interface has two components: a playable puzzle and a solver. To run the playable puzzle, run the `ChessPTUI` class and call it with the relative path to the data file you would like to load.
#### Controls
- `h(int)` - get a hint on the next move
- `l(oad) filename` - load a new data file
- `s(elect) r c` - select the cell at row `r` and column `c`
- `q(uit)` - quit the game
- `r(eset)` - reset the current game
To capture a piece, select the piece you wish to capture with, then select the piece you wish to capture.
#### Solver
To run the solver, run the `Chess` class and call it with the relative path to the data file you would like to load. It will print out the solution to the puzzle with the least number of moves.
### GUI
The GUI allows you to move pieces with your mouse by clicking the piece you wish to capture with, then clicking the piece you wish to capture. To run the GUI, run the `ChessGUI` class and call it with the relative path to the data file you would like to load.
## Data File Format
The chess puzzle game loads a starting position from a data file. This datafile is a text file with the first line defining number of rows and columns in the grid and the subsequent lines determining where the pieces are on the board. An empty space is denoted by a `.` (period) and a piece is denoted by a capital letter corresponding to algebraic chess notation. An example is provided below, and more can be found in the `data/` directory.
```
4 4
B . P K
N . . P
. . P Q
R . . P
```
## How Does the Solver Work?
The solver/hint system uses the Breadth First Search (BFS) algorithm to find the lowest number of moves needed to get to a solved state. It explores every possible move in each position, branching out until it reaches a position that is considered solved.

## Directory Guide
`data` - sample data files to load
`input` - sample user input that can be routed into the `ChessPTUI`
`output` - sample output for both the playable PTUI and the standalone solver
`src` - source code