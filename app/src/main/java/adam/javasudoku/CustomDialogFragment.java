package adam.javasudoku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by adam on 2015-12-29.
 */
public class CustomDialogFragment extends DialogFragment {

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());

    theDialog.setTitle("Exiting the Application");
    theDialog.setMessage("Are you sure you want to quit?");

    theDialog.setNegativeButton(R.string.custom_dialog_neg_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        getActivity().finish();
      }
    });

    theDialog.setPositiveButton(R.string.custom_dialog_pos_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dismiss();
      }
    });

    return theDialog.create();
  }

}
