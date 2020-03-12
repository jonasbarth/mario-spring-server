package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import src.engine.core.MarioGame;
import src.engine.core.MarioResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PlayLevel {
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
    
    public static String getLevel(String filepath) {
		String curDir2 = System.getProperty("user.dir");
	String content = "";
	try {
	    content = new String(Files.readAllBytes(Paths.get(curDir2 + filepath)));
	} catch (IOException e) {
		System.out.println(e.getMessage());
	}
	return content;
    }
    
    public static void main(final String[] args) {
    	/*
        new Thread(
				new Runnable() {
					@Override
					public void run() {
						SpringApplication.run(PlayLevel.class, args);
					}
				}).run(); */
		SpringApplicationBuilder builder = new SpringApplicationBuilder(PlayLevel.class);

		builder.headless(false);

		ConfigurableApplicationContext context = builder.run(args);
		MarioGame game = new MarioGame();

		//printResults(game.playGame(getLevel("/levels/original/lvl-1.txt"), 200, 0));
		//printResults(game.playGame(getLevel("original/lvl-2.txt", 200, 0)));
		//printResults(game.runGame(new main.agents.robinBaumgarten.Agent(), getLevel("levels/original/lvl-1.txt"), 20, 0, true));
    }
}
