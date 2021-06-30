package net.diegoqueres.breakout.elements.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import net.diegoqueres.breakout.elements.GameElement;
import net.diegoqueres.breakout.elements.Point;
import net.diegoqueres.breakout.elements.Rectangle;

public class Ball implements GameElement {
    public static final int DEFAULT_SIZE = 9;
    public static final int DEFAULT_X_SPEED = 4;
    public static final int DEFAULT_Y_SPEED = 4;
    public static final Color DEFAULT_COLOR = Color.WHITE;

    public int x;
    public int y;
    public int r;
    public int size;
    public int xSpeed;
    public int ySpeed;
    public int speed;
    public Color color;

    public Array<Rectangle> rectsCollided;

    public Ball() {
        color = new Color(DEFAULT_COLOR);
        rectsCollided = new Array<>();
    }

    public Ball(int x, int y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_X_SPEED, DEFAULT_Y_SPEED);
    }

    public Ball(int x, int y, int size, int xSpeed, int ySpeed) {
        this();
        this.x = x;
        this.y = y;
        this.r = size;
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.speed = Math.abs(xSpeed);
        correctIfInInvalidPosition();
    }

    public void correctIfInInvalidPosition() {
        if (isOutOfLeftCorner())
            this.x += (size - x);
        if (isOutOfRightCorner())
            this.x -= (size - x);
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

        clearCollisions();
    }

    public boolean isOutOfBoundsX() {
        return (isOutOfLeftCorner() || isOutOfRightCorner());
    }

    public boolean isOutOfBoundsY() {
        return isOutOfTopCorner();
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
        shape.setColor(color);
        shape.circle(x, y, size);
    }

    public boolean isBallFall() {
        return isOutOfBottomCorner();
    }

    public void checkCollision(Rectangle rect) {
        if (collidesWith(rect)) {
            this.ySpeed *= -1;
            rectsCollided.add(rect);
            if (rect instanceof Block)
                ((Block) rect).destroyed = true;
        }
    }

    public void clearCollisions() {
        for (Rectangle rect : rectsCollided) {
            if (rect instanceof Paddle) {
                this.rectsCollided.clear();
                break;
            }
        }
    }

    public int countBlocksCollided() {
        int count = 0;
        for (Rectangle rect : rectsCollided) {
            if (rect instanceof Block) count++;
        }
        return count;
    }

    public boolean collidesWith(Rectangle rect) {
        Point circleDistance = new Point();
        circleDistance.x = Math.abs(this.x - rect.getOriginX());
        circleDistance.y = Math.abs(this.y - rect.getOriginY());

        if (circleDistance.x > (rect.width/2 + this.r)) { return false; }
        if (circleDistance.y > (rect.height/2 + this.r)) { return false; }

        if (circleDistance.x <= (rect.width/2)) { return true; }
        if (circleDistance.y <= (rect.height/2)) { return true; }

        double cornerDistance_sq = Math.pow((circleDistance.x - rect.width/2), 2) +
                Math.pow((circleDistance.y - rect.height/2), 2);

        return (cornerDistance_sq <= Math.pow(this.r, 2));
    }

    public void slowMotion() {
        if (isSlowMotion()) return;
        this.xSpeed = this.xSpeed/2;
        this.ySpeed = this.ySpeed/2;
    }

    public boolean isSlowMotion() {
        return (Math.abs(this.xSpeed) < speed && Math.abs(this.ySpeed) < speed);
    }

    public void normalizeVelocity() {
        if (isDefaultSpeed()) return;
        this.xSpeed = (this.xSpeed < 0 ? -speed : speed);
        this.ySpeed = (this.ySpeed < 0 ? -speed : speed);
    }

    public boolean isDefaultSpeed() {
        return (Math.abs(this.xSpeed) == speed && Math.abs(this.ySpeed) == speed);
    }

    public void increaseVelocity() {
        if (isSlowMotion())
            normalizeVelocity();
        int newSpeed = Math.abs(speed) + 1;
        this.xSpeed = (xSpeed >= 0 ? newSpeed : -newSpeed);
        this.ySpeed = (ySpeed >= 0 ? newSpeed : -newSpeed);
        speed = newSpeed;
    }
    public void decreaseVelocity(int value) {
        if (isSlowMotion())
            normalizeVelocity();
        int newSpeed = Math.abs(speed) - value;
        this.xSpeed = (xSpeed >= 0 ? newSpeed : -newSpeed);
        this.ySpeed = (ySpeed >= 0 ? newSpeed : -newSpeed);
        speed = newSpeed;
    }
}
