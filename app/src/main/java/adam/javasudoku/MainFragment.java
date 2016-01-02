package adam.javasudoku;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Main/Title Fragment, shown when app launches
 */
public class MainFragment extends Fragment implements View.OnClickListener {

  Button newButton, loadButton, continueButton, quitButton;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_main, container, false);
    initialize(v);
    return v;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.main_frag_new_button:
        ((MainActivity)getActivity()).showGameFragment();
        break;
      case R.id.main_frag_quit_button:
        ((MainActivity)getActivity()).promptQuit();
        break;
      default:
        //Nothing
    }
  }

  private void initialize(View v) {
    newButton = (Button) v.findViewById(R.id.main_frag_new_button);
    quitButton = (Button) v.findViewById(R.id.main_frag_quit_button);

    newButton.setOnClickListener(this);
    quitButton.setOnClickListener(this);
  }
}
