package edu.msu.srijithv.steampunked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String GAME_SIZE = "Game_Size";
    public static final String PLAYER1_NAME = "Player1_Name";
    public static final String PLAYER2_NAME = "Player2_Name";

    private Spinner spinner;
    private EditText player1;
    private EditText player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player1 = findViewById(R.id.TextPerson1Name);
        player2 = findViewById(R.id.TextPerson2Name);

        spinner = findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_text);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        if (savedInstanceState != null) {
            player1.setText(savedInstanceState.getString(PLAYER1_NAME,"Player1"));
            player2.setText(savedInstanceState.getString(PLAYER2_NAME, "Player2"));
            int gamesize = savedInstanceState.getInt(GAME_SIZE, 10);
            int selPos = 1;
            switch (gamesize) {
                case 5:
                    selPos = 0;
                    break;

                case 10:
                    selPos = 1;
                    break;

                case 20:
                    selPos = 2;
                    break;
            }
            spinner.setSelection(selPos);

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(GAME_SIZE, Integer.parseInt(spinner.getSelectedItem().toString()));
        outState.putString(PLAYER1_NAME, player1.getText().toString());
        outState.putString(PLAYER2_NAME, player2.getText().toString());
    }
    public void onStart(View view) {
        if (player1.getText().toString().length() == 0 || player2.getText().toString().length() == 0) {
            Toast.makeText(this, R.string.need_names, Toast.LENGTH_LONG).show();
            return;

        }
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(PLAYER1_NAME, player1.getText().toString());
        intent.putExtra(PLAYER2_NAME, player2.getText().toString());
        intent.putExtra(GAME_SIZE, Integer.parseInt(spinner.getSelectedItem().toString()));
        startActivity(intent);
    }
}