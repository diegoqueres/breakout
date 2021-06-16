package net.diegoqueres.breakout;

import com.badlogic.gdx.Gdx;

public class Player {
    public final static  int MAX_LIFES = 3;
    public final static  int SCORE_UNIT = 10;
    enum STATE { ALIVE, DEAD }

    int lives;
    int score;
    STATE state;

    public Player() {
        this.lives = MAX_LIFES;
        this.score = 0;
        this.state = STATE.ALIVE;
    }

    public void dead() {
        lives--;
        this.state = STATE.DEAD;
    }

    public boolean isDead() {
        return this.state == STATE.DEAD;
    }

    public void incrementScore(int points) {
        score += points;
    }

    public void reanimate() {
        this.state = STATE.ALIVE;
    }
}
