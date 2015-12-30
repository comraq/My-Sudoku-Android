package adam.javasudoku;

import java.util.List;
import java.util.ArrayList;

public class Cell implements Cloneable {

  private List<Integer> values;
  private String name;
  private Sudoku sudoku;
  
  public Cell (Sudoku sudoku, String name) {
    this.sudoku = sudoku;
    values = new ArrayList<Integer>();
    this.name = name;
  }
  
  @Override
  protected Cell clone() throws CloneNotSupportedException {
    Cell cloned = (Cell)super.clone();
    cloned.setValues(new ArrayList<Integer>(values));
    return cloned;
  }
  
  public void initValues() {
    int size = sudoku.getDimensions();
    values.clear();
    for (int i = 0; i < (int)Math.pow(size, 2); i++) {
      values.add(i + 1);
    }   
  }
  
  public void setValues(List<Integer> values) {
    this.values = values; 
  }
  
  public void setValue(int val) {
    values.clear();
    values.add(val);
  }  
  
  public List<Integer> getValues() {
    return values;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  @Deprecated
  public Cell withSudoku(Sudoku sudoku) {
    this.sudoku = sudoku;
    return this;
  }
}
