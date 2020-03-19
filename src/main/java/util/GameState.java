package util;

import engine.helper.GameStatus;

public class GameState {

    private String level;
    private GameStatus status;
    private int tick;
    private float marioY;
    private float marioX;
    private boolean marioAlive;
    private int fps;
    private int timer;
    private boolean[] previousAction;
    private float previousReward;
    private int[][][] previousFrame;

    public GameState(String level, GameStatus status, int tick, float marioY, float marioX, boolean marioAlive, int fps, int timer, boolean[] previousAction, float previousReward, int[][][] previousFrame) {
        this.level = level;
        this.status = status;
        this.tick = tick;
        this.marioY = marioY;
        this.marioX = marioX;
        this.marioAlive = marioAlive;
        this.fps = fps;
        this.timer = timer;
        this.previousAction = previousAction;
        this.previousReward = previousReward;
        this.previousFrame = previousFrame;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public float getMarioY() {
        return marioY;
    }

    public void setMarioY(float marioY) {
        this.marioY = marioY;
    }

    public boolean isMarioAlive() {
        return marioAlive;
    }

    public void setMarioAlive(boolean marioAlive) {
        this.marioAlive = marioAlive;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public float getMarioX() {
        return marioX;
    }

    public void setMarioX(float marioX) {
        this.marioX = marioX;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public boolean[] getPreviousAction() {
        return previousAction;
    }

    public void setPreviousAction(boolean[] previousAction) {
        this.previousAction = previousAction;
    }

    public float getPreviousReward() {
        return previousReward;
    }

    public void setPreviousReward(float previousReward) {
        this.previousReward = previousReward;
    }

    public int[][][] getPreviousFrame() {
        return previousFrame;
    }

    public void setPreviousFrame(int[][][] previousFrame) {
        this.previousFrame = previousFrame;
    }
}
