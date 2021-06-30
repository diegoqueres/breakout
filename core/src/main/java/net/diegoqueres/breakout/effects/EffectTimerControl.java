package net.diegoqueres.breakout.effects;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class EffectTimerControl {
    List<EffectTimer> effectTimers;

    public EffectTimerControl() {
        effectTimers = new ArrayList<>();
    }

    public EffectTimer getTimer(Effect effect, Object target) {
        for (EffectTimer effectTimer : effectTimers) {
            if (effectTimer.getEffect() == effect && effectTimer.getTarget().equals(target))
                return effectTimer;
        }
        return null;
    }

    public void addTimer(EffectTimer effectTimer) {
        effectTimers.add(effectTimer);
    }

    public void updateTimers() {
        for (EffectTimer effectTimer : effectTimers) {
            if (!effectTimer.finished()) {
                effectTimer.addCurrentTime(Gdx.graphics.getDeltaTime());
            }
        }
    }

    public void removeTimers(Object target) {
        for (int i = 0; i < effectTimers.size(); i++) {
            EffectTimer effectTimer = effectTimers.get(i);
            if (effectTimer.getTarget().equals(target)) {
                removeTimer(effectTimer);
                i--;
            }
        }
    }

    public void removeTimer(EffectTimer effectTimer) {
        if (effectTimer != null) effectTimers.remove(effectTimer);
    }

    public EffectTimer create(Effect effect, Object target) {
        EffectTimer effectTimer = new EffectTimer(effect, target);
        addTimer(effectTimer);
        return effectTimer;
    }
}
