package edu.msu.srijithv.steampunked;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An example of how a pipe might be represented.
 */
public class Pipe implements Serializable {

    /**
     * Playing area this pipe is a member of
     */
    private transient PlayingArea playingArea = null;

    private int playerId = -1;      // The player this pipe belongs to 1 or 2
    private int pipeid = 0;         // resource id for this pipes graphic
    private boolean hasValve = false;   // true if this pipe has a valve

    private boolean isOpen = false;     // true if the valve is open
    private boolean hasGuage = false;   // true if this pipe has a gauge

    //private transient Context context;

    private transient Bitmap pipe;
    private transient Bitmap valve;
    private int rotation = 0;       // snapped rotation
    public float fRotation = 0.0f;  // dragging rotation.

    private transient Paint gaugePaint;
    /**
     * Array that indicates which sides of this pipe
     * has flanges. The order is north, east, south, west.
     *
     * As an example, a T that has a horizontal pipe
     * with the T open to the bottom would be:
     *
     * false, true, true, true
     */
    private final boolean[] connect; // = {false, false, false, false};
    //
    //  Translates
    private static final transient int[][] rotMatrix = {{0,3,2,1}, {1,0,3,2},{2,1,0,3}, {3,2,1,0}};
    /**
     * X location in the playing area (index into array)
     */
    private int x = 0;
    /**
     * Y location in the playing area (index into array)
     */
    private int y = 0;
    public float drawx;
    public float drawy;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void clearArea() {
        this.playingArea = null;
    }
    /**
     * Depth-first visited visited
     */
    private boolean visited = false;


