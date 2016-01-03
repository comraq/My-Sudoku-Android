package adam.javasudoku;

import android.app.ProgressDialog;
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

  Button newButton, loadButton, continueButton, quitButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i("MainFrag", "onCreate");
  }
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Log.i("MainFrag", "onSaveInstanceState");
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.i("MainFrag", "onPause");
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.i("MainFrag", "onStop");
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Log.i("MainFrag", "onDestroyView");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i("MainFrag", "onDestroy");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    Log.i("MainFrag", "onDetach");
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.i("MainFrag", "onStart");
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.i("MainFrag", "onResume");
  }

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
        ((MainActivity)getActivity()).newGameFragment();
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
    quitButton = (Button) v.findViewById(R.id.main_frag_quit_button);

    continueButton.setOnClickListener(this);
    newButton.setOnClickListener(this);
    quitButton.setOnClickListener(this);

    if (((MainActivity) getActivity()).getSudoku() == null) continueButton.setEnabled(false);
  }
}
