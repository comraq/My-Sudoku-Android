package adam.javasudoku;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

  //Static Constants
  //public final static String EXTRA_MESSAGE = "title";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setIcon(R.mipmap.ic_launcher);

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
        launchQuitDialog();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Called when the user clicks the New Game button
   */
  public void newGame(View view) {
    Intent intent = new Intent(this, SudokuGameActivity.class);
    /*String message = editText.getText().toString();
    intent.putExtra(EXTRA_MESSAGE, message);*/
    startActivity(intent);
  }

  public void exitApp(View view) {
    launchQuitDialog();
  }

  private void launchQuitDialog() {
    AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);

    quitDialog.setTitle(getString(R.string.quit_dialog_title));
    quitDialog.setMessage(getString(R.string.quit_dialog_message));

    quitDialog.setNegativeButton(R.string.dialog_yes_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    });
    quitDialog.setPositiveButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    quitDialog.show();
  }
}