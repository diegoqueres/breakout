package net.diegoqueres.breakout.elements.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import net.diegoqueres.breakout.elements.Rectangle;
import net.diegoqueres.breakout.elements.GameElement;

public class Paddle extends Rectangle implements GameElement {
    public static final int DEFAULT_BOTTOM_CORNER = 15;
    public static final int DEFAULT_WIDTH = 120;
    public static final int DEFAULT_HEIGHT = 15;
    public static final int DEFAULT_CONTROLLER_VELOCITY = 9;
    public static final Color PADDLE_COLOR = Color.LIGHT_GRAY;

    public int controllerVelocity;

    public Paddle(int x) {
        this(x, DEFAULT_BOTTOM_CORNER);
    }

    public Paddle(int x, int y) {
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Paddle(int x, int y, int width, int height) {
        super(x, y, width, height, PADDLE_COLOR);
        controllerVelocity = DEFAULT_CONTROLLER_VELOCITY;
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

    public void updatePosition(int x) {
        this.x = x - (width/2);
        correctIfInInvalidPosition();
    }

    public void increaseControllerVelocity() {
        controllerVelocity++;
    }

    public void decreaseControllerVelocity() {
        controllerVelocity--;
    }
}
