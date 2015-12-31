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
  public final static int INCORRECT = Color.parseColor("#f97474");
  public final static int CORRECT = Color.GREEN;
  public final static int UNCHECK = Color.WHITE;
  public final static int HINT = Color.YELLOW;
  public final static int DISABLED = Color.rgb(240, 240, 240);

  public CellTextView(Context context, int maxLength) {
    super(context);

    setInputType(InputType.TYPE_CLASS_NUMBER);
    setBackgroundColor(UNCHECK);
    setGravity(Gravity.CENTER);

    restrictEditTextLength(maxLength);
    setIncludeFontPadding(false);
    setEms(1);
    setEnabled(false);
    setTextColor(Color.BLACK);
  }

  private void restrictEditTextLength(int length) {
    InputFilter[] lengthFilter = new InputFilter[1];
    lengthFilter[0] = new InputFilter.LengthFilter(length);
    setFilters(lengthFilter);
  }

  @Override
  public void setEnabled(boolean bool) {
    super.setEnabled(bool);
    if (bool) {
      setBackgroundColor(UNCHECK);
    } else {
      setBackgroundColor(DISABLED);
    }
  }
}
