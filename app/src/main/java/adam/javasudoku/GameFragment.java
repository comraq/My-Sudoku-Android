package adam.javasudoku;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fragment for displaying the Sudoku Game
 */
public class GameFragment extends Fragment implements View.OnClickListener, Observer {

  private final static int BLOCK_SEPARATOR = Color.parseColor("#6ced38");
  private final static int GRID_LINES = Color.parseColor("#96dfe1");

  private Button generateButton, resetButton, hintButton, checkButton;
  private TextView statusTextView;
  private DialogInterface.OnClickListener resetListener;

  private SudokuBoard board;
  private MainActivity activity;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_game, container, false);
    initialize(v);
    return v;
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
        board.hint();
        break;
      case R.id.game_check_button:
        board.check();
        break;
      default:
        Toast.makeText(getActivity(), "Unidentified button! id: " + v.getId(), Toast.LENGTH_SHORT).show();
    }
  }

  private void generate() {
    activity.promptGenerate();
    board.updateDimensions();
    board.fillGrid();
  }

  private void reset() {
    AlertDialog.Builder resetDialog = new AlertDialog.Builder(activity);

    resetDialog.setTitle(getString(R.string.reset_dialog_title));
    resetDialog.setMessage(getString(R.string.reset_dialog_message));

    resetDialog.setNegativeButton(R.string.dialog_yes_button, resetListener);
    resetDialog.setPositiveButton(R.string.dialog_cancel_button, activity.getDismissListener());
    resetDialog.show();
  }

  private void gameWon() {
    List<Cell> cells = activity.getSudoku().getSolution().getCells();
    for (int i = 0; i < cells.size(); ++i) {
      if (cells.get(i).getValues().isEmpty()) {
        board.textCells.get(i).setEnabled(false);
        board.textCells.get(i).isCorrect();
      }
    }
    AlertDialog.Builder wonDialog = new AlertDialog.Builder(activity);

    wonDialog.setTitle(getString(R.string.won_dialog_title));
    wonDialog.setMessage(getString(R.string.won_dialog_message));

    wonDialog.setNegativeButton(getString(R.string.dialog_ok_button), activity.getDismissListener());
    wonDialog.setPositiveButton(getString(R.string.dialog_generate_button), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        generate();
      }
    });

    wonDialog.show();
  }

  private void initialize(View v) {
    //Initializing fields in GameFragment
    activity = (MainActivity) getActivity();

    generateButton = (Button) v.findViewById(R.id.game_generate_button);
    resetButton = (Button) v.findViewById(R.id.game_reset_button);
    hintButton = (Button) v.findViewById(R.id.game_hint_button);
    checkButton = (Button) v.findViewById(R.id.game_check_button);
    statusTextView = (TextView) v.findViewById(R.id.game_status_panel);

    generateButton.setOnClickListener(this);
    resetButton.setOnClickListener(this);
    hintButton.setOnClickListener(this);
    checkButton.setOnClickListener(this);

    board = new SudokuBoard(v);
    resetListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        board.resetGrid();
      }
    };
  }

  public enum SudokuState {
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

  @Override
  public void update(Observable observable, Object data) {
    statusTextView.setText(((SudokuState) data).message());
    switch (((SudokuState) data)) {
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
        break;
      case SOLVED:
        resetButton.setEnabled(true);
        checkButton.setEnabled(false);
        hintButton.setEnabled(false);
        gameWon();
        break;
      default:
        Toast.makeText(getActivity(), "Undefined state!", Toast.LENGTH_SHORT).show();
    }
  }

  private class SudokuBoard extends Observable implements TextWatcher {

    private GridLayout myGridLayout;
    private GridLayout.LayoutParams gridParams, cellParams;
    private SparseArray<CellTextView> textCells;
    private int dimensions;

    private SudokuBoard(View view) {
      myGridLayout = (GridLayout) view.findViewById(R.id.game_grid_layout);
      updateDimensions();
      myGridLayout.setRowCount(dimensions);
      myGridLayout.setColumnCount(dimensions);
      textCells = new SparseArray<CellTextView>();

      initCellParams();
      createGrid();

      addObserver(activity);
      addObserver(GameFragment.this);
      updateState(SudokuState.INIT);
    }

    private void createGrid() {
      myGridLayout.setBackgroundColor(BLOCK_SEPARATOR);

      GridLayout grid;
      for (int r = 0; r < dimensions; ++r) {
        gridParams.rowSpec = GridLayout.spec(r, 1.0f);
        for (int c = 0; c < dimensions; ++c) {
          grid = new GridLayout(activity);

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
      int maxLength = (dimensions > 3) ? 2 : 1;

      for (int r = 0; r < dimensions; ++r) {
        cellParams.rowSpec = GridLayout.spec(r, 1.0f);
        for (int c = 0; c < dimensions; ++c) {
          cellParams.columnSpec = GridLayout.spec(c, 1.0f);

          textCell = new CellTextView(activity, maxLength);
          grid.addView(textCell, new GridLayout.LayoutParams(cellParams));
          textCells.put(squareNum + r * (int) (Math.pow(dimensions, 2) + 0.5) + c, textCell);
          textCell.addTextChangedListener(this);
        }
      }
    }

    /**
     * Fills the grid with a generated sudoku based on the difficulty specified
     */
    private void fillGrid() {
      List<Cell> cells = activity.getSudoku().getSolution().getCells();
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
      updateState(SudokuState.GENERATED);
    }

    /**
     * Resets the grid to the originally generated sudoku
     */
    private void resetGrid() {
      List<Cell> cells = activity.getSudoku().getSolution().getCells();
      for (int i = 0; i < cells.size(); ++i) {
        if (cells.get(i).getValues().isEmpty()) {
          textCells.get(i).setText("");
          textCells.get(i).uncheck();
          textCells.get(i).setEnabled(true);
        }
      }
      updateState(SudokuState.RESETTED);
    }

    /**
     * Checks current Sudoku and highlights squares as follows:
     * Correct - Green
     * InCorrect - Red
     */
    private void check() {
      boolean solved = true;
      List<Integer> values = activity.getSudoku().getSolver().getGenValues();
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
      List<Integer> hintSquares = activity.getHintSquares();
      for (List<Integer> values = activity.getSudoku().getSolver().getGenValues(); !hintSquares.isEmpty(); hintSquares.remove(randS)) {
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

    private void uncheckTextCells() {
      CellTextView textCell;
      for (int i = 0; i < textCells.size(); ++i) {
        textCell = textCells.valueAt(i);
        if (textCell.isEnabled()) textCell.uncheck();
      }
    }

    private void initCellParams() {
      gridParams = new GridLayout.LayoutParams();
      cellParams = new GridLayout.LayoutParams();

      gridParams.setMargins(2, 2, 2, 2);
      cellParams.setMargins(2, 2, 2, 2);
    }

    private void updateDimensions() {
      if (activity.getSudoku() == null) {
        dimensions = 3;
      } else if (dimensions != activity.getSudoku().getDimensions()) {
        dimensions = activity.getSudoku().getDimensions();
        myGridLayout.removeAllViews();
        createGrid();
      }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      //Nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      uncheckTextCells();
      updateState(SudokuState.PLAYING);
    }

    @Override
    public void afterTextChanged(Editable s) {
      //Nothing
    }

    private void updateState(SudokuState nextState) {
      setChanged();
      notifyObservers(nextState);
    }
  }
}