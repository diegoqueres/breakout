package net.diegoqueres.breakout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Breakout extends ApplicationAdapter {
	ShapeRenderer shape;
	List<Ball> balls = new ArrayList<>();
	Paddle paddle;
	Random r = new Random();

	@Override
	public void create () {
		shape = new ShapeRenderer();
		Ball ball = new Ball(r.nextInt(Gdx.graphics.getWidth()), r.nextInt(Gdx.graphics.getHeight()));
		balls.add(ball);
		paddle = new Paddle(Gdx.graphics.getWidth()/2);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		for (Ball ball : balls) {
			ball.draw(shape);
		}
		paddle.draw(shape);
		shape.end();

		update();
	}

	private void update() {
		int x = Gdx.input.getX();
		paddle.updatePosition(x);

		for (Ball ball : balls) {
			ball.checkCollision(paddle);
			ball.update();
		}
	}
}
