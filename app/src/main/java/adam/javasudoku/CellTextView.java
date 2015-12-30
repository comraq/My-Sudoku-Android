package adam.javasudoku;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;

/**
 * Created by adam on 2015-12-29.
 */
public class CellTextView extends EditText {
  private final static int INCORRECT = Color.parseColor("#f97474");
  private final static int CORRECT = Color.GREEN;
  private final static int UNCHECK = Color.WHITE;
  private final static int HINT = Color.YELLOW;

  public CellTextView(Context context, int maxLength) {
    super(context);

    setInputType(InputType.TYPE_CLASS_NUMBER);
    setBackgroundColor(UNCHECK);
    setGravity(Gravity.CENTER);

    restrictEditTextLength(maxLength);
    setIncludeFontPadding(false);
    setEms(maxLength);
    setEnabled(false);
  }

  private void restrictEditTextLength(int length) {
    InputFilter[] lengthFilter = new InputFilter[1];
    lengthFilter[0] = new InputFilter.LengthFilter(length);
    setFilters(lengthFilter);
  }
}