    /**
     * Constructor
     * @param north True if connected north
     * @param east True if connected east
     * @param south True if connected south
     * @param west True if connected west
     */
    public Pipe(boolean north, boolean east, boolean south, boolean west) {
        connect = new boolean[4];
        connect[0] = north;
        connect[1] = east;
        connect[2] = south;
        connect[3] = west;
    }
    public boolean getConnect(int dir) {
        int rIndex = rotation / 90;
        int i = rotMatrix[dir][rIndex];
        return connect[i];
    }
    public int getRotDirection(int dir) {
        int rIndex = rotation / 90;
        return rotMatrix[dir][rIndex];
    }
    public void setPipeId(Context context, int resId) {
        //this.context = context;
        this.pipeid = resId;
        pipe = BitmapFactory.decodeResource(context.getResources(), resId);
        if (hasValve) {
            valve = BitmapFactory.decodeResource(context.getResources(), R.drawable.handle);
        }
    }
    public int getPipeId() {
        return pipeid;
    }
    public void setValve(Context context, Boolean flag) {
        this.hasValve = flag;
        this.valve = BitmapFactory.decodeResource(context.getResources(), R.drawable.handle);
    }
    public void setGauge(Boolean flag) {
        this.hasGuage = flag;
    }
    public void addDeltaRotation(float delta) {
        this.setRotation(fRotation + delta);
    }
    public void setRotation(float rAngle) {
        this.fRotation = rAngle;
        this.rotation = (int)rAngle;
    }
    public void snap(int marginX, int marginY,
                     int gameSize, int game_size) {
        float angle = (int)(Math.floor((fRotation + 45f) / 90f) * 90f);
        float sangle = angle % 360;
        if (sangle < 0f) {
            sangle = 360f + sangle;
        }
        Log.i("SteamPunk", "Snap angle " + angle + " " + sangle);
        rotation = (int)sangle;
        fRotation = rotation;
        int squareSize = gameSize / game_size;
        x = (int)((drawx - marginX )/squareSize);
        y = (int)((drawy - marginY )/squareSize);
        drawx = x * squareSize + marginX + squareSize/2f;
        drawy = y * squareSize + marginY + squareSize/2f;
    }
    public void drawAbsolute(Canvas canvas, int marginY,
                             int gameSize, int game_size) {
        canvas.save();
        canvas.translate(drawx, drawy);
        int squareSize = gameSize / game_size;
        float scaleFactor = (float)squareSize / (float)pipe.getWidth();
        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);
        canvas.rotate(this.rotation);
        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-pipe.getWidth() / 2f, -pipe.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(pipe, 0, 0, null);
        canvas.restore();

    }
    public void draw(Canvas canvas, int marginX, int marginY,
                     int gameSize, int game_size) {
        canvas.save();
        // Convert x,y to pixels and add the margin, then draw
        int squareSize = gameSize / game_size;
        float px, py;
        px = marginX + x * squareSize + squareSize/2f;
        py = marginY + y * squareSize + squareSize/2f;
        if (this.hasGuage) {
            py -= squareSize/4f;
        }
        canvas.translate(px, py);
        float scaleFactor = (float)squareSize / (float)pipe.getWidth();
        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);
        canvas.rotate(this.rotation);
        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-pipe.getWidth() / 2f, -pipe.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(pipe, 0, 0, null);
        canvas.restore();
        if (hasValve) {
            canvas.save();
            canvas.translate(px, py);
            canvas.scale(scaleFactor, scaleFactor);
            if (isOpen) {
                canvas.rotate(90);
            }
            canvas.translate(-pipe.getWidth() / 2f, -pipe.getHeight() / 2f);
            canvas.drawBitmap(valve, 0, 0, null);
            canvas.restore();
        }
        if (hasGuage) {
            float sx, sy, ex, ey;
            sx = px;// + 10 * scaleFactor;
            sy = py + (42 - pipe.getWidth()/2f) * scaleFactor;
            ey = sy + 50 * scaleFactor;
            if (isOpen) {
                ex = sx + 35 * scaleFactor;
            } else {
                ex = sx - 35 * scaleFactor;
            }
            if (gaugePaint == null) {
                gaugePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                gaugePaint.setColor(Color.RED);
                if (game_size == 20) {
                    gaugePaint.setStrokeWidth(5f);
                } else {
                    gaugePaint.setStrokeWidth(10f);
                }
            }
            canvas.save();
            canvas.drawLine(sx,sy, ex, ey, gaugePaint);
            canvas.restore();
        }
    }

    /*
     * Search to see if this pipe connect to a valid neighbor
     */
    public boolean doConnect() {
        for(int d=0; d<4; d++) {
            /*
             * If no connection this direction, ignore
             */
            if (!getConnect(d)) {
                continue;
            }
            Pipe n = neighbor(d);
            //  if no neighbor ignore
            if(n == null) {
                continue;
            }
            if (n.getPlayerId() != playerId) {
                // The neighbor is not ours, ignore
                continue;
            }
            // What is the matching location on
            // the other pipe. For example, if
            // we are looking in direction 1 (east),
            // the other pipe must have a connection
            // in direction 3 (west)
            int dp = (d + 2) % 4;
            if(n.getConnect(dp)) {
                // We have a good connection, the other side is
                // a flange to connect to
                return true;
            }

        }
        return false;
    }
    /**
     * Search to see if there are any downstream of this pipe
     *
     * This does a simple depth-first search to find any connections
     * that are not, in turn, connected to another pipe. It also
     * set the visited flag in all pipes it does visit, so you can
     * tell if a pipe is reachable from this pipe by checking that flag.
     * @return True if no leaks in the pipe
     */
    public boolean search(ArrayList<OpenFlange> openFlanges) {
        visited = true;

        for(int d=0; d<4; d++) {
            /*
             * If no connection this direction, ignore
             */
            if(!getConnect(d)) {
                continue;
            }

            Pipe n = neighbor(d);
            if(n == null) {
                // We leak
                // We have a connection with nothing on the other side
                openFlanges.add(new OpenFlange(this, x,y,d));
                continue;
            }

            // What is the matching location on
            // the other pipe. For example, if
            // we are looking in direction 1 (east),
            // the other pipe must have a connection
            // in direction 3 (west)
            int dp = (d + 2) % 4;
            if(!n.getConnect(dp)) {
                // We have a bad connection, the other side is not
                // a flange to connect to
                openFlanges.add(new OpenFlange(this, x,y,d));
                //continue;
                //return false;
            }

            if(!n.visited) {
                // Is there a lead in that direction
                if(!n.search(openFlanges)) {
                    // We found a leak downstream of this pipe
                    //return false;
                    continue;
                }
            }
            // if get here already visited this pip, so no leaks this way
            //  So just continue

        }

        // Yah, no leaks
        return openFlanges.size() <= 0;
    }

    /**
     * Find the neighbor of this pipe
     * @param d Index (north=0, east=1, south=2, west=3)
     * @return Pipe object or null if no neighbor
     */
    private Pipe neighbor(int d) {
        int neighbor_x = x;
        int neighbor_y = y;
        switch(d) {
            case 0:
                neighbor_y = y - 1;
                break;
            case 1:
                neighbor_x = x + 1;
                break;
            case 2:
                neighbor_y = y + 1;
                break;
            case 3:
               neighbor_x = x - 1;
               break;
        }
        //
        //  Make sure we do not index out of the playing area
        //
        if (neighbor_x>= 0 && neighbor_x < playingArea.getWidth() && neighbor_y >=0 && neighbor_y < playingArea.getHeight()) {
            return playingArea.getPipe(neighbor_x, neighbor_y);
        }
        return null;
    }

    /**
     * Get the playing area
     * @return Playing area object
     */
    public PlayingArea getPlayingArea() {
        return playingArea;
    }

    /**
     * Set the playing area and location for this pipe
     * @param playingArea Playing area we are a member of
     * @param x X index
     * @param y Y index
     */
    public void set(PlayingArea playingArea, int x, int y) {
        this.playingArea = playingArea;
        this.x = x;
        this.y = y;
    }

    /**
     * Has this pipe been visited by a search?
     * @return True if yes
     */
    public boolean beenVisited() {
        return visited;
    }

    /**
     * Set the visited flag for this pipe
     * @param visited Value to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
