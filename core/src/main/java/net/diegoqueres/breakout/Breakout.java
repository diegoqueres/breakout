package net.diegoqueres.breakout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Breakout extends ApplicationAdapter {
    enum GameState { GAME, GAME_OVER }

    List<Block> blocks = new ArrayList<>();
    Paddle paddle;
    Ball ball;
    Sound hitBlockSound;
    Sound hitPaddleSound;
    Sound deathSound;

    Player player;
    GameState gameState;

    BitmapFont bitmapFont;
    GlyphLayout layout;
    SpriteBatch batch;
    ShapeRenderer shape;
    Random r = new Random();

    @Override
    public void create() {
        //assets
        hitBlockSound = Gdx.audio.newSound(Gdx.files.internal("hit_block.wav"));
        hitPaddleSound = Gdx.audio.newSound(Gdx.files.internal("hit_paddle.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("death.wav"));

        startGameElements();

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        layout = new GlyphLayout();
    }

    private void startGameElements() {
        gameState = GameState.GAME;
        int gameWidth = Gdx.graphics.getWidth();
        int gameHeight = Gdx.graphics.getHeight();

        //Game elements
        int blockWidth = 63;
        int blockHeight = 20;
        int blockPadding = 10;
        for (int y = gameHeight / 2; y < gameHeight; y += blockHeight + blockPadding) {
            for (int x = 0; x < gameWidth; x += blockWidth + blockPadding) {
                Color colBlockColor = Block.BLOCK_COLORS[x % Block.BLOCK_COLORS.length];
                blocks.add(new Block(colBlockColor, x, y, blockWidth, blockHeight));
            }
        }
        ball = new Ball(r.nextInt(gameWidth), r.nextInt(gameHeight/2 - blockPadding));
        paddle = new Paddle(gameWidth/2);

        player = new Player();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        draw();
        update();
    }

    private void draw() {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setAutoShapeType(true);
        for (Block block : blocks) {
            block.draw(shape);
            if (block.destroyed)
                hitBlockSound.play(0.3f);
        }
        paddle.draw(shape);
        if (ball.rectsCollided.contains(paddle, false)) {
            hitPaddleSound.play(0.3f);
        }
        ball.draw(shape);
        drawLives(shape);
        shape.end();


        batch.begin();
        if (gameState == GameState.GAME_OVER) {
            drawGameOver();
        } else {
            if (player.isDead()) deathSound.play(0.33f);
        }
        drawScore();
        batch.end();
    }

    private void drawLives(ShapeRenderer shape) {
        int xPos = 75;
        shape.setColor(Color.WHITE);
        ShapeRenderer.ShapeType oldShapeType = shape.getCurrentType();
        for (int i = Player.MAX_LIFES; i > 0; i--) {
            if (player.lives >= i) {
                shape.set(ShapeRenderer.ShapeType.Filled);
            } else {
                shape.set(ShapeRenderer.ShapeType.Line);
            }
            shape.circle(xPos, Gdx.graphics.getHeight() - 15, 10);
            xPos -= 25;
        }
        shape.set(oldShapeType);
    }

    private void drawScore() {
        int scoreLength = (int) (Math.log10(player.score) + 1);
        int rightPadding = 15 + (scoreLength * 10);

        bitmapFont.setColor(Color.WHITE);
        bitmapFont.draw(batch, String.format("%d", player.score), Gdx.graphics.getWidth() - rightPadding, Gdx.graphics.getHeight() - 15);
    }

    private void drawGameOver() {
        layout.setText(bitmapFont, "Game Over", Color.RED, Gdx.graphics.getWidth(), Align.center,true);
        float x = 0;
        float y = Gdx.graphics.getHeight()/2 + layout.height/2;
        bitmapFont.draw(batch, layout, x, y);
    }

    private void update() {
        if (player.isDead()) {
            if (player.lives >= 0) {
                ball.x = r.nextInt(Gdx.graphics.getWidth());
                ball.y = r.nextInt(Gdx.graphics.getHeight() / 2 - 10);
                player.reanimate();
            }
            gameState = GameState.GAME_OVER;

            // restart
            if (Gdx.input.isTouched()) {
                startGameElements();
            }
            return;
        }

        gameState = GameState.GAME;
        updateBlocks();

        int x = Gdx.input.getX();
        paddle.updatePosition(x);

        ball.checkCollision(paddle);
        for (Block block : blocks) {
            ball.checkCollision(block);
        }
        ball.checkPlayerFall(player);

        ball.update();
    }

    private void updateBlocks() {
    	for (int i = 0; i < blocks.size(); i++) {
    		Block b = blocks.get(i);
    		if (b.destroyed) {
                updateScore();
                blocks.remove(b);
				i--; // we need to decrement i when a ball gets removed, otherwise we skip a ball!
			}
		}
	}

    private void updateScore() {
        int blocksCollided = ball.countBlocksCollided();
        int points = Player.SCORE_UNIT;
        if (blocksCollided > 1)         //bonus
            points += Player.SCORE_UNIT * blocksCollided;
        player.incrementScore(points);
        Gdx.app.log("Points", "Score: " + player.score + ". Points: " + String.valueOf(points) + ". Collisions: " + blocksCollided);
    }
}
