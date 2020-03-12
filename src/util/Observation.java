package src.util;

public class Observation {
    private Reward reward;
    private State state;
    private State[] states;

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
}
