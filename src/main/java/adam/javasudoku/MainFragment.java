package adam.javasudoku;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Main/Title Fragment, shown when app launches
 */
public class MainFragment extends Fragment implements View.OnClickListener {

  Button continueButton, newButton, loadButton, quitButton;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_main, container, false);
    initialize(v);
    ((MainActivity) getActivity()).setActivityTitle();
    Log.i("MainFrag", "onCreateView");
    return v;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.main_frag_cont_button:
        ((MainActivity)getActivity()).continueGameFragment();
        break;
      case R.id.main_frag_new_button:
        ((MainActivity)getActivity()).promptNew();
        break;
      case R.id.main_frag_load_button:
        ((MainActivity)getActivity()).promptLoad();
        break;
      case R.id.main_frag_quit_button:
        ((MainActivity)getActivity()).promptQuit();
        break;
      default:
        //Nothing
    }
  }

  private void initialize(View v) {
    continueButton = (Button) v.findViewById(R.id.main_frag_cont_button);
    newButton = (Button) v.findViewById(R.id.main_frag_new_button);
    loadButton = (Button) v.findViewById(R.id.main_frag_load_button);
    quitButton = (Button) v.findViewById(R.id.main_frag_quit_button);

    continueButton.setOnClickListener(this);
    newButton.setOnClickListener(this);
    loadButton.setOnClickListener(this);
    quitButton.setOnClickListener(this);

    if (((MainActivity) getActivity()).getSudoku() == null) continueButton.setEnabled(false);
  }
}
