package edu.msu.srijithv.steampunked;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.Random;
//
//  This class implements all the main game logic
//
public class Game  implements Serializable {
    //
    //  Pipe Types
    //
    private transient final int PIPE_RANDOM = -1;
    private transient final int PIPE_CAP = 0;
    private transient final int PIPE_NINETY = 1;
    private transient final int PIPE_STRAIGHT = 2;
    private transient final int PIPE_TEE = 3;
    private transient final int PIPE_GAUGE = 4;

    private int game_size = 10;
    private int playerId = -1;
    private String player1;
    private Pipe start1;
    private Pipe end1;
    private String player2;
    private Pipe start2;
    private Pipe end2;
    private transient TextPaint textPaint;
    //private transient Paint outlinePaint;

    private Pipe[] availablePipes;
    private PlayingArea play;
    private transient Context context;
    private transient Random random;
    private Pipe dragging;
    private int draggingIndex;
    private transient GameActivity gameActivity;
    private transient int gameSize;
    private transient boolean landscape;
    private transient int marginY;
    private transient int marginX;
    private transient Bitmap steam;

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private class Touch implements Serializable {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;
        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;
        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }
    }
    private transient Touch touch1 = new Touch();
    private transient Touch touch2 = new Touch();

    public Game(Context context) {
        this.context = context;
        random = new Random();
        reset(10, "Player1", "Player2");
        init(context);
    }
    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public void init(Context context) {
        this.context = context;
        textPaint = new TextPaint();
        //outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //outlinePaint.setColor(0xff008000);
        //outlinePaint.setStyle(Paint.Style.STROKE);

        if (random == null) {
            random = new Random();
        }
        for (Pipe pipe: availablePipes) {
            pipe.setPipeId(context, pipe.getPipeId());
        }
        if (draggingIndex >= 0) {
            dragging.setPipeId(context, dragging.getPipeId());
        }
        steam = BitmapFactory.decodeResource( context.getResources(),R.drawable.leak);

        play.init(context);
      }
    public void reset(int game_size, String player1, String player2) {
        this.game_size = game_size;
        this.player1 = player1;
        this.player2 = player2;

        playerId = 1;
        play = new PlayingArea(context, game_size, game_size);
        start1 = new Pipe(true, false, false, false);
        start1.setPipeId(this.context, R.drawable.straight);
        start1.setValve(context, true);
        start1.setRotation(90);
        start1.setPlayerId(1);
        //end1 = new Pipe(false, false, false, true);
        //end1.setPipeId(this.context, R.drawable.gauge);
        //end1.setGauge(true);
        end1 = generatePipe(PIPE_GAUGE);
        end1.setPlayerId(1);
        if (game_size == 5) {
            play.add(start1, 0, 0);

        } else {
            play.add(start1, 0, 1);
        }
        play.add(end1, play.getWidth()-1, play.getHeight()/2 - 1);
        start2 = new Pipe(true, false, false, false);
        start2.setPipeId(this.context, R.drawable.straight);
        start2.setValve(context,true);
        start2.setRotation(90);
        start2.setPlayerId(2);
        //end2 = new Pipe(false, false, false, true);
        //end2.setPipeId(this.context, R.drawable.gauge);
        //end2.setGauge(true);
        end2 = generatePipe(PIPE_GAUGE);
        end2.setPlayerId(2);
        play.add(start2, 0, play.getHeight()/2);
        play.add(end2, play.getWidth()-1,play.getHeight()-2);
        //
        //  Now generate the random available pipes
        //
        availablePipes = new Pipe[5];
        for (int i = 0; i < 5; i++) {
            Pipe pipe;
            pipe = generatePipe(PIPE_RANDOM);
            pipe.setY(0);
            availablePipes[i] = pipe;
        }
        dragging = null;
        draggingIndex = -1;
    }
    private Pipe generatePipe(int index) {
        Pipe pipe;
        int pipeN;
        //
        //  If index is negative then randomly generate
        //  a pipe. Otherwise index is the pipe type
        //
        if (index == PIPE_RANDOM) {
            double temp_num = random.nextDouble();
            //
            //  Straight generated 20% of the time
            //  Ninety generated 30% of the time
            //  Tee generated 30% of the time
            //  Cap generated 20% of the time
            //
            if (temp_num < .20) {
                pipeN = PIPE_STRAIGHT;  //Straight
            } else if (temp_num < 0.50) {
                pipeN = PIPE_NINETY;  //ninety
            } else if (temp_num < 0.80) {
                pipeN = PIPE_TEE;  // Tee
            } else {
                pipeN = PIPE_CAP; // Cape
            }
        } else {
            pipeN = index;
        }
        switch (pipeN) {
            case PIPE_CAP: //cap
                pipe = new Pipe(false, false, true, false);
                pipe.setPipeId(context, R.drawable.cap);
                break;
            case PIPE_NINETY: // ninety
                pipe = new Pipe(false, true, true, false);
                pipe.setPipeId(context, R.drawable.ninety);
                break;
            case PIPE_STRAIGHT: //straight
                pipe = new Pipe(true, false, true, false);
                pipe.setPipeId(context, R.drawable.straight);
                break;
            case PIPE_TEE: //tee
            default:
                pipe = new Pipe(true, true, true, false);
                pipe.setPipeId(context, R.drawable.tee);
                break;
            case PIPE_GAUGE: // gauge
                pipe = new Pipe(false, false, false, true);
                pipe.setPipeId(context, R.drawable.gauge);
                pipe.setGauge(true);
                break;
        }
        return pipe;
    }

    public void draw(View view, Canvas canvas) {
        landscape = false;
        if (play == null) {
            return;
        }
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();
        if (wid > hit) {
            landscape = true;
        }
        // Determine the minimum of the two dimensions
        int minDim = Math.min(wid, hit);
        int maxDim = Math.max(wid, hit);

        float SCALE_IN_VIEW = .95f;
        if (!landscape) {
            if ((maxDim - minDim) < (maxDim * 0.20f)) {
                SCALE_IN_VIEW = .80f;
            }
        }
        gameSize = (int)(minDim * SCALE_IN_VIEW);
        gameSize = (gameSize / game_size);
        gameSize = gameSize * game_size;
        
        // Compute the margins so we center the puzzle
        marginY = (int)((minDim - minDim * 0.95f) / 2f);
        marginX = (minDim - gameSize) / 2;
        if (landscape) {
            int tmp = marginX;
            //noinspection SuspiciousNameCombination
            marginX = marginY;
            marginY = tmp;
        }

        play.draw(canvas, marginX, marginY, gameSize, game_size);
        //
        //  Draw the player names
        //
        int pieceSize = gameSize / game_size;
        textPaint.setTextSize(pieceSize / 2f);
        //float textWidth = textPaint.measureText(player1);
        float txtCenterX, txtCenterY;
        txtCenterX = marginX + start1.getX() * pieceSize;
        txtCenterY = marginY + start1.getY() * pieceSize;
        canvas.drawText(player1, txtCenterX, txtCenterY + pieceSize*1.5f, textPaint);
        txtCenterX = marginX + start2.getX() * pieceSize;
        txtCenterY = marginY + start2.getY() * pieceSize;
        canvas.drawText(player2, txtCenterX, txtCenterY + pieceSize*1.5f, textPaint);

        int pmargin;
        int margin;
        if (landscape) {
            pmargin = marginX*2 + gameSize;
            margin = marginY;
        } else {
            pmargin = marginY*2 + gameSize;
            margin = marginX;
        }
        //
        //  Draw the available pieces
        //
        float sSize = gameSize/6f;
        float offset = sSize / 2;
        for (int i = 0; i < 5; i++) {
            if (i != draggingIndex) {
                Pipe pipe = availablePipes[i];
                float startP = margin + offset;
                if (landscape) {
                    pipe.setY(i);
                    pipe.setX(0);
                    pipe.setRotation(90);
                    //canvas.drawRect((float)pmargin,startP + sSize*i,(float)(pmargin+sSize),startP + sSize*(i+1), outlinePaint);
                    pipe.draw(canvas, pmargin, (int)startP, gameSize, 6);

                } else {
                    pipe.setX(i);
                    pipe.setY(0);
                    pipe.setRotation(0);
                    //canvas.drawRect(startP + sSize*i, pmargin, startP + sSize*(i+1), pmargin + sSize, outlinePaint);
                    pipe.draw(canvas, (int)startP, pmargin, gameSize, 6);
                }
            }
        }
        //
        // if dragging draw the piece
        //
        if (dragging != null) {
            dragging.drawAbsolute(canvas, marginY, gameSize, game_size);
        }
        if (PlayingArea.openFlanges.size() > 0) {
            for(OpenFlange flange : PlayingArea.openFlanges) {
                drawSteam(canvas, flange.x, flange.y, flange.direction, marginX, marginY, gameSize, game_size);
            }
        }
    }
    private void drawSteam(Canvas canvas, int x, int y, int direction, int marginX, int marginY, int gameSize, int game_size) {
        // Convert x,y to pixels and add the margin, then draw
        int squareSize = gameSize / game_size;
        float px, py;
        int rotation;

        if (direction == 0) {
            y -= 1;
        } else if (direction == 1) {
            x += 1;
        } else if (direction == 2) {
            y += 1;
       } else if (direction == 3) {
            x -= 1;
        }
        //
        //  Don't draw the steam if out of playing area
        //  While doing so will not cause an error, let's still not do this
        //
        if (x < 0 || x >= game_size || y < 0 || y >= game_size) {
            return;
        }
        px = marginX + x * squareSize + squareSize/2f;
        py = marginY + y * squareSize + squareSize/2f;
        rotation = direction * 90;
        //
        //  Now draw the leak
        //
        canvas.save();
        canvas.translate(px, py);
        float scaleFactor = (float)squareSize / (float)steam.getWidth();
        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);
        canvas.rotate(rotation);
        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-steam.getWidth() / 2f, -steam.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(steam, 0, 0, null);
        canvas.restore();

    }
     public boolean onTouchEvent(View view, MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                touch1.copyToLast();
                if (dragging == null) {
                     //
                    //  determine if touch a available piece
                    //
                    int index;
                    int row;
                    if (landscape) {
                        index = (int)((event.getY() - marginY) / gameSize * 7);
                        row = (int)((event.getX() - (marginX * 2 + gameSize)) / gameSize);
                        //index = (int)relY;
                    } else {
                        index = (int)((event.getX() - marginX) / gameSize * 7);
                        row = (int)((event.getY() - (marginY * 2 + gameSize)) / gameSize);
                        //index = (int) relX;
                    }
                    Log.i("SteamPunked","Touch Row = "+row+" index = "+index);
                    if (row == 0 && index >=1 && index < 6) {
                        dragging = availablePipes[index - 1];
                        dragging.setPlayerId(this.playerId);
                        draggingIndex = index - 1;
                        //
                        //  put the dragging piece in the playing area
                        //
                        if (landscape) {
                            dragging.drawx = marginX + gameSize / 2f;
                            dragging.drawy = marginY + gameSize / 2f;

                        } else {
                            dragging.drawy = marginY + gameSize / 2f;
                            dragging.drawx = marginX + gameSize / 2f;
                        }
                        dragging.snap(marginX, marginY,gameSize, game_size);
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                if (dragging != null) {
                    dragging.snap(marginX, marginY,gameSize, game_size);
                }
                view.invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (dragging != null) {
                    getPositions(event);
                    move();
                    view.invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (dragging != null) {
                    if (touch2.id == -1) {
                        touch2.id = id;
                        getPositions(event);
                        touch2.copyToLast();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (dragging != null) {
                    if (id == touch2.id) {
                        touch2.id = -1;
                    } else if (id == touch1.id) {
                        Touch t = touch1;
                        touch1 = touch2;
                        touch2 = t;
                        touch2.id = -1;
                    }
                    dragging.snap(marginX, marginY, gameSize, game_size);
                    view.invalidate();
                    return true;
                }
                break;
        }
        return false;
    }
    private void setPosition() {
        int halfSize = gameSize/game_size/2;
        if (dragging.drawx < marginX + halfSize) {
            dragging.drawx = marginX + halfSize;
        }
        if (dragging.drawx > marginX + gameSize - halfSize) {
            dragging.drawx = marginX + gameSize - halfSize;
        }
        if (dragging.drawy < marginY + halfSize) {
            dragging.drawy = marginY + halfSize;
        }
        if (dragging.drawy > marginY + gameSize - halfSize) {
            dragging.drawy = marginY + gameSize - halfSize;
        }

    }
    private void rotate(float dAngle, float x1, float y1) {
        // Compute the radians angle
        dragging.addDeltaRotation(dAngle);
        double rAngle = Math.toRadians(dAngle);
        float ca = (float) Math.cos(rAngle);
        float sa = (float) Math.sin(rAngle);
        float xp = (dragging.drawx - x1) * ca - (dragging.drawy - y1) * sa + x1;
        float yp = (dragging.drawx - x1) * sa + (dragging.drawy - y1) * ca + y1;
        dragging.drawx = xp;
        dragging.drawy = yp;
    }
    private void move() {
        touch1.computeDeltas();
        dragging.drawx += touch1.dX;
        dragging.drawy += touch1.dY;
        setPosition();

        if (touch2.id > 0) {
            float angle1 = angle(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float angle2 = angle(touch1.x, touch1.y, touch2.x, touch2.y);
            float da = angle2 - angle1;
            rotate(da, touch1.x, touch1.y);
            //dragging.setRotation(angle2);
            Log.i("SteamPunked", "Delta angle " + da + " Angle " + dragging.fRotation);

        }

    }
    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            // Get coordinates
            float x = event.getX(i);
            float y = event.getY(i);

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            } else if(id == touch2.id) {
                touch2.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }
    }
    /**
     * Determine the angle for two touches
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed angle in degrees
     */
    private float angle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
    //
    //  Game actions
    //
    public boolean Install() {
        if (dragging == null) {
            return false;
        }
        //  Don't allow the player to install a pipe if there
        //  is already a pipe at that location.
        if (play.getPipe(dragging.getX(), dragging.getY()) != null) {
            return false;
        }
        play.add(dragging, dragging.getX(), dragging.getY());
        if (dragging.doConnect()) {
            endTurn();
            return true;
        }
        play.remove(dragging, dragging.getX(), dragging.getY());

        return false;
    }
    public boolean OpenValve() {
        //
        //  This opens the valve on the starting pipe
        //  Searches for opennings
        //
        Pipe start;
        if (getCurrentPlayer() == 1) {
            start = start1;
        } else {
            start = start2;
        }
        start.setOpen(true);
        return play.search(start);
    }

    //  Check if the proper end pipe has been visited
    //
    public boolean connectedPipe() {
        Pipe end;
        if (getCurrentPlayer() == 1) {
            end = end1;
        } else {
            end = end2;
        }
        return end.beenVisited();
    }

    //
    //  Redraw the end pipe gauge showing pressure
    //  on the guage
    //
    public void showPressure() {
        Pipe end;
        if (getCurrentPlayer() == 1) {
            end = end1;
        } else {
            end = end2;
        }
        end.setOpen(true);
    }

    public void Discard() {
        if (dragging == null) {
            // randomly pick an available pipe to be replaced
            draggingIndex = random.nextInt(5);
        }
        endTurn();
    }
    public int getCurrentPlayer() {
        return this.playerId;
    }

    //
    //  Do all the processing at the end of the turn
    private void endTurn() {
        //  indicate not dragging
        dragging = null;
        //  generate a new available pipe
        availablePipes[draggingIndex] = generatePipe(PIPE_RANDOM);
        draggingIndex = -1;
        dragging = null;
        //  switch to the next player.
        playerId = nextPlayer(playerId);

    }

    //
    //  Change to the other player
    //
    private int nextPlayer(int id) {
        int playerId = id + 1;
        if (playerId > 2) {
            playerId = 1;
        }
        return playerId;
    }
}
