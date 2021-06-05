package net.diegoqueres.breakout.geometry;

public class Rectangle {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle() {}

    public Rectangle(int x, int y, int width, int height) {
        this.x = x + (width/2);
        this.y = y + (height/2);
        this.width = width;
        this.height = height;
    }

    public boolean collidesWith(Rectangle r2) {
        if ((x + width >= r2.x) && (x <= r2.x + r2.width)
            && (y + height >= r2.y) && (y <= r2.y + r2.height))
            return true;
        else
            return false;
    }
}
