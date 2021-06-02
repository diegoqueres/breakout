package net.diegoqueres.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

public class Ball {
    public static final int DEFAULT_SIZE = 10;
    public static final int DEFAULT_X_SPEED = 3;
    public static final int DEFAULT_Y_SPEED = 3;

    int x;
    int y;
    int size;
    int xSpeed;
    int ySpeed;


    public Ball(int x, int y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_X_SPEED, DEFAULT_Y_SPEED);
    }

    public Ball(int x, int y, int size, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.size = size;
        correctIfInInvalidPosition();
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    private void correctIfInInvalidPosition() {
        if (isOutOfLeftCorner())
            this.x += (size - x);
        if (isOutOfRightCorner())
            this.x -= (size - x);
        if (isOutOfBottomCorner())
            this.y += (size - y);
        if (isOutOfTopCorner())
            this.y -= (size - y);
    }

    public void update() {
        x += xSpeed;
        y += ySpeed;
        if (isOutOfBoundsX())
            xSpeed = -xSpeed;
        if (isOutOfBoundsY())
            ySpeed = -ySpeed;
    }

    public boolean isOutOfBoundsX() {
        return (isOutOfLeftCorner() || isOutOfRightCorner());
    }

    public boolean isOutOfBoundsY() {
        return (isOutOfBottomCorner() || isOutOfTopCorner());
    }

    public boolean isOutOfLeftCorner() {
        return (x - size) < 0;
    }

    public boolean isOutOfRightCorner() {
        return (x + size) > Gdx.graphics.getWidth();
    }

    public boolean isOutOfTopCorner() {
        return (y + size) > Gdx.graphics.getHeight();
    }

    public boolean isOutOfBottomCorner() {
        return (y - size) < 0;
    }

    public void draw(ShapeRenderer shape) {
        shape.circle(x, y, size);
    }
}
