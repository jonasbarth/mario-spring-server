package util;

public class Init {

    private boolean visual;
    private float scale;
    private int marioState;
    private int timer;
    private int fps;
    private String level;

    public Init(boolean visual, float scale, int marioState, int timer, int fps, String level) {
        this.visual = visual;
        this.scale = scale;
        this.marioState = marioState;
        this.timer = timer;
        this.fps = fps;
        this.level = level;
    }

    public boolean isVisual() {
        return visual;
    }

    public float getScale() {
        return scale;
    }

    public int getMarioState() {
        return marioState;
    }

    public int getTimer() {
        return timer;
    }

    public int getFps() {
        return this.fps;
    }

    public String getLevel() {
        return this.level;
    }
}
