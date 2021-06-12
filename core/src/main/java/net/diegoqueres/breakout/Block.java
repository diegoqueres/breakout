package net.diegoqueres.breakout;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Block {
    int x, y, width, height;
    boolean destroyed;

    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.destroyed = false;
    }

    public void draw(ShapeRenderer shape) {
        shape.rect(x, y, width, height);
    }
}
