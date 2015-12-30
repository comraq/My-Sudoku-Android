package adam.javasudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

  //Static Constants
  //public final static String EXTRA_MESSAGE = "adam.javasudoku.MESSAGE";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
      }
    });
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
    switch(item.getItemId()) {

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

  /** Called when the user clicks the New Game button */
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
    new CustomDialogFragment().show(getFragmentManager(), "Quit Dialog");
  }
}
