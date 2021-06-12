package net.diegoqueres.breakout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Breakout extends ApplicationAdapter {
    ShapeRenderer shape;
    List<Block> blocks = new ArrayList<>();
    Paddle paddle;
    Ball ball;
    Sound hitBlockSound;
    Sound hitPaddleSound;
    Random r = new Random();

    @Override
    public void create() {
        //Game elements
        int blockWidth = 63;
        int blockHeight = 20;
        int blockPadding = 10;
        for (int y = Gdx.graphics.getHeight() / 2; y < Gdx.graphics.getHeight(); y += blockHeight + blockPadding) {
            for (int x = 0; x < Gdx.graphics.getWidth(); x += blockWidth + blockPadding) {
                Color colBlockColor = Block.BLOCK_COLORS[x % Block.BLOCK_COLORS.length];
                blocks.add(new Block(colBlockColor, x, y, blockWidth, blockHeight));
            }
        }
        ball = new Ball(r.nextInt(Gdx.graphics.getWidth()), r.nextInt(Gdx.graphics.getHeight()/2 - blockPadding));
        paddle = new Paddle(Gdx.graphics.getWidth()/2);

        //assets
        hitBlockSound = Gdx.audio.newSound(Gdx.files.internal("hit_block.wav"));
        hitPaddleSound = Gdx.audio.newSound(Gdx.files.internal("hit_paddle.wav"));

        shape = new ShapeRenderer();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        draw();
        update();
    }

    private void draw() {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (Block block : blocks) {
            block.draw(shape);
        }
        ball.draw(shape);
        paddle.draw(shape);
        shape.end();
    }

    private void update() {
        int x = Gdx.input.getX();
        paddle.updatePosition(x);
        ball.checkCollision(paddle, hitPaddleSound);

        for (Block block : blocks) {
            ball.checkCollision(block, hitBlockSound);
        }
        updateBlocks();

        ball.update();
    }

    private void updateBlocks() {
    	for (int i = 0; i < blocks.size(); i++) {
    		Block b = blocks.get(i);
    		if (b.destroyed) {
    			blocks.remove(b);
				// we need to decrement i when a ball gets removed, otherwise we skip a ball!
    			i--;
			}
		}
	}
}
