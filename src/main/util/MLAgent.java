package main.util;

import main.engine.core.MarioWorld;

public interface MLAgent {

    boolean[] getActions(int[][] frame, float reward);

    float calculateReward(MarioWorld world);

    void setX(float x);

    void setTick(int tick);
}
