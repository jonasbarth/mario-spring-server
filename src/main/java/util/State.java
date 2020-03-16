package util;

import engine.helper.GameStatus;

public class State {

    private int[][][] frame;
    private GameStatus gameStatus;



    public void setFrame(int[][][] frame) {
        this.frame = frame;
    }

    public int[][][] getFrame() {
        return this.frame;
    }


    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
