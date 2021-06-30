package net.diegoqueres.breakout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import net.diegoqueres.breakout.effects.Effect;
import net.diegoqueres.breakout.effects.EffectTimer;
import net.diegoqueres.breakout.effects.EffectTimerControl;
import net.diegoqueres.breakout.elements.impl.Ball;
import net.diegoqueres.breakout.elements.impl.Block;
import net.diegoqueres.breakout.elements.impl.Paddle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.badlogic.gdx.controllers.Controllers.getControllers;
import static net.diegoqueres.breakout.Constants.*;

public class Breakout extends ApplicationAdapter {
    enum GameState {LOADING_LEVEL, MAIN_GAME, GAME_OVER}

    Array<Block> blocks;
    Array<Ball> balls;
    Paddle paddle;
    Sound hitBlockSound;
    Sound hitPaddleSound;
    Sound deathSound;
    Sound bonusSound;
    Array<Music> levelMusics;

    Player player;
    GameState gameState;
    Controller controller;
    int highScore;

    EffectTimerControl effectTimerControl;

    int initialBlockY;
    int targetBlockY;
    int targetBlockDeslocY;
    Map<Block, Integer> mapBlockTargets;

    BitmapFont bitmapFont;
    Music levelMusic;
    GlyphLayout layout;
    SpriteBatch batch;
    ShapeRenderer shape;
    Random r = new Random();

    boolean audioEnabled;

    @Override
    public void create() {
        audioEnabled = true;
        loadAudioAssets();

        blocks = new Array<>();
        balls = new Array<>();
        player = new Player();
        effectTimerControl = new EffectTimerControl();

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        layout = new GlyphLayout();
        configControllers();

        highScore = 0;
        initGameLevel(Boolean.FALSE);
    }

