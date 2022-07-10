package edu.msu.srijithv.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
public class GameView extends View {
    private final String GAME_STATE = "Game_State";
    private GameActivity gameActivity;
    private Game game;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
       game = new Game(context);
    }

    public void reset(int game_size, String player1, String player2) {
        game.reset(game_size, player1, player2);
    }
    public void setGameActivity(GameActivity activity) {
        this.gameActivity = activity;
        game.setGameActivity(activity);
    }
    public void saveState(Bundle outState) {
        outState.putSerializable(GAME_STATE, game);
    }
    public void restoreState(@NonNull Bundle inState) {
        game = (Game) inState.getSerializable(GAME_STATE);
        game.setGameActivity(gameActivity);
        game.init(getContext());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        game.draw(this, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return game.onTouchEvent(this, event);
    }
    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean performClick() {
        return super.performClick();
    }
    public boolean Install() {
        boolean rtn = game.Install();
        invalidate();
        return rtn;
    }
    public void Discard() {
        game.Discard();
        invalidate();
    }
    public boolean OpenValve() {
        boolean rtn = game.OpenValve();
        if (rtn) {
           rtn = game.connectedPipe();
        }
        if (rtn) {
            game.showPressure();
        }
        invalidate();
        return rtn;
    }
    public int GetCurrentPlayer() {
        return game.getCurrentPlayer();
    }
 }