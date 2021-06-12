package net.diegoqueres.breakout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Block {
    public static final Color[] BLOCK_COLORS = new Color[]{
            Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED
    };

    int x, y, width, height;
    Color color;
    boolean destroyed;

    public Block(Color color, int x, int y, int width, int height) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.destroyed = false;
    }

    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.rect(x, y, width, height);
    }
}
