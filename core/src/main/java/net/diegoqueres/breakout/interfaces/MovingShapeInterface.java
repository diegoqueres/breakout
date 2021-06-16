package net.diegoqueres.breakout.interfaces;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface MovingShapeInterface {
    void correctIfInInvalidPosition();
    boolean isOutOfBoundsX();
    boolean isOutOfBoundsY();
    boolean isOutOfLeftCorner();
    boolean isOutOfRightCorner();
    boolean isOutOfTopCorner();
    boolean isOutOfBottomCorner();
    void draw(ShapeRenderer shape);
}
