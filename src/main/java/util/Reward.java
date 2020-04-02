package util;

import engine.core.MarioWorld;

public class Reward {

    private float reward;
    private float x;
    private int tick;

    public Reward() {

    }

    public float getReward() {
        return this.reward;
    }

    public void setReward(float reward) {
        this.reward = reward;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }


    public void calculateReward(MarioWorld world) {
        float v = world.mario.x - this.x;
        float c = this.tick - world.currentTick;
        float d = 0.0f;

        switch (world.gameStatus) {

            case WIN:
                d = 15.0f;
                break;

            case LOSE:
                d = -100.0f;
                break;

            case TIME_OUT:
                d = -100.0f;
                break;

            default:
                d = 0.0f;

        }


        float reward = v + c + d;
        this.reward = reward > 15 ? 15 : reward;
        //this.reward = reward < -15 ? -15 : reward;

    }
}
