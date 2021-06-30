package net.diegoqueres.breakout.elements;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface GameElement {
    void correctIfInInvalidPosition();
    boolean isOutOfBoundsX();
    boolean isOutOfBoundsY();
    boolean isOutOfLeftCorner();
    boolean isOutOfRightCorner();
    boolean isOutOfTopCorner();
    boolean isOutOfBottomCorner();
    void draw(ShapeRenderer shape);
}
