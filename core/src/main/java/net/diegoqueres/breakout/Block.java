package net.diegoqueres.breakout;

import com.badlogic.gdx.graphics.Color;
import net.diegoqueres.breakout.geometry.Rectangle;

public class Block extends Rectangle {
    public static final Color[] BLOCK_COLORS = new Color[]{
            Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED
    };

    boolean destroyed;

    public Block(Color color, int x, int y, int width, int height) {
        super(x, y, width, height, color);
        this.destroyed = false;
    }
}
