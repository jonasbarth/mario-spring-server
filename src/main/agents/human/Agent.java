package main.agents.human;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import main.engine.core.MarioAgent;
import main.engine.core.MarioForwardModel;
import main.engine.core.MarioTimer;
import main.engine.helper.MarioActions;

public class Agent extends KeyAdapter implements MarioAgent {
    private boolean[] actions = null;
    
    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
	actions = new boolean[MarioActions.numberOfActions()];
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
	return actions;
    }

    @Override
    public String getAgentName() {
	return "HumanAgent";
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
	toggleKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
	toggleKey(e.getKeyCode(), false);
    }

    private void toggleKey(int keyCode, boolean isPressed) {
	if(this.actions == null) {
	    return;
	}
	switch (keyCode) {
	case KeyEvent.VK_LEFT:
	    this.actions[MarioActions.LEFT.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_RIGHT:
	    this.actions[MarioActions.RIGHT.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_DOWN:
	    this.actions[MarioActions.DOWN.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_S:
	    this.actions[MarioActions.JUMP.getValue()] = isPressed;
	    break;
	case KeyEvent.VK_A:
	    this.actions[MarioActions.SPEED.getValue()] = isPressed;
	    break;
	}
    }

}
