package src.agents.doNothing;

import src.engine.core.MarioAgent;
import src.engine.core.MarioForwardModel;
import src.engine.core.MarioTimer;
import src.engine.helper.MarioActions;

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
