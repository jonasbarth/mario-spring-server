package src;

import src.engine.core.MarioGame;
import src.engine.core.MarioLevelGenerator;
import src.engine.core.MarioLevelModel;
import src.engine.core.MarioResult;
import src.engine.core.MarioTimer;
import src.agents.robinBaumgarten.Agent;
import src.levelGenerators.notch.LevelGenerator;

public class GenerateLevel {
    public static void printResults(MarioResult result) {
	System.out.println("****************************************************************");
	System.out.println("Game Status: " + result.getGameStatus().toString() + 
		" Percentage Completion: " + result.getCompletionPercentage());
	System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() + 
		" Remaining Time: " + (int)Math.ceil(result.getRemainingTime() / 1000f)); 
	System.out.println("Mario State: " + result.getMarioMode() +
		" (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
	System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() + 
		" Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() + 
		" Falls: " + result.getKillsByFall() + ")");
	System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() + 
		" Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
	System.out.println("****************************************************************");
    }
    
    public static void main(String[] args) {
		MarioLevelGenerator generator = new LevelGenerator();
		String level = generator.getGeneratedLevel(new MarioLevelModel(150, 16), new MarioTimer(5*60*60*1000));
		MarioGame game = new MarioGame();
		// printResults(game.playGame(level, 200, 0));
		printResults(game.runGame(new Agent(), level, 20, 0, true));
    }
}
