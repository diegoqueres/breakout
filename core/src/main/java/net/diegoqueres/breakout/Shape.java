package net.diegoqueres.breakout;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Shape {
    void correctIfInInvalidPosition();
    boolean isOutOfBoundsX();
    boolean isOutOfBoundsY();
    boolean isOutOfLeftCorner();
    boolean isOutOfRightCorner();
    boolean isOutOfTopCorner();
    boolean isOutOfBottomCorner();
    void draw(ShapeRenderer shape);
}
