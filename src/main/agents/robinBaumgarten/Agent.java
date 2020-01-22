package main.agents.robinBaumgarten;

import main.engine.core.MarioAgent;
import main.engine.core.MarioForwardModel;
import main.engine.core.MarioTimer;
import main.engine.helper.MarioActions;

/**
 * @author RobinBaumgarten
 */
public class Agent implements MarioAgent{
    private boolean action[];
    private AStarTree tree;
    
    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
	this.action = new boolean[MarioActions.numberOfActions()];
	this.tree = new AStarTree();
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
	action = this.tree.optimise(model, timer);
	return action;
    }

    @Override
    public String getAgentName() {
	return "RobinBaumgartenAgent";
    }

}
