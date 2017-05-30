import java.util.ArrayList;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;


// Represents a utilities class
class Utilities {
    // takes in a coordinate position and scales it so that it lies on the grid
    int adjustCoord(int position) {
        return (position * Cell.SIZE) + (Cell.SIZE / 2);
    }
    
    // grab the first non null value in the list 
    int getNonNull(ArrayList<Integer> list) {
        for (Integer number : list) {
            if (number != null)
                return number;
        }
        return -1;
    }
    
    // takes in a list of integers and returns the largest one
    int max(ArrayList<Integer> list) {
        int result = this.getNonNull(list);
        for (Integer number : list) {
            if (number != null) {
                if (number > result) {
                    result = number;
                }
            }
        }
        return result;
    }
    
    // takes in a list of integers and returns the smallest one
    int min(ArrayList<Integer> list) {
        int result = this.getNonNull(list);
        for (Integer number : list) {
            if (number != null) {
                if (number < result) {
                    result = number;
                }
            }
        }
        return result;
    }
    
    // takes in a list of integers and looks for the value 1000 in it,
    // if it is found then its index is returned otherwise, -1 is returned
    int find1000(ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null)
                if (list.get(i) == 1000)
                    return i;
        }
        return -1;
    }
    
    // this method takes in a list of integers and an integer
    // and returns at which index that integer is found
    int getIndex(ArrayList<Integer> list, int best) {
        int i = 0;
        for (Integer number : list) {
            if (number != null) {
                if (number == best) {
                    break;
                }
                else {
                    i++;
                }
            }
            else
                i++;
        }
        return i;
    }
    
    // this method takes in a list of integers and sums them up
    int add(ArrayList<Integer> list) {
        int result = 0;
        for (Integer number : list)
            if (number!=null)
                result = result + number;
        return result;
    }
   
    // given an operation as a string and an index, this method
    // returns the new index
    int getNewIndex(int index, String op) {
        
        if (op == null) {}
        else if (op == "+") {
            index++;
        }
        else if (op == "-") {
            index--;
        }
        return index;
    }    
}

// Represents a connect 4 disk
class Disk {
    
    // integer from 1 to 2, based on which player
    boolean isPlayer1;
    // the color of the disk based on the player
    Color color;
    // the position of the disk
    Posn posn;
    // is this disk part of the solution set
    boolean partOfSolution;
    
    // initializes disk
    Disk(boolean isPlayer1) {
        this.isPlayer1 = isPlayer1;
        this.color = this.getColor();
        this.partOfSolution = false;
    }
    
    // returns the color based on the player
    Color getColor() {
        if (this.isPlayer1) {
            return Color.BLACK;
        }
        else {
            return Color.RED;
        }
    }
    
    // drops the disk and takes in the coordinate that it falls to
    void dropDisk(Cell target) {
        if (this.posn.y == target.location.y) {
            target.currentDisk = this;
            target.occupied = true;
        }
        else {
            this.posn.y = this.posn.y + 10;
        }
    }
    
    // draws the disk
    void drawDisk(WorldScene scene) {
        CircleImage disk = new CircleImage(Cell.SIZE / 2, OutlineMode.SOLID, this.color);
        scene.placeImageXY(disk, this.posn.x, this.posn.y);
    }
    
    // initializes the disk position
    void initializePosition(Cell target) {
        this.posn = new Posn(target.location.x, Cell.SIZE / 2);
    }
}

// Represents a cell
class Cell {
    // Variable containing the length of the side of a cell
    public static int SIZE = 80;
    
    // Where this cell is located
    Posn location;
    
    // Is this cell occupied?
    boolean occupied;
    
    // Cells border
    WorldImage border = new RectangleImage(SIZE, SIZE, OutlineMode.OUTLINE, Color.BLACK);
    
    // Represents the current disk that is on this cell
    Disk currentDisk;
    
    // row
    int row;
    
    // column
    int column;
    
    // initializes cell
    Cell(Posn location, int column, int row) {
        this.location = location;
        this.occupied = false;
        this.currentDisk = null;
        this.column = column;
        this.row = row;
    }
}


// Represents the board 
class Board extends World {
    // The board width
    public static int WIDTH = 7;
    public static int HEIGHT = 6;
    
    // do we want to play with the computer??
    boolean computer = true;
    
