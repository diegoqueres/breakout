package net.diegoqueres.breakout.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Rectangle {
    public int x;
    public int y;
    public int originX;
    public int originY;
    public int width;
    public int height;
    public Color color;

    public Rectangle() {}

    public Rectangle(int x, int y, int width, int height) {
        this(x, y, width, height, null);
    }

    public Rectangle(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = new Color(color);
    }

    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.rect(x, y, width, height);
    }

    public boolean collidesWith(Rectangle r2) {
        if ((originX + width >= r2.originX) && (originX <= r2.originX + r2.width)
            && (originY + height >= r2.originY) && (originY <= r2.originY + r2.height))
            return true;
        else
            return false;
    }

    public int getOriginX() {
        return x + (width/2);
    }

    public int getOriginY() {
        return y + (height/2);
    }
}
