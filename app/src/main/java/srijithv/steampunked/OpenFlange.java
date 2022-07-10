package edu.msu.srijithv.steampunked;
//
//  This class is used to keep track of an open flange
//  during the connected search.
//
public class OpenFlange {
    public final Pipe pipe;
    public final int x;
    public final int y;
    public final int direction;
    public OpenFlange(Pipe pipe, int x, int y, int direction) {
        this.pipe = pipe;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}
