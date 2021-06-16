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
    enum GameState { GAME_PLAY, GAME_OVER }

    List<Block> blocks = new ArrayList<>();
    Paddle paddle;
    Ball ball;
    Sound hitBlockSound;
    Sound hitPaddleSound;
    Sound deathSound;
    Sound bonusSound;

    Player player;
    GameState gameState;
    int highScore;
    float timeIndicateBonusEarned;

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
        bonusSound = Gdx.audio.newSound(Gdx.files.internal("bonus.wav"));

        startGameElements();
        highScore = 0;

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        layout = new GlyphLayout();
    }

    private void startGameElements() {
        gameState = GameState.GAME_PLAY;
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
        timeIndicateBonusEarned = 0;
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
            if (block.destroyed) {
                if (ball.countBlocksCollided() > 1) {
                    hitBlockSound.play(0.05f);
                    bonusSound.play(0.30f);
                } else {
                    hitBlockSound.play(0.3f);
                }
            }
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
            if (player.isDead()) deathSound.play(0.3f);
        }
        drawScore();
        drawHighScore();
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
        Color scoreColor = Color.WHITE;
        if (timeIndicateBonusEarned > 0) {
            scoreColor = Color.YELLOW;
        }
        layout.setText(bitmapFont, String.valueOf(player.score), scoreColor, 0, Align.left,true);
        float padding = 15f;
        float x = Gdx.graphics.getWidth() - padding - layout.width;
        float y = Gdx.graphics.getHeight() - padding;
        bitmapFont.draw(batch, layout, x, y);
    }

    private void drawHighScore() {
        layout.setText(bitmapFont, String.valueOf(highScore), Color.WHITE, Gdx.graphics.getWidth(), Align.center,true);
        float padding = 15f;
        float x = 0;
        float y = Gdx.graphics.getHeight() - padding;
        bitmapFont.draw(batch, layout, x, y);
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
            } else {
                gameState = GameState.GAME_OVER;
                updateHighScore();
                if (Gdx.input.isTouched()) {      // restart
                    startGameElements();
                }
            }
            return;
        }
        gameState = GameState.GAME_PLAY;
        updateBlocks();
        updateIndicators();

        int x = Gdx.input.getX();
        paddle.updatePosition(x);

        ball.checkCollision(paddle);
        for (Block block : blocks) {
            ball.checkCollision(block);
        }
        ball.checkPlayerFall(player);

        ball.update();
    }

    private void updateHighScore() {
        if (player.score > highScore)
            highScore = player.score;
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
        if (blocksCollided > 1) {        //bonus
            timeIndicateBonusEarned = 2L;
            points += Player.SCORE_UNIT * blocksCollided;
        }
        player.incrementScore(points);
    }

    private void updateIndicators() {
        if (timeIndicateBonusEarned > 0f) {
            timeIndicateBonusEarned -= Gdx.graphics.getDeltaTime();
            if (timeIndicateBonusEarned < 0f)
                timeIndicateBonusEarned = 0f;
        }
    }
}
