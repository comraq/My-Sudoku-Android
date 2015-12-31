package adam.javasudoku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class SudokuGameActivity extends AppCompatActivity implements View.OnClickListener, Observer {

  private final static int BLOCK_SEPARATOR = Color.parseColor("#6ced38");
  private final static int GRID_LINES = Color.parseColor("#96dfe1");

  private GridLayout myGridLayout;
  private GridLayout.LayoutParams gridParams, cellParams;
  private Map<Integer, CellTextView> textCells;

  private Button generateButton, resetButton, hintButton, checkButton;

  private Sudoku sudoku;
  private int dimensions;
  private List<Integer> hintSquares;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sudoku_game);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(textView);*/

    sudoku = new Sudoku().initialize(4);
    initialize();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.game_generate_button:
        createGrid(v);
        fillGrid(Solver.CHALLENGE);
        break;
      case R.id.game_reset_button:
        resetGrid();
        break;
      case R.id.game_hint_button:
        testToastShort(v);
        break;
      case R.id.game_check_button:
        break;
      default:
        Toast.makeText(this, "Unidentified button! id: " + v.getId(), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void update(Observable observable, Object data) {

  }

  public void testToastShort(View view) {
    String msg = "Testing short toast with button: " + ((Button) view).getText().toString();
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  public void createGrid(View view) {
    myGridLayout.setBackgroundColor(BLOCK_SEPARATOR);

    GridLayout grid;
    for (int r = 0; r < dimensions; ++r) {
      gridParams.rowSpec = GridLayout.spec(r, 1.0f);
      for (int c = 0; c < dimensions; ++c) {
        grid = new GridLayout(this);

        grid.setRowCount(dimensions);
        grid.setColumnCount(dimensions);
        grid.setBackgroundColor(GRID_LINES);

        gridParams.columnSpec = GridLayout.spec(c, 1.0f);
        myGridLayout.addView(grid, new GridLayout.LayoutParams(gridParams));
        createEditTexts(grid, r * (int) (Math.pow(dimensions, 3) + 0.5) + c * dimensions);
      }
    }
  }

  private void createEditTexts(GridLayout grid, int squareNum) {
    CellTextView textCell;
    int maxLength = (dimensions > 3)? 2 : 1;

    for (int r = 0; r < dimensions; ++r) {
      cellParams.rowSpec = GridLayout.spec(r, 1.0f);
      for (int c = 0; c < dimensions; ++c) {
        cellParams.columnSpec = GridLayout.spec(c, 1.0f);

        textCell = new CellTextView(this, maxLength);
        grid.addView(textCell, new GridLayout.LayoutParams(cellParams));
        textCells.put(squareNum + r * (int) (Math.pow(dimensions, 2) + 0.5) + c, textCell);
      }
    }
  }

  private void fillGrid(String diff) {
    try {
      sudoku.setSolution(sudoku.getSolver().generate(diff));
      List<Cell> cells = sudoku.getSolution().getCells();

      for (int i = 0; i < cells.size(); ++i) {
        CellTextView textCell = textCells.get(i);
        textCell.setText("");
        if (!cells.get(i).getValues().isEmpty()) {
          textCell.setText(Integer.toString(cells.get(i).getValues().get(0)));
          textCell.setEnabled(false);
        } else {
          textCell.setEnabled(true);
        }
      }
    } catch (CloneNotSupportedException e) {
      Toast.makeText(this, R.string.solver_generate_error, Toast.LENGTH_LONG).show();
    }
    hintSquares = new ArrayList<Integer>(sudoku.getSquares());
  }

  private void resetGrid() {
    List<Cell> cells = sudoku.getSolution().getCells();
    for (int i = 0; i < cells.size(); ++i) {
      if (cells.get(i).getValues().isEmpty()) {
        textCells.get(i).setText("");
        textCells.get(i).setBackgroundColor(CellTextView.UNCHECK);
      }
    }
    hintSquares = new ArrayList<Integer>(sudoku.getSquares());
  }

  private void initialize() {
    //Initializing fields in MainActivity
    dimensions = sudoku.getDimensions();

    textCells = new HashMap<Integer, CellTextView>();

    myGridLayout = (GridLayout) findViewById(R.id.game_grid_layout);
    myGridLayout.setRowCount(dimensions);
    myGridLayout.setColumnCount(dimensions);

    generateButton = (Button) findViewById(R.id.game_generate_button);
    resetButton = (Button) findViewById(R.id.game_reset_button);
    hintButton = (Button) findViewById(R.id.game_hint_button);
    checkButton = (Button) findViewById(R.id.game_check_button);

    generateButton.setOnClickListener(this);
    resetButton.setOnClickListener(this);
    hintButton.setOnClickListener(this);
    checkButton.setOnClickListener(this);

    setActivityTitle();
    initCellParams();
  }

  private void initCellParams() {
    gridParams = new GridLayout.LayoutParams();
    cellParams = new GridLayout.LayoutParams();

    gridParams.setMargins(2, 2, 2, 2);
    cellParams.setMargins(2, 2, 2, 2);
  }

  private void setActivityTitle() {
    this.setTitle(dimensions + "x" + dimensions + " Sudoku");
  }

  private enum SudokuState {
    INIT("Press Generate to Select a Sudoku"),
    GENERATED("Generated a New Sudoku"),
    RESETTED("Here is the Original Sudoku"),
    PLAYING("Playing"),
    SOLVED("All Solved!");

    private String theMessage;

    SudokuState(String m) {
      theMessage = m;
    }

    String message() {
      return theMessage;
    }
  }

  private class SudokuGame extends Observable {
    private SudokuState currentState = SudokuState.INIT;

    private void updateState(SudokuState nextState) {

    }
  }
}
