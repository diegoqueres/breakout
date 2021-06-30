package net.diegoqueres.breakout.elements.impl;

import com.badlogic.gdx.graphics.Color;
import net.diegoqueres.breakout.elements.Rectangle;

public class Block extends Rectangle {
    public static final Color[] BLOCK_COLORS = new Color[]{
            Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED
    };

    public boolean destroyed;

    public Block(Color color, int x, int y, int width, int height) {
        super(x, y, width, height, color);
        this.destroyed = false;
    }
}