    // Array list with with each ArrayList<Cell> being a column !!!
    ArrayList<ArrayList<Cell>> board;
    
    // Can you click on the board or is an event occurring?
    boolean canClick;
    
    // Whose turn is it?
    boolean isPlayer1;
    
    // target cell for a falling disk
    // this has a null value when there is no target
    Cell targetCell;
    
    // the current falling disk
    Disk currentFallingDisk;
    
    // is there a four in a row on the board
    boolean fourInRow;
    
    // initialize the board
    Board() {
        this.board = this.initializeBoard();
        this.canClick = true;
        this.isPlayer1 = true;
        this.targetCell = null;
        this.fourInRow = false;
    }
    
    // gets the end screen
    WorldScene getEndScreen() {
        WorldScene scene = this.getEmptyScene();
        String whoWon = " Won!";
        if (this.isPlayer1) {
            whoWon = "You " + whoWon;
        }
        else {
            whoWon = "The Computer " + whoWon;
        }
        TextImage endImage = new TextImage(whoWon, 50, Color.BLACK);
        scene.placeImageXY(endImage, ((Board.HEIGHT * Cell.SIZE) / 2) + (Cell.SIZE / 2), (Board.WIDTH * Cell.SIZE) / 2);
        return scene;
    }
   
    
    // initializes the board using width and height
    ArrayList<ArrayList<Cell>> initializeBoard() {
        
        Utilities utils = new Utilities();
        ArrayList<ArrayList<Cell>> board = new ArrayList<ArrayList<Cell>>();
        
        for (int i = 0; i < WIDTH; i++) {
            ArrayList<Cell> column = new ArrayList<Cell>();
            for (int j = 0; j < HEIGHT; j++) {
                column.add(new Cell(new Posn(utils.adjustCoord(i), utils.adjustCoord(j)), i, j));
            }
            board.add(column);
        }
        return board;
    }
    
   // draws the board on the passed in scene
   void drawBoard(WorldScene scene) {
       for (ArrayList<Cell> column: this.board) {
           for (Cell cell: column) {
               scene.placeImageXY(cell.border, cell.location.x, cell.location.y);
           }
       }
   }
   
   // draws all the disks in the game onto the scene
   void drawDisks(WorldScene scene) {
       for (ArrayList<Cell> column : this.board) {
           for (Cell cell : column) {
               if (cell.currentDisk != null) {
                   cell.currentDisk.drawDisk(scene);
               }
           }
       }
   }
   
   // draws the portion that displays which player's turn it is
   void drawPlayerTurn(WorldScene scene) {
       String playerString = "Turn: ";
       if (this.isPlayer1) {
           playerString = playerString + "You";
       }
       else {
           playerString = playerString + "The Computer";
       }
       
       scene.placeImageXY(new TextImage(playerString, 30, Color.BLACK),
               (Board.WIDTH * Cell.SIZE) / 2 ,(Board.HEIGHT + 1) * Cell.SIZE);
   }
   
   // returns whether the click was within bounds
   boolean withinBounds(Posn posn) {
       boolean checkX = (posn.x > 0 && posn.x < Board.WIDTH * Cell.SIZE);
       boolean checkY = (posn.y > 0 && posn.y < Board.HEIGHT * Cell.SIZE);
       
       return checkX && checkY;
   }
   
   // returns the column that was clicked on by the player
   ArrayList<Cell> findColumn(Posn posn) {
      
       int index = (posn.x - (Cell.SIZE / 16)) / Cell.SIZE;
       return this.board.get(index);
   }
   
   // finds the nearest occupied cell
   Cell findNearestUnoccupiedCell(ArrayList<Cell> column) {
       
       for (int i = 0; i < column.size(); i++) {
           if (i == column.size() - 1) {
               return column.get(i);
           }
           else if (column.get(i).occupied && i == 0) {
               return null;
           }
           else {
               if (column.get(i + 1).occupied) {
                   return column.get(i);
               }
           }
       }
       // if non are unoccupied return null
       return null;
   }
   
   // makes the world scene
   public WorldScene makeScene() {
       WorldScene scene = this.getEmptyScene();
       if (this.currentFallingDisk != null) {
           this.currentFallingDisk.drawDisk(scene);
       }
       this.drawDisks(scene);
       this.drawBoard(scene);
       this.drawPlayerTurn(scene);
       return scene;
   }
   