    private void loadAudioAssets() {
        levelMusics = new Array();
        for (int i = 0; i < LevelMusic.values().length; i++) {
            FileHandle fileHandle = Gdx.files.internal("musics/" + LevelMusic.values()[i].getFileName());
            Music music = Gdx.audio.newMusic(fileHandle);
            levelMusics.add(music);
        }

        hitBlockSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit_block.ogg"));
        hitPaddleSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit_paddle.ogg"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/death.ogg"));
        bonusSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bonus.ogg"));
    }

    private void initGameLevel(boolean isLevelChange) {
        gameState = GameState.LOADING_LEVEL;
        int gameWidth = Gdx.graphics.getWidth();
        int gameHeight = Gdx.graphics.getHeight();

        targetBlockDeslocY = 200;
        initialBlockY = (gameHeight / 2) + targetBlockDeslocY;
        targetBlockY = gameHeight / 2;

        initBlocks(initialBlockY, BLOCKS_PADDING);
        initBalls(isLevelChange);
        paddle = new Paddle(gameWidth / 2);

        startPlayLevelMusic();
    }

    private void startPlayLevelMusic() {
        int level = player.getLevel();
        if (levelMusic != null && levelMusic.isPlaying()) {
            levelMusic.stop();
        }

        int levelIdx = ((level - 1) % LEVEL_MULTIPLE_ADD_BALL);
        LevelMusic levelMusicData = LevelMusic.getByLevelMultiple(levelIdx + 1);
        levelMusic = levelMusics.get(levelIdx);
        levelMusic.setVolume(levelMusicData.getVolume());
        levelMusic.setPosition(0f);
        toggleLevelMusic();
    }

    private void initBalls(boolean isLevelChange) {
        int gameWidth = Gdx.graphics.getWidth();
        int gameHeight = Gdx.graphics.getHeight();
        int level = player.getLevel();

        if (isLevelChange) {
            initBallsPosition();
            increaseDifficult(level);
        } else {
            balls.clear();
            balls.add(new Ball(r.nextInt(gameWidth), r.nextInt(gameHeight / 2 - BLOCKS_PADDING)));
        }

        initBallsFadeInEffect();

        if (isDebuggingEnabled())
            debugBalls();
    }

    private void initBallsPosition() {
        for (Ball ball : balls) {
            ball.x = r.nextInt(Gdx.graphics.getWidth());
            ball.y = r.nextInt((Gdx.graphics.getHeight() / 2) - BLOCKS_PADDING);
            ball.ySpeed = Math.abs(ball.ySpeed);        //avoid start level with ball falling
            removeBallEffects(ball);
        }
    }

    private void initBallsFadeInEffect() {
        for (Ball ball : balls) {
            effectTimerControl.create(Effect.BALL_FADE_IN, ball);
            float alpha = 0f;
            ball.color.a = alpha;
        }
    }

    private void increaseDifficult(int level) {
        int gameWidth = Gdx.graphics.getWidth();
        int gameHeight = Gdx.graphics.getHeight();

        if (level % LEVEL_MULTIPLE_ADD_BALL == 0 && balls.size < MAX_BALLS) {
            for (Ball ball : balls) {
                ball.decreaseVelocity(1);
            }
            balls.add(new Ball(r.nextInt(gameWidth), r.nextInt(gameHeight / 2 - BLOCKS_PADDING)));
        } else {
            for (Ball ball : balls) {
                ball.increaseVelocity();
            }
        }
    }

    private void initBlocks(int initialBlockY, int blockPadding) {
        int gameWidth = Gdx.graphics.getWidth();
        int gameHeight = Gdx.graphics.getHeight();

        final int blockWidth = 63;
        final int blockHeight = 20;
        for (int y = gameHeight / 2; y < gameHeight; y += blockHeight + blockPadding) {
            for (int x = 0; x < gameWidth; x += blockWidth + blockPadding) {
                Color colBlockColor = new Color(Block.BLOCK_COLORS[x % Block.BLOCK_COLORS.length]);
                blocks.add(new Block(colBlockColor, x, y, blockWidth, blockHeight));
            }
        }

        //repositioning blocks to animate
        mapBlockTargets = new HashMap<>();
        for (Block block : blocks) {
            mapBlockTargets.put(block, block.y);
            block.y += targetBlockDeslocY;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        switch (gameState) {
            case LOADING_LEVEL:
                draw();
                updateLoadingLevel();
                break;

            case MAIN_GAME:
            case GAME_OVER:
            default:
                draw();
                update();
        }
    }

    private void updateLoadingLevel() {
        for (Block block : blocks) {
            Integer blockTarget = mapBlockTargets.get(block);
            if (blockTarget == block.y)
                gameState = GameState.MAIN_GAME;
            else
                block.y += (blockTarget - block.y) * .1f;
        }
    }

    private void draw() {
        drawShapes();
        drawSprites();
    }

    private void drawShapes() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setAutoShapeType(true);

        for (Block block : blocks) {
            block.draw(shape);
            if (block.destroyed) {
                boolean isBallMoreThanOnce = false;
                for (Ball ball : balls) {
                    if (ball.rectsCollided.contains(block, false)) {
                        if (ball.countBlocksCollided() > 1) {
                            isBallMoreThanOnce = true;
                            if (isAudioEnabled()) {
                                hitBlockSound.play(0.05f);
                                bonusSound.play(0.30f);
                            }
                        }
                    }
                }
                if (isAudioEnabled() && !isBallMoreThanOnce) {
                    hitBlockSound.play(0.3f);
                }
            }
        }

        paddle.draw(shape);
        drawLives(shape);

        if (gameState != GameState.LOADING_LEVEL) {
            for (Ball ball : balls) {
                if (isAudioEnabled() && ball.rectsCollided.contains(paddle, false)) {
                    hitPaddleSound.play(0.3f);
                }
                ball.draw(shape);
            }
        }

        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawSprites() {
        batch.begin();
        if (gameState == GameState.GAME_OVER) {
            drawGameOver();
        } else {
            if (isAudioEnabled() && player.isDead()) deathSound.play(0.3f);
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
        Color scoreColor = new Color(Color.WHITE);
        for (Ball ball : balls) {
            EffectTimer effectTimer = effectTimerControl.getTimer(Effect.INDICATION_BONUS_EARNED, ball);
            if (effectTimer != null && !effectTimer.finished()) {
                scoreColor = new Color(Color.YELLOW);
            } else {
                effectTimerControl.removeTimer(effectTimer);
            }
        }
        layout.setText(bitmapFont, String.valueOf(player.score), scoreColor, 0, Align.left, true);
        float padding = 15f;
        float x = Gdx.graphics.getWidth() - padding - layout.width;
        float y = Gdx.graphics.getHeight() - padding;
        bitmapFont.draw(batch, layout, x, y);
    }

    private void drawHighScore() {
        layout.setText(bitmapFont, String.valueOf(highScore), Color.WHITE, Gdx.graphics.getWidth(), Align.center, true);
        float padding = 15f;
        float x = 0;
        float y = Gdx.graphics.getHeight() - padding;
        bitmapFont.draw(batch, layout, x, y);
    }

    private void drawGameOver() {
        layout.setText(bitmapFont, "Game Over", Color.RED, Gdx.graphics.getWidth(), Align.center, true);
        float x = 0;
        float y = Gdx.graphics.getHeight() / 2 + layout.height / 2;
        bitmapFont.draw(batch, layout, x, y);
    }

    private void update() {
        if (player.isDead()) {
            if (player.lives >= 0) {
                initBallsPosition();
                initBallsFadeInEffect();
                player.reanimate();
            } else {
                gameState = GameState.GAME_OVER;
                updateHighScore();
                stopLevelMusic();
                if (getUserInputRestart()) {
                    player.init();
                    initGameLevel(Boolean.FALSE);
                }
            }
            return;
        }

        gameState = GameState.MAIN_GAME;
        effectTimerControl.updateTimers();
        updateBallEffects();
        updateBlocks();
        updateLevel();

        int x = getHorizontalUserInput();
        if (x != paddle.getOriginX())
            paddle.updatePosition(x);

        readInputChangePaddleVelocity();
        readInputToggleAudio();

        int contBallFalls = 0;
        for (Ball ball : balls) {
            ball.checkCollision(paddle);
            for (Block block : blocks) {
                ball.checkCollision(block);
            }
            if (ball.isBallFall()) {
                contBallFalls++;
            }
            ball.update();
        }
        if (contBallFalls == balls.size) {
            player.dead();
        }
    }

    private void stopLevelMusic() {
        if (levelMusic != null && levelMusic.isPlaying())
            levelMusic.stop();
    }

    private int getHorizontalUserInput() {
        int x = paddle.getOriginX();

        if (controller != null) {
            boolean pressedDpadLeft = controller.getButton(controller.getMapping().buttonDpadLeft);
            boolean pressedLeftStick = controller.getButton(controller.getMapping().buttonLeftStick);
            boolean pressedDpadRight = controller.getButton(controller.getMapping().buttonDpadRight);
            boolean pressedRightStick = controller.getButton(controller.getMapping().buttonRightStick);
            if (pressedDpadLeft || pressedLeftStick) {
                x -= paddle.controllerVelocity;
            } else if (pressedDpadRight || pressedRightStick) {
                x += paddle.controllerVelocity;
            }
        } else {
            x = Gdx.input.getX();
        }

        return x;
    }

    private boolean getUserInputRestart() {
        if (Gdx.input.isTouched()) return true;
        if (controller != null) {
            return (controller.getButton(controller.getMapping().buttonStart)
                    || controller.getButton(controller.getMapping().buttonB)
                    || controller.getButton(controller.getMapping().buttonY));
        }
        return false;
    }

    private void readInputChangePaddleVelocity() {
        if (controller == null) return;
        final Integer buttonL = controller.getMapping().buttonL1;
        final Integer buttonR = controller.getMapping().buttonR1;

        if (controller.getButton(buttonL)) {
            EffectTimer timer = effectTimerControl.getTimer(Effect.BUTTON_PRESS, buttonL);
            if (timer == null || timer.finished()) {
                effectTimerControl.removeTimer(timer);
                effectTimerControl.create(Effect.BUTTON_PRESS, buttonL);
                paddle.decreaseControllerVelocity();
            }
        }
        if (controller.getButton(buttonR)) {
            EffectTimer timer = effectTimerControl.getTimer(Effect.BUTTON_PRESS, buttonR);
            if (timer == null || timer.finished()) {
                effectTimerControl.removeTimer(timer);
                effectTimerControl.create(Effect.BUTTON_PRESS, buttonR);
                paddle.increaseControllerVelocity();
            }
        }
    }

    private void readInputToggleAudio() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            EffectTimer timer = effectTimerControl.getTimer(Effect.BUTTON_PRESS, Input.Keys.S);
            if (timer == null || timer.finished()) {
                effectTimerControl.removeTimer(timer);
                effectTimerControl.create(Effect.BUTTON_PRESS, Input.Keys.S);
                toggleAudio();
                toggleLevelMusic();
            }
        }
    }

    private void updateHighScore() {
        if (player.score > highScore)
            highScore = player.score;
    }

    private void updateBlocks() {
        for (int i = 0; i < blocks.size; i++) {
            Block b = blocks.get(i);
            if (b.destroyed) {
                for (Ball ball : balls) {
                    if (ball.rectsCollided.contains(b, false)) {
                        updateScore(ball);
                        initBallSlowMotionVelocity(ball);
                    }
                }
                blocks.removeValue(b, false);
                i--; // we need to decrement i when a ball gets removed, otherwise we skip a ball!
            }
        }
    }

    private void updateLevel() {
        if (blocks.isEmpty() && gameState == GameState.MAIN_GAME) {
            player.incrementLevel();
            initGameLevel(Boolean.TRUE);
        }
    }

    private void updateScore(Ball ball) {
        int blocksCollided = ball.countBlocksCollided();
        int points = Player.SCORE_UNIT;
        if (blocksCollided > 1) {        //bonus
            EffectTimer effectTimer = effectTimerControl.create(Effect.INDICATION_BONUS_EARNED, ball);
            points += Player.SCORE_UNIT * blocksCollided;
        }
        player.incrementScore(points);
    }

    private void initBallSlowMotionVelocity(Ball ball) {
        EffectTimer effectTimer = effectTimerControl.create(Effect.BALL_SLOW_MOTION, ball);
        int blocksCollided = ball.countBlocksCollided();
        if (blocksCollided > 1) {
            ball.slowMotion();
            ball.color = new Color(Color.YELLOW);
        }
    }

    private void updateBallEffects() {
        EffectTimer effectTimer = null;

        for (Ball ball : balls) {
            effectTimer = effectTimerControl.getTimer(Effect.BALL_SLOW_MOTION, ball);
            if (effectTimer != null && effectTimer.finished()) {
                effectTimerControl.removeTimer(effectTimer);
                ball.normalizeVelocity();
                ball.color = new Color(Ball.DEFAULT_COLOR);
            }

            effectTimer = effectTimerControl.getTimer(Effect.BALL_FADE_IN, ball);
            if (effectTimer != null) {
                final float totalAlpha = 1f;
                if (!effectTimer.finished()) {
                    float percent = effectTimer.getCurrentTime() / effectTimer.getEffect().getTargetTime();
                    float alpha = totalAlpha * percent;
                    ball.color.a = alpha;
                } else {
                    effectTimerControl.removeTimer(effectTimer);
                    ball.color.a = totalAlpha;
                }
            }
        }
    }

    private void removeBallEffects(Ball ball) {
        effectTimerControl.removeTimers(ball);
        ball.normalizeVelocity();
        ball.color = new Color(Ball.DEFAULT_COLOR);
        float alphaTotal = 1f;
        ball.color.a = alphaTotal;
    }

    private void configControllers() {
        for (Controller controller : getControllers()) {
            Gdx.app.log("Controller", "Controller detected: " + controller.getName());
            if (this.controller == null) {
                Gdx.app.log("Controller", "Using as primary input.");
                this.controller = controller;
            }
        }
    }

    private boolean isDebuggingEnabled() {
        //return java.lang.management.ManagementFactory.getRuntimeMXBean().
        //       getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        return false;
    }

    private void debugBalls() {
        for (int b = 0; b < balls.size; b++) {
            Gdx.app.log("Debug", "Level #" + player.getLevel() + ". Player lives=" + player.lives +
                    ". Ball #" + (b + 1) + " vel=" + Math.abs(balls.get(b).xSpeed) + " defaultVel=" + Math.abs(balls.get(b).speed));
        }
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    public void toggleAudio() {
        audioEnabled = audioEnabled ? false : true;
    }

    private void toggleLevelMusic() {
        if (isAudioEnabled()) {
            levelMusic.play();
        } else {
            if (levelMusic.isPlaying())
                levelMusic.pause();
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < levelMusics.size; i++)
            levelMusics.get(i).dispose();
    }
}
