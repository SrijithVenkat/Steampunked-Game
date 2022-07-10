package edu.msu.srijithv.steampunked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    private String name1;
    private String name2;
    private int game_size;
    private GameView gameView;
    private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        gameView = findViewById(R.id.gameView);
        gameView.setGameActivity(this);
        name = findViewById(R.id.lbl_playerName);

        if (intent != null) {
            name1 = intent.getStringExtra(MainActivity.PLAYER1_NAME);
            name2 = intent.getStringExtra(MainActivity.PLAYER2_NAME);
            game_size = intent.getIntExtra(MainActivity.GAME_SIZE, 10);
        }
        if (savedInstanceState != null) {
            name1 = savedInstanceState.getString(MainActivity.PLAYER1_NAME, "Player1");
            name2 = savedInstanceState.getString(MainActivity.PLAYER2_NAME, "Player2");
            game_size = savedInstanceState.getInt(MainActivity.GAME_SIZE, 10);
        }
        gameView.reset(game_size, name1, name2);
        if (savedInstanceState != null) {
            gameView.restoreState(savedInstanceState);
         }
        setName(gameView.GetCurrentPlayer());
     }

    private void setName(int iPlayer) {
        if (iPlayer == 1) {
            name.setText(name1);
        } else {
            name.setText(name2);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.GAME_SIZE, game_size);
        outState.putString(MainActivity.PLAYER1_NAME, name1);
        outState.putString(MainActivity.PLAYER2_NAME, name2);
        gameView.saveState(outState);
    }

    //
    //  If the user presses the back key, treat this like a surrender
    //
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        // Parameterize the builder
        builder.setTitle(R.string.surrender);
        builder.setMessage(R.string.verify_surrender);
        builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> getOut());
        builder.setNegativeButton(android.R.string.cancel, null);
        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
   }

    private void getOut() {
        super.onBackPressed();
    }

    public void onInstallClick(View view) {
        if (!gameView.Install()) {
            Toast.makeText(getApplicationContext(), R.string.msg_invalid_move, Toast.LENGTH_LONG).show();
        }
        setName(gameView.GetCurrentPlayer());
    }

    public void onDiscardClick(View view) {
        gameView.Discard();
        setName(gameView.GetCurrentPlayer());
    }
    public void onOpenValveClick(View view) {
         boolean rtn = gameView.OpenValve();
        int current = gameView.GetCurrentPlayer();
        String title;
        String message;
        String name;
        if (current == 1) {
            name = name1;
        } else {
            name = name2;
        }
        if (rtn) {
            // you win
            title = getString(R.string.winner_title);
            message = getString(R.string.winner_message, name);
        } else {
            // you lose
            title = getString(R.string.loser_title);
            message = getString(R.string.loser_message, name);
        }
        EndGameDlg alertDialog = new EndGameDlg(title, message);

        alertDialog.show(getSupportFragmentManager(), "End Game");

    }

    public void onSurrenderClick(View view) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        // Parameterize the builder
        builder.setTitle(R.string.surrender);
        builder.setMessage(R.string.verify_surrender);
        builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> finish());
        builder.setNegativeButton(android.R.string.cancel, null);
        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}