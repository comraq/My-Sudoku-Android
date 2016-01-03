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
  public final static int DISABLED = Color.rgb(240, 240, 240);

  private int backgroundColour;

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
  public void setBackgroundColor(int colour) {
    super.setBackgroundColor(colour);
    backgroundColour = colour;
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

  public void isCorrect() { setBackgroundColor(CORRECT); }

  public void isIncorrect() { setBackgroundColor(INCORRECT); }

  public void uncheck() { setBackgroundColor(UNCHECK); }

  public void hinted() { setBackgroundColor(HINT); }

  public int getBackgroundColour() { return backgroundColour; }
}
