package adam.javasudoku;

import java.util.List;
import java.util.ArrayList;

public class Sudoku {
  
  private final List<Integer> digits;
  private final List<Character> rows;
  private final List<Character> cols; 
  private final List<List<Integer>> unitList;
  private final List<List<List<Integer>>> units;
  private final List<List<Integer>> peers;
  private final Solver solver;
  
  private int dimensions;
  private List<Integer> squares;
  private Solution solution;
  
  private String testGrid = "4 . . . . . 8 . 5 . 3 . . . . . . . . . . 7 . . . . . . 2 . . . . . 6 . . . . . 8 . 4 . . . . . . 1 . . . . . . . 6 . 3 . 7 . 5 . . 2 . . . . . 1 . 4 . . . . . .";
  private String multiGrid = ". . . . . 6 . . . . 5 9 . . . . . 8 2 . . . . 8 . . . . 4 5 . . . . . . . . 3 . . . . . . . . 6 . . 3 . 5 4 . . . 3 2 5 . . 6 . . . . . . . . . . . . . . . . . .";
  private String blank;
  
  public Sudoku() {
    digits = new ArrayList<Integer>();
    rows = new ArrayList<Character>();
    cols = new ArrayList<Character>(); 
    unitList = new ArrayList<List<Integer>>();
    units = new ArrayList<List<List<Integer>>>();
    peers = new ArrayList<List<Integer>>();
    solver = new Solver(this);
  }
  
  public Sudoku initialize() {
    initialize(3);
    return this;
  }
  
  public Sudoku initialize(int dimensions){
    //Initializing the private fields
    
    this.dimensions = dimensions; //Default is 3 unless later changed
    digits.clear();
    for (int i = 0; i < (int)Math.pow(dimensions, 2); i++) {
      digits.add(i + 1); //Initializing the list of possible digits
    }
    rows.clear();
    cols.clear();
    for (int i = 97; i < (97 + (int)Math.pow(dimensions, 2)); ++i) {
      rows.add((char) i); //Initializing rows and cols
      cols.add((char) i);
    }
    
    squares = initSquares(rows, cols);
    assert squares.size() == (int)Math.pow(dimensions, 4);
    initUnitList();
    assert unitList.size() == (int)Math.pow(dimensions, 2)*3;
    initUnits();
    assert units.get(squares.get(25)).size() == 3; //Cell chosen arbitrarily
    initPeers();
    assert peers.get(squares.get(50)).size() == 3*((int)Math.pow(dimensions, 2) - 1) - 2*(dimensions-1); //Cell chosen arbitrarily
    
    for (blank = "."; blank.length() < (int)Math.pow(dimensions, 4); blank += " .") {
      //Initializing the blank string/grid
    }
    return this;
  }

  public int getDimensions() {
    return dimensions;
  }
  
  public List<Integer> getDigits() {
    return digits;
  }
  
  public List<Character> getRows() {
    return rows;
  }
  
  public List<Character> getCols() {
    return cols;
  }
  
  public List<Integer> getSquares() {
    return squares;
  }
  
  public List<List<Integer>> getUnitList() {
    return unitList;
  }
  
  public List<List<List<Integer>>> getUnits() {
    return units;
  }
  
  public List<List<Integer>> getPeers() {
    return peers;
  }
  
  public Solution getSolution() {
    if (solution == null) {
      solution = new Solution(this);
    }
    return solution;
  }
  
  public void setSolution(Solution solution) {
    this.solution = solution;
  }
  
  public Solver getSolver() {
    return solver;
  }
  
  public String getGrid(String grid) {
    if (grid == "test") {
      return testGrid;
    } else if (grid == "multi") {
      return multiGrid;
    } else {
      return blank;
    }
  }
  
  protected List<Cell> initCells() {
    List<Cell> retCells = new ArrayList<Cell>();
    for (char row : rows) {
      for (char col : cols) {
        retCells.add(new Cell(this, "" + row + col));
      }
    }  
    return retCells; 
  }
  
  protected List<Cell> initCells(List<Character> rows, List<Character> cols, List<Cell> toCells, List<Cell> fromCells) {
    for (char row : rows) {
      for (char col : cols) {
        toCells.add(fromCells.get(this.rows.indexOf(row)*(int)Math.pow(dimensions, 2) + this.cols.indexOf(col)));
      }
    }  
    return toCells; 
  }  
  
  private final List<Integer> initSquares(List<Character> rows, List<Character> cols) {
    List<Integer> retSquares = new ArrayList<Integer>();
    for (char row : rows) {
      for (char col : cols) {
        retSquares.add(this.rows.indexOf(row)*(int)Math.pow(dimensions, 2) + this.cols.indexOf(col));
      }
    }  
    return retSquares;
  }

  /**
   * Initialize the list of units, starting with square units, listing row by row.
   * Then, adding the each of the horizontal units.
   * Last, followed by each of the vertical units. 
   */
  private final void initUnitList() {
    unitList.clear();
    for (int row = 0; row < dimensions; ++row) {
      for (int col = 0; col < dimensions; ++col) {
        unitList.add(initSquares(rows.subList(row*dimensions, (row + 1)*dimensions), cols.subList(col*dimensions, (col+1)*dimensions)));
      }
    }    
    for (int row = 0; row < rows.size(); ++row) {
      unitList.add(initSquares(rows.subList(row, row+1), cols));
    }      
    for (int col = 0; col < cols.size(); ++col) {
      unitList.add(initSquares(rows, cols.subList(col, col+1)));
    }
  }
  
  private final void initUnits() {
    units.clear();
    for (int square : squares) {
      units.add(new ArrayList<List<Integer>>());
      for (List<Integer> unit : unitList) {
        if (unit.contains(square)) {
          units.get(square).add(unit);
          if (units.get(square).size() == 3) {
            break;
          }
        }
      }
    }    
  }
  
  private final void initPeers() {
    peers.clear();
    for (int square : squares) {
      peers.add(square, new ArrayList<Integer>());
      for (List<Integer> unit : units.get(square)) {
        for (int unitSquare : unit) {
          if (unitSquare != square && !peers.get(square).contains(unitSquare)) {
            peers.get(square).add(unitSquare);
          }
        }  
      }
    }
  }
}
