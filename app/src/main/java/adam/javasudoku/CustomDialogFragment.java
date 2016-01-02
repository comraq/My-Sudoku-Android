package adam.javasudoku;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by adam on 2015-12-29.
 */
public class CustomDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {//}, DialogInterface.OnShowListener {

  private static final String TITLE = "title";
  private static final String NEG_BUTTON = "neg";
  private static final String POS_BUTTON = "pos";

  private CustomDialogListener dialogListener;

  public static CustomDialogFragment newInstance(Context context, int titleId, int negButtonId, int posButtonId) {//int msgId) {
    CustomDialogFragment f = new CustomDialogFragment();
    Bundle b = new Bundle();

    b.putString(TITLE, context.getString(titleId));
    b.putString(NEG_BUTTON, context.getString(negButtonId));
    b.putString(POS_BUTTON, context.getString(posButtonId));

    f.setArguments(b);
    return f;
  }

  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    RadioButton diffBeginnerRadio = (RadioButton) getDialog().findViewById(R.id.radio_diff_beginner);
    if (checkedId == R.id.radio_dimensions_three) {
      diffBeginnerRadio.setEnabled(true);
    } else {
      diffBeginnerRadio.setEnabled(false);
      if (diffBeginnerRadio.isChecked()) {
        ((RadioButton) getDialog().findViewById(R.id.radio_diff_casual)).setChecked(true);
      }
    }
  }

  /*@Override
  public void onShow(DialogInterface dialog) {
    theDialog.findViewById(R.id.radio_dimensions_three).setOnClickListener(this);
    theDialog.findViewById(R.id.radio_dimensions_four).setOnClickListener(this);

    View dimensionRadioThree = getDialog().findViewById(R.id.radio_dimensions_three);
    View dimensionRadioFour = getDialog().findViewById(R.id.radio_dimensions_three);
    dimensionRadioThree.setOnClickListener(this);
    dimensionRadioFour.setOnClickListener(this);
  }*/

  interface CustomDialogListener {
    void doNegClick(int id1, int id2);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final String title = getArguments().getString(TITLE);
    final String negButtonText = getArguments().getString(NEG_BUTTON);
    final String posButtonText = getArguments().getString(POS_BUTTON);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setTitle(title);
    builder.setNegativeButton(negButtonText, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialogListener.doNegClick(
          ((RadioGroup) getDialog().findViewById(R.id.radio_dimensions_group)).getCheckedRadioButtonId(),
          ((RadioGroup) getDialog().findViewById(R.id.radio_diff_group)).getCheckedRadioButtonId());
      }
    });
    builder.setPositiveButton(posButtonText, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dismiss();
      }
    });

    LayoutInflater i = getActivity().getLayoutInflater();
    View dialogView = i.inflate(R.layout.dialogpreference_generate, null);
    builder.setView(dialogView);

    //dialogView.findViewById(R.id.radio_dimensions_three).setOnClickListener(this);
    //dialogView.findViewById(R.id.radio_dimensions_four).setOnClickListener(this);
    ((RadioGroup)dialogView.findViewById(R.id.radio_dimensions_group)).setOnCheckedChangeListener(this);

    return builder.create();
  }

  /*@Override
  public void onClick(View v) {
    RadioButton diffBeginnerRadio = (RadioButton) getDialog().findViewById(R.id.radio_diff_beginner);
    if (v.getId() == R.id.radio_dimensions_three) {
      diffBeginnerRadio.setEnabled(true);
    } else {
      diffBeginnerRadio.setEnabled(false);
      if (diffBeginnerRadio.isChecked()) {
        ((RadioButton) getDialog().findViewById(R.id.radio_diff_casual)).setChecked(true);
      }
    }
  }*/

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      dialogListener = (CustomDialogListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(dialogListener.toString() + " must implement CustomDialogListener");
    }
  }
}
