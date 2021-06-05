package net.diegoqueres.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Paddle implements Shape {
    public static final int DEFAULT_BOTTOM_CORNER = 15;
    public static final int DEFAULT_WIDTH = 120;
    public static final int DEFAULT_HEIGHT = 15;

    int x;
    int y;
    int width;
    int height;

    public Paddle(int x) {
        this(x, DEFAULT_BOTTOM_CORNER, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        correctIfInInvalidPosition();
    }

    public void correctIfInInvalidPosition() {
        if (isOutOfLeftCorner())
            this.x = 0;
        if (isOutOfRightCorner())
            this.x = Gdx.graphics.getWidth() - width;
        if (isOutOfBottomCorner())
            this.y = DEFAULT_BOTTOM_CORNER;
        if (isOutOfTopCorner())
            this.y = Gdx.graphics.getHeight() - height;
    }

    public boolean isOutOfBoundsX() {
        return (isOutOfLeftCorner() || isOutOfRightCorner());
    }

    public boolean isOutOfBoundsY() {
        return (isOutOfBottomCorner() || isOutOfTopCorner());
    }

    public boolean isOutOfLeftCorner() { return x < 0; }

    public boolean isOutOfRightCorner() {
        return (x + width) > Gdx.graphics.getWidth();
    }

    public boolean isOutOfTopCorner() {
        return (y + height) > Gdx.graphics.getHeight();
    }

    public boolean isOutOfBottomCorner() { return y < DEFAULT_BOTTOM_CORNER; }

    public void draw(ShapeRenderer shape) {
        shape.setColor(Color.WHITE);
        shape.rect(x, y, width, height);
    }

    public void updatePosition(int x) {
        this.x = x - (width/2);
        correctIfInInvalidPosition();
    }
}
