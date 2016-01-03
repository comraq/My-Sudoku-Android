package adam.javasudoku;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, CustomDialogFragment.CustomDialogListener {

  private DialogInterface.OnClickListener quitListener, dismissListener;
  private CustomDialogFragment genDiagFrag;

  private Sudoku sudoku;
  private List<Integer> hintSquares;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setIcon(R.mipmap.ic_launcher);

    initDialogListeners();
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.add(R.id.main_activity, new MainFragment());
    ft.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    switch (item.getItemId()) {
      //noinspection SimplifiableIfStatement
      case R.id.action_new_sudoku:
        promptGenerate();
        return true;

      case R.id.action_exit:
        promptQuit();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void continueGameFragment() { getFragmentManager().popBackStack(); }

  public void promptNew() {
    if (getFragmentManager().getBackStackEntryCount() > 0) {
      AlertDialog.Builder newDialog = new AlertDialog.Builder(this);

      newDialog.setTitle(getString(R.string.new_dialog_title));
      newDialog.setMessage(getString(R.string.new_dialog_message));

      newDialog.setNegativeButton(R.string.dialog_yes_button, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          newGameFragment();
        }
      });
      newDialog.setPositiveButton(R.string.dialog_cancel_button, dismissListener);
      newDialog.show();
    } else {
      newGameFragment();
    }
  }

  public void promptGenerate() {
    if (genDiagFrag == null) genDiagFrag = CustomDialogFragment.newInstance(this, R.string.dialog_generate_title, R.string.dialog_generate_button, R.string.dialog_cancel_button);
    genDiagFrag.show(getFragmentManager(), "Generate Dialog Fragment");
  }

  @Override
  public void doNegClick(int dimensionsId, int diffId) {
    if (!(getFragmentManager().findFragmentById(R.id.main_activity) instanceof GameFragment)) {
      newGameFragment();
      getFragmentManager().executePendingTransactions();
    }
    final int dimensions;
    final String diff;
    if (dimensionsId == R.id.radio_dimensions_four) {
      dimensions = 4;
    } else {
      dimensions = 3;
    }
    switch (diffId) {
      case R.id.radio_diff_beginner:
        diff = Solver.BEGINNER;
        break;
      case R.id.radio_diff_casual:
        diff = Solver.CASUAL;
        break;
      case R.id.radio_diff_challenge:
        diff = Solver.CHALLENGE;
        break;
      default:
        diff = Solver.CASUAL;
    }

    //Showing progress dialog while generating 4x4 Sudokus will currently result in stackoverflow
    //new BackgroundTask(this, dimensions, diff).execute();

    newSudoku(dimensions, diff);
    GameFragment game = (GameFragment) getFragmentManager().findFragmentById(R.id.main_activity);
    game.updateBoard();
  }

  public void promptLoad() {
    loadCurrentGame();
  }

  public void promptSave() {
    saveCurrentGame();
  }

  public void promptQuit() {
    AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);

    quitDialog.setTitle(getString(R.string.quit_dialog_title));
    quitDialog.setMessage(getString(R.string.quit_dialog_message));

    quitDialog.setNegativeButton(R.string.dialog_yes_button, quitListener);
    quitDialog.setPositiveButton(R.string.dialog_cancel_button, dismissListener);
    quitDialog.show();
  }

  public void setActivityTitle() {
    if (getFragmentManager().findFragmentById(R.id.main_activity) instanceof MainFragment) {
      this.setTitle(getString(R.string.app_name));
    } else {
      this.setTitle(sudoku.getDimensions() + "x" + sudoku.getDimensions() + " Sudoku");
    }
  }

  private void newGameFragment() {
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.replace(R.id.main_activity, new GameFragment());
    ft.commit();
  }

  private void newSudoku(int newDimensions, String difficulty) {
    if (sudoku == null) {
      sudoku = new Sudoku().initialize(newDimensions);
    } else if (sudoku.getDimensions() != newDimensions) {
      sudoku.initialize(newDimensions);
    }
    try {
      sudoku.setSolution(sudoku.getSolver().generate(difficulty));
    } catch (CloneNotSupportedException e) {
      Toast.makeText(this, R.string.solver_generate_error, Toast.LENGTH_LONG).show();
    }
  }

  private void loadCurrentGame() {}

  private void saveCurrentGame() {
    //File savedGame = new File(getFilesDir(), getString(R.string.app_saved_game_filename));
  }

  private void initDialogListeners() {
    quitListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    };
    dismissListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    };
  }

  public Sudoku getSudoku() { return sudoku; }

  public List<Integer> getHintSquares() { return hintSquares; }

  public DialogInterface.OnClickListener getDismissListener() { return dismissListener; }

  @Override
  public void update(Observable observable, Object data) {
    if (data == GameFragment.SudokuState.GENERATED || (data == GameFragment.SudokuState.RESETTED))
      hintSquares = new ArrayList<Integer>(sudoku.getSquares());
  }

  /**
   * Overrides back button navigation with fragments
   */
  @Override
  public void onBackPressed() {
    if (getFragmentManager().findFragmentById(R.id.main_activity) instanceof GameFragment) {
      FragmentTransaction ft = getFragmentManager().beginTransaction();
      ft.replace(R.id.main_activity, new MainFragment());
      if (sudoku != null) ft.addToBackStack(null);
      ft.commit();
    } else {
      promptQuit();
    }
  }

  /**
   * Removes focus from textViews when touching outside
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      View v = getCurrentFocus();
      if ( v instanceof EditText) {
        Rect outRect = new Rect();
        v.getGlobalVisibleRect(outRect);
        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
          v.clearFocus();
          InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
      }
    }
    return super.dispatchTouchEvent(event);
  }

  //Current solving algorithm causes stackoverflow with the following when generating 4x4 Sudokus
  @Deprecated
  private class BackgroundTask extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog;
    private int dimensions;
    private String diff;

    public BackgroundTask(MainActivity activity, int dimensions, String diff) {
      dialog = new ProgressDialog(activity);
      this.dimensions = dimensions;
      this.diff = diff;
    }

    @Override
    protected void onPreExecute() {
      dialog.setTitle("Generating Sudoku...");
      dialog.setMessage("Please be patient as generating larger and challenging Sudokus takes time");
      dialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
      if (dialog.isShowing()) {
        dialog.dismiss();
        setActivityTitle();
        GameFragment game = (GameFragment) getFragmentManager().findFragmentById(R.id.main_activity);
        game.updateBoard();
      }
    }

    @Override
    protected Void doInBackground(Void... params) {
      newSudoku(dimensions, diff);
      return null;
    }
  }
}