   // handles mouse events
   public void onMousePressed(Posn posn) {
       if (!this.canClick || !this.withinBounds(posn)) {
           return;
       }
       ArrayList<Cell> chosenColumn = this.findColumn(posn);
       Cell nearestUnoccupiedCell = this.findNearestUnoccupiedCell(chosenColumn);
       if (nearestUnoccupiedCell == null) {
           return;
       }
       this.targetCell = nearestUnoccupiedCell;
       this.currentFallingDisk = new Disk(this.isPlayer1);
       this.currentFallingDisk.initializePosition(this.targetCell); 
       this.canClick = false;
   }
 
   // the on tick function that renders the game
   public void onTick() {
       
       if (!this.isPlayer1 && this.targetCell == null) {
           new Computer(this).makeMove();
           return;
       }
       
       if (this.targetCell == null) {
              return;
       }
           
       this.currentFallingDisk.dropDisk(this.targetCell);
       if (this.targetCell.currentDisk == this.currentFallingDisk) {
           this.fourInRow(this.targetCell);
           
           this.targetCell = null;
           this.currentFallingDisk = null;
           if (this.fourInRow) {
               return;
           }
           this.isPlayer1 = !this.isPlayer1;
           this.canClick = true;
       }
   }
   
   // traverses the sequence that is described by the passed in arguments
   void traverseSequence(Cell targetCell, int i, int j, String opi, String opj,
           ArrayList<Cell> potential, Color colorToFind) {
       
       Utilities utils = new Utilities();
       boolean foundOpposite = false;
       while (!foundOpposite) {
           if (i > this.board.get(0).size() - 1 || j > this.board.size() - 1 || i < 0 || j < 0) {
               break;
           }
           
           if (this.board.get(j).get(i).occupied) {
               if (this.board.get(j).get(i).currentDisk.color.equals(colorToFind)) {
                   potential.add(this.board.get(j).get(i));
               
                   i = utils.getNewIndex(i, opi);
                   j = utils.getNewIndex(j, opj);
                     
               }
               else {
                   foundOpposite = !foundOpposite;
               }
           }
           else {
               foundOpposite = !foundOpposite;
           }
       }
   }
   
   // Checks for any four in a row solutions
   int lookForFour(Cell targetCell, String op1i, String op1j, String op2i, String op2j) {
       Color colorToFind = targetCell.currentDisk.color;
       ArrayList<Cell> potential = new ArrayList<Cell>();
       Utilities utils = new Utilities();
       
       int i = targetCell.row;
       int j = targetCell.column;
     
       this.traverseSequence(targetCell, i, j, op1i, op1j, potential, colorToFind);
      
       i = utils.getNewIndex(targetCell.row, op2i);
       j = utils.getNewIndex(targetCell.column, op2j);
       
       this.traverseSequence(targetCell, i, j, op2i, op2j, potential, colorToFind);
        
       return potential.size();
   }
   
   // check if there are any vertical solutions
   int lookVertical(Cell targetCell) {
       return this.lookForFour(targetCell, "+", null, "-", null);
   }
   
   // check if there are any horizontal solutions
   int lookHorizontal(Cell targetCell) {
       return this.lookForFour(targetCell, null, "+", null, "-");
   }
   
   // check if there are any diagonal solutions (with a negative slope)
   int lookDiagonalLeft(Cell targetCell) {
       return this.lookForFour(targetCell, "+", "+", "-", "-");
   }
   
   // check if there are any diagonal solutions (with a positive slope)
   int lookDiagonalRight(Cell targetCell) {
       return this.lookForFour(targetCell, "-", "+", "+", "-");
   }

   // checks if there is a four in a row after the last move and changes the 
   // fourInRow boolean in the board class if there is 
   void fourInRow(Cell targetCell) {
       if (this.lookVertical(targetCell) >= 4 || this.lookHorizontal(targetCell) >= 4 ||
          this.lookDiagonalLeft(targetCell) >= 4 || this.lookDiagonalRight(targetCell) >= 4) {
           
           this.fourInRow = true;
       }
   }
   
