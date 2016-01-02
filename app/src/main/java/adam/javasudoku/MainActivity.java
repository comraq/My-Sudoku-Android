package adam.javasudoku;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class MainActivity extends AppCompatActivity implements Observer {

  //Static Constants
  //public final static String EXTRA_MESSAGE = "title";
  private final static int TEST_DIMENSION = 3;

  private DialogInterface.OnClickListener quitListener, dismissListener;

  private Sudoku sudoku;
  private List<Integer> hintSquares;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setIcon(R.mipmap.ic_launcher);

    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.add(R.id.main_activity, new MainFragment());
    ft.commit();
    initDialogListeners();

    /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
      }
    });*/
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
      case R.id.action_settings:
        return true;

      case R.id.action_exit:
        promptQuit();
        return true;

      case R.id.action_test1:
        return true;

      case R.id.action_test2:
        getFragmentManager().popBackStack();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void showGameFragment() {
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.replace(R.id.main_activity, new GameFragment());
    ft.addToBackStack(null);
    ft.commit();
  }

  public void promptGenerate() {
    newSudoku(TEST_DIMENSION, Solver.CHALLENGE);
  }

  public void promptQuit() {
    AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);

    quitDialog.setTitle(getString(R.string.quit_dialog_title));
    quitDialog.setMessage(getString(R.string.quit_dialog_message));

    quitDialog.setNegativeButton(R.string.dialog_yes_button, quitListener);
    quitDialog.setPositiveButton(R.string.dialog_cancel_button, dismissListener);
    quitDialog.show();
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
    setActivityTitle();
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

  private void setActivityTitle() {
    this.setTitle(sudoku.getDimensions() + "x" + sudoku.getDimensions() + " Sudoku");
  }

  public Sudoku getSudoku() {
    return sudoku;
  }

  public List<Integer> getHintSquares() {
    return hintSquares;
  }

  public DialogInterface.OnClickListener getDismissListener() {
    return dismissListener;
  }

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
    if (getFragmentManager().getBackStackEntryCount() > 0) {
      getFragmentManager().popBackStackImmediate();
    } else {
      promptQuit();
      //super.onBackPressed();
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
}