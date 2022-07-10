package edu.msu.srijithv.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A representation of the playing area
 */
@SuppressWarnings("ALL")
public class PlayingArea implements Serializable {
    //
    //  This list is used to keep track of open flanges
    //  during a connect search when the valve is openned
    //  by a player. At the end of the search steam will be
    //  drawn at any open flanges in this list.
    //  This list should be emptied at the start of a search.
    //
    public transient static ArrayList<OpenFlange> openFlanges;
    /**
     * Width of the playing area (integer number of cells)
     */
    private final int width;

    /**
     * Height of the playing area (integer number of cells)
     */
    private final int height;

    /**
     * Storage for the pipes
     * First level: X, second level Y
     */
    private Pipe [][] pipes;
    //
    //  paint for drawing the background and border
    private transient Paint fillPaint;
    private transient Paint outlinePaint;
    /**
     * Construct a playing area
     * @param width Width (integer number of cells)
     * @param height Height (integer number of cells)
     */
    public PlayingArea(Context context, int width, int height) {
        this.width = width;
        this.height = height;

        // Allocate the playing area
        // Java automatically initializes all of the locations to null
        pipes = new Pipe[width][height];
        init(context);
    }
    public void init(Context context) {
        // Create paint for filling the area the puzzle will
        // be solved in.
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xffcccccc);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0xff008000);
        outlinePaint.setStyle(Paint.Style.STROKE);
        for (Pipe[] row: pipes) {
            for (Pipe pipe: row) {
                if (pipe != null) {
                    pipe.setPipeId(context, pipe.getPipeId());
                }
            }
        }
        openFlanges = new ArrayList<>();
    }
    /**
     * Get the playing area height
     * @return Height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the playing area width
     * @return Width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the pipe at a given location.
     * This will return null if outside the playing area.
     * @param x X location
     * @param y Y location
     * @return Reference to Pipe object or null if none exists
     */
    public Pipe getPipe(int x, int y) {
        if(x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }

        return pipes[x][y];
    }

    /**
     * Add a pipe to the playing area
     * @param pipe Pipe to add
     * @param x X location
     * @param y Y location
     */
    public void add(Pipe pipe, int x, int y) {
        pipes[x][y] = pipe;
        pipe.set(this, x, y);
    }
    /**
     * remove a pipe from the playing area
     * @param pipe Pipe to remove
     * @param x X location
     * @param y Y location
     */
    public void remove(Pipe pipe, int x, int y) {
        pipes[x][y] = null;
        pipe.clearArea();
    }
    /**
     * Search to determine if this pipe has no leaks
     * @param start Starting pipe to search from
     * @return true if no leaks
     */
    public boolean search(Pipe start) {
        /*
         * Set the visited flags to false
         */
        for(Pipe[] row: pipes) {
            for(Pipe pipe : row) {
                if (pipe != null) {
                    pipe.setVisited(false);
                }
            }
        }
        openFlanges = new ArrayList<>();
        return start.search(openFlanges);
    }
    //
    //  Draw the playing area
    //  This does not include the pipes that the current player
    //  can pick.
    //
    public void draw(Canvas canvas, int marginX, int marginY, int puzzleSize, int scaleFactor) {
        //
        //  Draw the playing area background color and a border
        //
        canvas.drawRect(marginX, marginY,
                marginX + puzzleSize, marginY + puzzleSize, fillPaint);
        canvas.drawRect(marginX, marginY, marginX + puzzleSize, marginY + puzzleSize, outlinePaint);
        float pieceSize = puzzleSize / (float)height;
        //
        //  Draw grid lines to help debug snapping
        /*
        for (int i = 1; i < height; i++) {
            canvas.drawLine(marginX, marginY + i * pieceSize, marginX + puzzleSize, marginY + i * pieceSize, outlinePaint);
            canvas.drawLine(marginX + i * pieceSize, marginY, marginX + i * pieceSize, marginY + puzzleSize, outlinePaint);
        }
        */
        //
        //  Draw any pipes that have been added to the playing area
        //
        for(Pipe[] row: pipes) {
            for(Pipe pipe : row) {
                if (pipe != null) {
                    pipe.draw(canvas, marginX, marginY, puzzleSize, scaleFactor);
                }
            }
        }
    }
}