   // check if the world is in a state where it should end
   public WorldEnd worldEnds() {
     
       WorldScene scene = this.getEndScreen();
  
       if (this.fourInRow) {
           return new WorldEnd(true, scene);
       } else {
           return new WorldEnd(false, scene);
       }
   }
}

// class representing the computer
class Computer {
    Board game;
    Color color;
    ArrayList<ArrayList<Cell>> board;
    Utilities utils = new Utilities();
    
    // initialize the computer
    Computer(Board game) {
        this.game = game;
        this.board = this.game.board;
        this.color = Color.RED;
    }
    
    // take the number of pieces in a row that was found and evaluate the 
    // the score for that collection of moves
    int evaluateOutcome(boolean isComp, int maxValue) {
        int result;
        if (maxValue >= 4) {
            result = 1000;
        }
        else {
            result = 0;
        }
        if (!isComp)
            result = result * -1;
    
        return result;
    }
    
    // make the move that the AI decided to do
    void makeMove() {
        ArrayList<Cell> clickOn = this.computerTurn(2);
        this.game.targetCell = this.game.findNearestUnoccupiedCell(clickOn);
        this.game.currentFallingDisk = new Disk(this.game.isPlayer1);
        this.game.currentFallingDisk.initializePosition(this.game.targetCell); 
        this.game.canClick = false;
    }
   
    // evaluate all possible scenarios by traversing the tree until depth
    // is zero and return a list of scenarios (which are integer values)
    ArrayList<Integer> evaluateScenarios(int depth, boolean isPlayer) {
        ArrayList<Integer> scenarios = new ArrayList<Integer>();
        for (ArrayList<Cell> column : this.game.board) {
            Cell targetCell = this.game.findNearestUnoccupiedCell(column);
            if (targetCell != null) {
                targetCell.occupied = true;
                targetCell.currentDisk = new Disk(isPlayer);
                ArrayList<Integer> howMany = new ArrayList<Integer>();
                howMany.add(this.game.lookVertical(targetCell));
                howMany.add(this.game.lookHorizontal(targetCell));
                howMany.add(this.game.lookDiagonalLeft(targetCell));
                howMany.add(this.game.lookDiagonalRight(targetCell));
                int total = this.utils.max(howMany);
                int score = this.evaluateOutcome(!isPlayer, total);
                if ((score == 1000 && !isPlayer) || (score == -1000 && isPlayer) || (depth == 0 && isPlayer))
                    scenarios.add(score);
                else {
                    if (isPlayer) 
                        scenarios.add(this.computerTurnHelp(depth - 1));
                    else
                        scenarios.add(this.playerTurn(depth));
                }
                targetCell.occupied = false;
                targetCell.currentDisk = null;
            }
            else
                scenarios.add(null);
        }
        return scenarios;
    }
    
    // evaluate the move that the computer should do and return the column that 
    // the computer will drop it's disk in
    ArrayList<Cell> computerTurn(int depth) {
        // evaluate all possible scenarios that can occur and return them
        ArrayList<Integer> scenarios = this.evaluateScenarios(depth, false);
        
        // print out the scenarios to physically see the computer making it's decisions
        System.out.println(scenarios);
        
        // try to find a 1000, if it doesn't exist then find and return the index
        // of the greatest value in the scenarios list
        int index = this.utils.find1000(scenarios);
        if (index == -1) {
            int bestMove = this.utils.max(scenarios);
            index = this.utils.getIndex(scenarios, bestMove);
        }
 
        // return the column that the computer decided to drop it's disk
        return this.game.board.get(index);
    }
    
    // determine what potential moves the player can make and their outcomes
    // and then return their aggregated score
    int playerTurn(int depth) {
        ArrayList<Integer> scenarios = this.evaluateScenarios(depth, true);
        return this.utils.add(scenarios);
    }
    
    // evaluate the possible moves that the computer can make and their outcomes
    // and then return their aggregated score
    int computerTurnHelp(int depth) {
        ArrayList<Integer> scenarios = this.evaluateScenarios(depth, false);
        return this.utils.add(scenarios);
    }    
}

class StartGame {
    
    // main method used to run the MazeGame
    public static void main(String[] argv) {
        Board w = new Board();
        w.bigBang(Board.WIDTH * Cell.SIZE,
                (Board.HEIGHT + 2) * Cell.SIZE, .0075); 
    } 
}
