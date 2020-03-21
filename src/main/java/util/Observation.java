package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Observation {
    private Reward reward;
    private State state;
    private State[] states;
    private int[][][][] frames;
    private String gameStatus;


    public Observation(Reward reward, State state) {
        this.reward = reward;
        this.state = state;
    }

    public Observation(Reward reward, State state, State[] states) {
        this.reward = reward;
        this.state = state;
        this.states = states;
    }

    public Reward getReward() {
        return reward;
    }

    public State getState() {
        return state;
    }

    public State[] getStates() {
        return this.states;
    }

    public float getValue() {
        return this.reward.getReward();
    }

    public void setFrames(int[][][][] frames) {
        this.frames = frames;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public byte[] getByteArray() {
        int frames = this.frames.length;
        int channels = this.frames[0].length;
        int width = this.frames[0][0].length;
        int height = this.frames[0][0][0].length;

        System.out.printf("width %d  x  height %d\n", width, height);
        //if (true)
            //return new byte[4*frames*3*84*84];
        // Set up a ByteBuffer called intBuffer
        ByteBuffer intBuffer = ByteBuffer.allocate(4*frames*channels*width*height); // 4 bytes in an int
        intBuffer.order(ByteOrder.LITTLE_ENDIAN); // Java's default is big-endian

        // Copy ints from intArray into intBuffer as bytes
        for (int f = 0; f < frames; f++) {
            for (int c = 0; c < channels; c++){
                for (int w = 0; w < width; w++) {
                    for (int h = 0; h < height; h++) {
                        intBuffer.putInt(this.frames[f][c][w][h]);
                    }
                }

            }
        }

        // Convert the ByteBuffer to a byte array and return it
        byte[] byteArray = intBuffer.array();
        return byteArray;
    }




}
