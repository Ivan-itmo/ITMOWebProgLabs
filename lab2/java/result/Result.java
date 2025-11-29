package result;

import java.io.Serializable;

public class Result implements Serializable {
    private String result;
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private long time;

    public Result(double x, double y, double r, boolean hit, long time) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.time = time;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public boolean isHit() { return hit; }
    public long getTime() { return time; }
}
