package net.diegoqueres.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class BallTest {

    @Before
    public void setup() {
        Gdx.graphics = mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth())
                .thenReturn(1024);
        Mockito.when(Gdx.graphics.getHeight())
                .thenReturn(768);
    }

    @Test
    public void testBallCreationWithValidValues() throws Exception {
        Ball ball = new Ball(100, 100);

        Assert.assertFalse("it's not off the x-axis", ball.isOutOfBoundsX());
        Assert.assertFalse("it's not off the y-axis", ball.isOutOfBoundsY());
        Assert.assertNotNull("it's not null", ball);
        Assert.assertEquals("it's in valid and original position x",100, ball.x);
        Assert.assertEquals("it's in valid and original position y",100, ball.y);
    }

    @Test
    public void testBallCreationInInvalidXPosition() throws Exception {
        Ball ballZero = new Ball(0, 100);
        Ball ballMinusTen = new Ball(-10, 100);
        Ball ballHalfDefaultSize = new Ball(Ball.DEFAULT_SIZE/2, 100);
        Ball ballDefaultSizeMinusOne = new Ball(Ball.DEFAULT_SIZE-1, 100);
        Ball ballDefaultSize = new Ball(Ball.DEFAULT_SIZE, 100);

        Assert.assertTrue("it's in valid x position",
                !ballZero.isOutOfBoundsX() && !ballMinusTen.isOutOfBoundsX()
                && !ballHalfDefaultSize.isOutOfBoundsX() && !ballDefaultSize.isOutOfBoundsX()
                && !ballDefaultSizeMinusOne.isOutOfBoundsX()
        );
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballDefaultSizeMinusOne.x);
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballHalfDefaultSize.x);
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballMinusTen.x);
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballZero.x);
    }

    @Test
    public void testBallCreationInInvalidYPosition() throws Exception {
        Ball ballZero = new Ball(100, 0);
        Ball ballMinusTen = new Ball(100, -10);
        Ball ballHalfDefaultSize = new Ball(100, Ball.DEFAULT_SIZE/2);
        Ball ballDefaultSizeMinusOne = new Ball(100, Ball.DEFAULT_SIZE-1);
        Ball ballDefaultSize = new Ball(100, Ball.DEFAULT_SIZE);

        Assert.assertTrue("it's in valid y position",
                !ballZero.isOutOfBoundsY() && !ballMinusTen.isOutOfBoundsY()
                && !ballHalfDefaultSize.isOutOfBoundsY() && !ballDefaultSize.isOutOfBoundsY()
                && !ballDefaultSizeMinusOne.isOutOfBoundsY()
        );
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballDefaultSizeMinusOne.y);
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballHalfDefaultSize.y);
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballMinusTen.y);
        Assert.assertEquals(Ball.DEFAULT_SIZE, ballZero.y);
    }
}