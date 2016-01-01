package adam.javasudoku;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by adam on 2015-12-29.
 */
public class CustomDialogFragment extends DialogFragment {

  private static final String TITLE = "title";
  private static final String MESSAGE = "message";

  private CustomDialogListener dialogListener;

  public static CustomDialogFragment newInstance(String title, String message) {
    CustomDialogFragment f = new CustomDialogFragment();
    Bundle b = new Bundle();

    b.putString(TITLE, title);
    b.putString(MESSAGE, message);

    f.setArguments(b);
    return f;
  }

  interface CustomDialogListener {
    void doNegClick();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final String title = getArguments().getString(TITLE);
    final String message = getArguments().getString(MESSAGE);

    AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());

    theDialog.setTitle(title);
    theDialog.setMessage(message);

    theDialog.setNegativeButton(R.string.dialog_yes_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialogListener.doNegClick();
      }
    });

    theDialog.setPositiveButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dismiss();
      }
    });

    return theDialog.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      dialogListener = (CustomDialogListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(dialogListener.toString() + " must implement CustomDialogListener");
    }
  }
}
