package net.diegoqueres.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.diegoqueres.breakout.geometry.Point;
import net.diegoqueres.breakout.geometry.Rectangle;

public class Ball implements Shape {
    public static final int DEFAULT_SIZE = 9;
    public static final int DEFAULT_X_SPEED = 4;
    public static final int DEFAULT_Y_SPEED = 4;

    int x;
    int y;
    int r;
    int size;
    int xSpeed;
    int ySpeed;
    Color color = Color.WHITE;


    public Ball(int x, int y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_X_SPEED, DEFAULT_Y_SPEED);
    }

    public Ball(int x, int y, int size, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.r = size;
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        correctIfInInvalidPosition();
    }

    public void correctIfInInvalidPosition() {
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
        shape.setColor(color);
        shape.circle(x, y, size);
    }

    public void checkCollision(Paddle paddle, Sound hitSound) {
        if (collidesWith(paddle)) {
            this.ySpeed *= -1;
            hitSound.play(0.3f);
        }
    }

    public void checkCollision(Block block, Sound hitSound) {
        if (collidesWith(block)) {
            this.ySpeed = - this.ySpeed;
            block.destroyed = true;
            hitSound.play(0.3f);
        }
    }

    public boolean collidesWith(Paddle paddle) {
        Rectangle rect = new Rectangle();
        rect.x = paddle.x + (paddle.width/2);
        rect.y = paddle.y + (paddle.height/2);
        rect.width = paddle.width;
        rect.height = paddle.height;
        Point circleDistance = new Point();
        circleDistance.x = Math.abs(this.x - rect.x);
        circleDistance.y = Math.abs(this.y - rect.y);

        if (circleDistance.x > (rect.width/2 + this.r)) { return false; }
        if (circleDistance.y > (rect.height/2 + this.r)) { return false; }

        if (circleDistance.x <= (rect.width/2)) { return true; }
        if (circleDistance.y <= (rect.height/2)) { return true; }

        double cornerDistance_sq = Math.pow((circleDistance.x - rect.width/2), 2) +
                Math.pow((circleDistance.y - rect.height/2), 2);

        return (cornerDistance_sq <= Math.pow(this.r, 2));
    }

    public boolean collidesWith(Block block) {
        Rectangle rect = new Rectangle();
        rect.x = block.x + (block.width/2);
        rect.y = block.y + (block.height/2);
        rect.width = block.width;
        rect.height = block.height;
        Point circleDistance = new Point();
        circleDistance.x = Math.abs(this.x - rect.x);
        circleDistance.y = Math.abs(this.y - rect.y);

        if (circleDistance.x > (rect.width/2 + this.r)) { return false; }
        if (circleDistance.y > (rect.height/2 + this.r)) { return false; }

        if (circleDistance.x <= (rect.width/2)) { return true; }
        if (circleDistance.y <= (rect.height/2)) { return true; }

        double cornerDistance_sq = Math.pow((circleDistance.x - rect.width/2), 2) +
                Math.pow((circleDistance.y - rect.height/2), 2);

        return (cornerDistance_sq <= Math.pow(this.r, 2));
    }
}
