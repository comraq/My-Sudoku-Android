package adam.javasudoku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SudokuGameActivity extends AppCompatActivity {

    private int dimensions;
    private GridLayout myGridLayout;
    private GridLayout.LayoutParams gridParams, cellParams;
    private EditText editText;
    private Button generateButton, resetButton, hintButton, checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(textView);*/

        this.setTitle("3x3 Sudoku");

        initialize();
        int maxLength = (dimensions > 3)? 2 : 1;

        initCellParams(maxLength);
    }

    public void testToastShort(View view) {
        String msg = "Testing reset button and short toast: " + editText.getText().toString();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void disableEditText(View view) {
        if (editText.isEnabled()) {
            editText.setEnabled(false);
        } else {
            editText.setEnabled(true);
        }
    }

    public void createGrid(View view) {
        myGridLayout.setBackgroundColor(Color.parseColor("#6ced38"));

        GridLayout grid;
        for (int r = 0; r < dimensions; ++r) {
            gridParams.rowSpec = GridLayout.spec(r, 1.0f);
            for (int c = 0; c < dimensions; ++c) {
                grid = new GridLayout(this);

                grid.setRowCount(dimensions);
                grid.setColumnCount(dimensions);
                grid.setBackgroundColor(Color.parseColor("#96dfe1"));

                gridParams.columnSpec = GridLayout.spec(c, 1.0f);
                myGridLayout.addView(grid, new GridLayout.LayoutParams(gridParams));
                createEditTexts(grid);
            }
        }
    }

    private void createEditTexts(GridLayout grid) {
        EditText editTextCell;
        int maxLength = (dimensions > 3)? 2 : 1;

        for (int r = 0; r < dimensions; ++r) {
            cellParams.rowSpec = GridLayout.spec(r, 1.0f);
            for (int c = 0; c < dimensions; ++c) {
                editTextCell = new EditText(this);

                editTextCell.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextCell.setBackgroundColor(Color.WHITE);
                editTextCell.setGravity(Gravity.CENTER);

                restrictEditTextLength(editTextCell, 1);
                editTextCell.setIncludeFontPadding(false);
                editTextCell.setEms(maxLength);
                //editTextCell.setText("" + (r*dimensions + c));

                cellParams.columnSpec = GridLayout.spec(c, 1.0f);
                grid.addView(editTextCell, new GridLayout.LayoutParams(cellParams));
            }
        }
    }

    private void initCellParams(int length) {
        gridParams = new GridLayout.LayoutParams();
        cellParams = new GridLayout.LayoutParams();

        gridParams.setMargins(2, 2, 2, 2);
        cellParams.setMargins(2, 2, 2, 2);
    }

    private void restrictEditTextLength(EditText editText, int length) {
        InputFilter[] lengthFilter = new InputFilter[1];
        lengthFilter[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(lengthFilter);
    }

    private void initialize() {
        //Initializing fields in MainActivity
        dimensions = 3;

        myGridLayout = (GridLayout) findViewById(R.id.test_grid_layout);
        myGridLayout.setRowCount(dimensions);
        myGridLayout.setColumnCount(dimensions);

        editText = (EditText) findViewById(R.id.edit_message);

        generateButton = (Button) findViewById(R.id.generate_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        hintButton = (Button) findViewById(R.id.hint_button);
        checkButton = (Button) findViewById(R.id.check_button);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                generateButton.setEnabled(!editText.getText().toString().trim().isEmpty());
                resetButton.setEnabled(!editText.getText().toString().trim().isEmpty());
                hintButton.setEnabled(!editText.getText().toString().trim().isEmpty());
                checkButton.setEnabled(!editText.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }
}
