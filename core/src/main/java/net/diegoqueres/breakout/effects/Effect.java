package net.diegoqueres.breakout.effects;

public enum Effect {
    INDICATION_BONUS_EARNED(1.5f),
    BALL_SLOW_MOTION(0.15f),
    BALL_FADE_IN(0.55f),
    BUTTON_PRESS(0.05f);

    float targetTime;

    Effect(float targetTime) {
        this.targetTime = targetTime;
    }

    public float getTargetTime() {
        return targetTime;
    }
}
