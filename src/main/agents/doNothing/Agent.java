package main.agents.doNothing;

import main.engine.core.MarioAgent;
import main.engine.core.MarioForwardModel;
import main.engine.core.MarioTimer;
import main.engine.helper.MarioActions;

public class Agent implements MarioAgent {
    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
	
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
	return new boolean[MarioActions.numberOfActions()];
    }

    @Override
    public String getAgentName() {
	return "DoNothingAgent";
    }
}
