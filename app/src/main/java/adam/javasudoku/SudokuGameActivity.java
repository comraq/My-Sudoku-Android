package adam.javasudoku;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ThreadLocalRandom;

public class SudokuGameActivity extends AppCompatActivity implements View.OnClickListener, Observer {

  private final static int BLOCK_SEPARATOR = Color.parseColor("#6ced38");
  private final static int GRID_LINES = Color.parseColor("#96dfe1");

  private Button generateButton, resetButton, hintButton, checkButton;
  private TextView statusTextView;
  private GridLayout myGridLayout;
  private GridLayout.LayoutParams gridParams, cellParams;
  private SparseArray<CellTextView> textCells;

  private SudokuGame sudokuGame;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sudoku_game);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(textView);*/

    initialize();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.game_generate_button:
        generate();
        break;
      case R.id.game_reset_button:
        reset();
        break;
      case R.id.game_hint_button:
        sudokuGame.hint();
        break;
      case R.id.game_check_button:
        sudokuGame.check();
        break;
      default:
        Toast.makeText(this, "Unidentified button! id: " + v.getId(), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void update(Observable observable, Object data) {
    SudokuState state = sudokuGame.currentState;
    statusTextView.setText(sudokuGame.currentState.message());
    switch (state) {
      case INIT:
        resetButton.setEnabled(false);
        checkButton.setEnabled(false);
        hintButton.setEnabled(false);
        break;
      case GENERATED:
      case RESETTED:
        resetButton.setEnabled(false);
        checkButton.setEnabled(false);
        hintButton.setEnabled(true);
        break;
      case PLAYING:
        resetButton.setEnabled(true);
        checkButton.setEnabled(true);
        hintButton.setEnabled(true);
        uncheckTextCells();
        break;
      case SOLVED:
        resetButton.setEnabled(true);
        checkButton.setEnabled(false);
        hintButton.setEnabled(false);
        gameWon();
        break;
      default:
        Toast.makeText(this, "Undefined state!", Toast.LENGTH_SHORT).show();
    }
  }

  private void generate() {
    createGrid();
    sudokuGame.fillGrid(Solver.CHALLENGE);
  }

  private void createGrid() {
    myGridLayout.setBackgroundColor(BLOCK_SEPARATOR);

    GridLayout grid;
    for (int r = 0; r < sudokuGame.dimensions; ++r) {
      gridParams.rowSpec = GridLayout.spec(r, 1.0f);
      for (int c = 0; c < sudokuGame.dimensions; ++c) {
        grid = new GridLayout(this);

        grid.setRowCount(sudokuGame.dimensions);
        grid.setColumnCount(sudokuGame.dimensions);
        grid.setBackgroundColor(GRID_LINES);

        gridParams.columnSpec = GridLayout.spec(c, 1.0f);
        myGridLayout.addView(grid, new GridLayout.LayoutParams(gridParams));
        createEditTexts(grid, r * (int) (Math.pow(sudokuGame.dimensions, 3) + 0.5) + c * sudokuGame.dimensions);
      }
    }
  }

  private void createEditTexts(GridLayout grid, int squareNum) {
    CellTextView textCell;
    int maxLength = (sudokuGame.dimensions > 3)? 2 : 1;

    for (int r = 0; r < sudokuGame.dimensions; ++r) {
      cellParams.rowSpec = GridLayout.spec(r, 1.0f);
      for (int c = 0; c < sudokuGame.dimensions; ++c) {
        cellParams.columnSpec = GridLayout.spec(c, 1.0f);

        textCell = new CellTextView(this, maxLength);
        grid.addView(textCell, new GridLayout.LayoutParams(cellParams));
        textCells.put(squareNum + r * (int) (Math.pow(sudokuGame.dimensions, 2) + 0.5) + c, textCell);
        textCell.addTextChangedListener(sudokuGame);
      }
    }
  }

  private void uncheckTextCells() {
    CellTextView textCell;
    for (int i = 0; i < textCells.size(); ++i) {
      textCell = textCells.valueAt(i);
      if (textCell.isEnabled()) textCell.uncheck();
    }
  }

  private void gameWon() {
    List<Cell> cells = sudokuGame.sudoku.getSolution().getCells();
    for (int i = 0; i < cells.size(); ++i) {
      if (cells.get(i).getValues().isEmpty()) {
        textCells.get(i).setEnabled(false);
        textCells.get(i).isCorrect();
      }
    }
    AlertDialog.Builder wonDialog = new AlertDialog.Builder(this);

    wonDialog.setTitle(getString(R.string.won_dialog_title));
    wonDialog.setMessage(getString(R.string.won_dialog_message));

    wonDialog.setNegativeButton(getString(R.string.dialog_ok_button), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    wonDialog.setPositiveButton(getString(R.string.dialog_generate_button), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        generate();
      }
    });

    wonDialog.show();
  }

  private void initialize() {
    //Initializing fields in MainActivity
    sudokuGame = new SudokuGame();
    sudokuGame.addObserver(this);

    myGridLayout = (GridLayout) findViewById(R.id.game_grid_layout);
    myGridLayout.setRowCount(sudokuGame.dimensions);
    myGridLayout.setColumnCount(sudokuGame.dimensions);

    generateButton = (Button) findViewById(R.id.game_generate_button);
    resetButton = (Button) findViewById(R.id.game_reset_button);
    hintButton = (Button) findViewById(R.id.game_hint_button);
    checkButton = (Button) findViewById(R.id.game_check_button);
    statusTextView = (TextView) findViewById(R.id.game_status_panel);

    generateButton.setOnClickListener(this);
    resetButton.setOnClickListener(this);
    hintButton.setOnClickListener(this);
    checkButton.setOnClickListener(this);

    setActivityTitle();
    initCellParams();
    
    sudokuGame.updateState(SudokuState.INIT);
  }

  private void initCellParams() {
    gridParams = new GridLayout.LayoutParams();
    cellParams = new GridLayout.LayoutParams();

    gridParams.setMargins(2, 2, 2, 2);
    cellParams.setMargins(2, 2, 2, 2);
  }

  private void setActivityTitle() {
    this.setTitle(sudokuGame.dimensions + "x" + sudokuGame.dimensions + " Sudoku");
  }

  private void reset() {
    AlertDialog.Builder resetDialog = new AlertDialog.Builder(this);

    resetDialog.setTitle(getString(R.string.reset_dialog_title));
    resetDialog.setMessage(getString(R.string.reset_dialog_message));

    resetDialog.setNegativeButton(R.string.dialog_yes_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        sudokuGame.resetGrid();
      }
    });

    resetDialog.setPositiveButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    resetDialog.show();
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

  private class SudokuGame extends Observable implements TextWatcher {
    private SudokuState currentState;

    private Sudoku sudoku;
    private int dimensions;
    private List<Integer> hintSquares;

    private SudokuGame () {
      sudoku = new Sudoku().initialize(3);
      dimensions = sudoku.getDimensions();
      textCells = new SparseArray<CellTextView>();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      //Nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      updateState(SudokuState.PLAYING);
    }

    @Override
    public void afterTextChanged(Editable s) {
      //Nothing
    }

    /**
     * Fills the grid with a generated sudoku based on the difficulty specified
     */
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
        Toast.makeText(SudokuGameActivity.this, R.string.solver_generate_error, Toast.LENGTH_LONG).show();
      }
      hintSquares = new ArrayList<Integer>(sudoku.getSquares());
      updateState(SudokuState.GENERATED);
    }

    /**
     * Resets the grid to the originally generated sudoku
     */
    private void resetGrid() {
      List<Cell> cells = sudoku.getSolution().getCells();
      for (int i = 0; i < cells.size(); ++i) {
        if (cells.get(i).getValues().isEmpty()) {
          textCells.get(i).setText("");
          textCells.get(i).uncheck();
          textCells.get(i).setEnabled(true);
        }
      }
      hintSquares = new ArrayList<Integer>(sudoku.getSquares());
      updateState(SudokuState.RESETTED);
    }

    /**
     * Checks current Sudoku and highlights squares as follows:
     * Correct - Green
     * InCorrect - Red
     */
    private void check() {
      boolean solved = true;
      List<Integer> values = sudoku.getSolver().getGenValues();
      for (int i = 0; i < values.size(); ++i) {
        CellTextView textCell = textCells.get(i);
        if (textCell.isEnabled()) {
          if (textCell.getText().toString().equals(Integer.toString(values.get(i)))) {
            textCell.isCorrect();
          } else {
            textCell.isIncorrect();
            solved = false;
          }
        }
      }
      if (solved) updateState(SudokuState.SOLVED);
    }

    /**
     * Reveals the answer to a random square and highlight it in yellow
     */
    public void hint() {
      Integer randS;
      for (List<Integer> values = sudoku.getSolver().getGenValues(); !hintSquares.isEmpty(); hintSquares.remove(randS)) {
        randS = hintSquares.get(ThreadLocalRandom.current().nextInt(0, hintSquares.size()));
        CellTextView textCell = textCells.get(randS);
        if (textCell.isEnabled() && !textCell.getText().toString().equals(Integer.toString(values.get(randS)))) {
          textCell.setText(Integer.toString(values.get(randS)));
          textCell.hinted();
          hintSquares.remove(randS);
          break;
        }
      }
    }

    private void updateState(SudokuState nextState) {
      currentState = nextState;
      setChanged();
      notifyObservers();
    }
  }
}
