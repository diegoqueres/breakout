package net.diegoqueres.breakout.effects;

public class EffectTimer {
    private Effect effect;
    private Object target;
    private float currentTime;

    public EffectTimer(Effect effect, Object target) {
        this.effect = effect;
        this.target = target;
        start();
    }

    public Effect getEffect() {
        return effect;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void addCurrentTime(float time) {
        currentTime += time;
    }

    public boolean finished() {
        return (currentTime >= effect.getTargetTime());
    }

    public void start() {
        this.currentTime = 0f;
    }

    public Object getTarget() {
        return target;
    }
}
