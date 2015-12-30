package adam.javasudoku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SudokuGameActivity extends AppCompatActivity {

  private final static int BLOCK_SEPARATOR = Color.parseColor("#6ced38");
  private final static int GRID_LINES = Color.parseColor("#96dfe1");

  private int dimensions;
  private GridLayout myGridLayout;
  private GridLayout.LayoutParams gridParams, cellParams;
  private Map<Integer, CellTextView> textCells;

  private Button generateButton, resetButton, hintButton, checkButton;

  private Sudoku sudoku;

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

    sudoku = new Sudoku().initialize();

    initialize();
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
        createEditTexts(grid, r * (int) Math.pow(dimensions, 3) + c * dimensions);
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
        textCells.put(squareNum + r * (int) Math.pow(dimensions, 2) + c, textCell);
      }
    }
  }

  private void initialize() {
    //Initializing fields in MainActivity
    dimensions = sudoku.getDimensions();

    textCells = new HashMap<Integer, CellTextView>();

    myGridLayout = (GridLayout) findViewById(R.id.test_grid_layout);
    myGridLayout.setRowCount(dimensions);
    myGridLayout.setColumnCount(dimensions);

    generateButton = (Button) findViewById(R.id.generate_button);
    resetButton = (Button) findViewById(R.id.reset_button);
    hintButton = (Button) findViewById(R.id.hint_button);
    checkButton = (Button) findViewById(R.id.check_button);

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
}
