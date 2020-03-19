package engine.core;

import java.awt.event.WindowEvent;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;

import org.springframework.http.MediaType;
import run.PlayLevel;
import agents.human.Agent;
import engine.helper.GameStatus;
import engine.helper.MarioActions;
import util.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class MarioGame{
    /**
     * the maximum time that agent takes for each step
     */
    public static final long maxTime = 40;
    /**
     * extra time before reporting that the agent is taking more time that it should
     */
    public static final long graceTime = 10;
    /**
     * Screen width
     */
    public static final int width = 256;
    /**
     * Screen height
     */
    public static final int height = 256;
    /**
     * Screen width in tiles
     */
    public static final int tileWidth = width/16;
    /**
     * Screen height in tiles
     */
    public static final int tileHeight = height/16;
    /**
     * print debug details
     */
    public static final boolean verbose = false;
    
    /**
     * pauses the whole game at any moment
     */
    public boolean pause = false;
    
    /**
     * events that kills the player when it happens only care about type and param
     */
    private MarioEvent[] killEvents;
    
    //visualization
    private JFrame window = null;
    private MarioRender render = null;
    private MarioAgent agent = null;
    private MarioWorld world = null;

    //used for initialising the visuals
    private VolatileImage renderTarget = null;
    private Graphics backBuffer = null;
    private Graphics currentBuffer = null;

    //used for running the game
    private MarioTimer agentTimer;
    private ArrayList<MarioEvent> gameEvents;
    private ArrayList<MarioAgentEvent> agentEvents;
    private int fps;
    private final int FRAME_STACK = 4;
    
    /**
     * Create a mario game to be played
     */
    public MarioGame() {
	
    }
    
    /**
     * Create a mario game with a different forward model where the player on certain event
     * @param killPlayer events that will kill the player
     */
    public MarioGame(MarioEvent[] killEvents) {
	this.killEvents = killEvents;
    }
    
    private int getDelay(int fps) {
	if(fps <= 0) {
	    return 0;
	}
	return 1000 / fps;
    }
    
    private void setAgent(MarioAgent agent) {
	this.agent = agent;
        if (agent instanceof KeyAdapter) {
            this.render.addKeyListener((KeyAdapter) this.agent);
        }
    }
    
    /**
     * Play a certain mario level
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @return statistics about the current game
     */
    public MarioResult playGame(String level, int timer) {
	return this.runGame(new Agent(), level, timer, 0, true, 30, 2);
    }
    
    /**
     * Play a certain mario level
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @return statistics about the current game
     */
    public MarioResult playGame(String level, int timer, int marioState) {
	return this.runGame(new Agent(), level, timer, marioState, true, 30, 2);
    }
    
    /**
     * Play a certain mario level
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @param fps the number of frames per second that the update function is following
     * @return statistics about the current game
     */
    public MarioResult playGame(String level, int timer, int marioState, int fps) {
	return this.runGame(new Agent(), level, timer, marioState, true, fps, 2);
    }
    
    /**
     * Play a certain mario level
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @param fps the number of frames per second that the update function is following
     * @param scale the screen scale, that scale value is multiplied by the actual width and height
     * @return statistics about the current game
     */
    public MarioResult playGame(String level, int timer, int marioState, int fps, float scale) {
	return this.runGame(new Agent(), level, timer, marioState, true, fps, scale);
    }
    
    /**
     * Run a certain mario level with a certain agent
     * @param agent the current AI agent used to play the game
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer) {
	return this.runGame(agent, level, timer, 0, false, 0, 2);
    }
    
    /**
     * Run a certain mario level with a certain agent
     * @param agent the current AI agent used to play the game
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer, int marioState) {
	return this.runGame(agent, level, timer, marioState, false, 0, 2);
    }
    
    /**
     * Run a certain mario level with a certain agent
     * @param agent the current AI agent used to play the game
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @param visuals show the game visuals if it is true and false otherwise
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer, int marioState, boolean visuals) {
	return this.runGame(agent, level, timer, marioState, visuals, visuals?30:0, 2);
    }
    
    /**
     * Run a certain mario level with a certain agent
     * @param agent the current AI agent used to play the game
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @param visuals show the game visuals if it is true and false otherwise
     * @param fps the number of frames per second that the update function is following
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer, int marioState, boolean visuals, int fps) {
	return this.runGame(agent, level, timer, marioState, visuals, fps, 2);
    }
    
    /**
     * Run a certain mario level with a certain agent
     * @param agent the current AI agent used to play the game
     * @param level a string that constitutes the mario level, it uses the same representation as the VGLC but with more details. for more details about each symbol check the json file in the levels folder.
     * @param timer number of ticks for that level to be played. Setting timer to anything <=0 will make the time infinite
     * @param marioState the initial state that mario appears in. 0 small mario, 1 large mario, and 2 fire mario.
     * @param visuals show the game visuals if it is true and false otherwise
     * @param fps the number of frames per second that the update function is following
     * @param scale the screen scale, that scale value is multiplied by the actual width and height
     * @return statistics about the current game
     */
    public MarioResult runGame(MarioAgent agent, String level, int timer, int marioState, boolean visuals, int fps, float scale) {


        boolean[] right = {false,true,false,false,false};
        for (int i = 0; i < 50; i++) {
            this.executeAction(right);
        }

        return null;
        /*
        if (visuals) {
            this.window = new JFrame("Mario AI Framework");
            this.render = new MarioRender(scale);
            this.window.setContentPane(this.render);
            this.window.pack();
            this.window.setResizable(false);
            this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.render.init();
            this.window.setVisible(true);
        }
        this.setAgent(agent);
        return this.gameLoop(level, timer, marioState, visuals, fps);*/
    }

        private MarioResult gameLoop(String level, int timer, int marioState, boolean visual, int fps) {
            this.world = new MarioWorld(this.killEvents);

            this.world.visuals = visual;
            this.world.initializeLevel(level, 1000 * timer);
            if (visual) {
                this.world.initializeVisuals(this.render.getGraphicsConfiguration());

            }
            this.world.mario.isLarge = marioState > 0;
            this.world.mario.isFire = marioState > 1;
            this.world.update(new boolean[MarioActions.numberOfActions()]);
            long currentTime = System.currentTimeMillis();

            //initialize graphics
            VolatileImage renderTarget = null;
            Graphics backBuffer = null;
            Graphics currentBuffer = null;
            int[][] currentFrame;

            /*Initialises the game visuals*/
            if(visual) {
                renderTarget = this.render.createVolatileImage(MarioGame.width, MarioGame.height);
                backBuffer = this.render.getGraphics();
                currentBuffer = renderTarget.getGraphics();
                this.render.addFocusListener(this.render);
                this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
                ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                int[][][] matrix = imgPre.getGrayscaleMatrix();
                MLAgent test = new ServerMLAgent("http://127.0.0.1:5000/");
                //test.getActions(matrix, 0.0f);

            }
            //System.exit(0);
            MarioTimer agentTimer = new MarioTimer(MarioGame.maxTime);
            this.agent.initialize(new MarioForwardModel(this.world.clone()), agentTimer);

            ArrayList<MarioEvent> gameEvents = new ArrayList<>();
            ArrayList<MarioAgentEvent> agentEvents = new ArrayList<>();
            /*TODO Expose the GameState in the game loop. Receive actions from the Server
             */
            /*TODO Implement frame skipping. How can I send multiple frames if Mario needs to make a decision every frame?*/

                /*TODO calculate the reward based on the current state of the game*/
            int[][][] matrix = null;
            MLAgent test = new ServerMLAgent("http://127.0.0.1:5000/");
            test.setX(this.world.mario.x);
            test.setTick(this.world.currentTick);
            int frameSkipping = 4;
            boolean[] actions = {false, false, false, false, false};

            while(this.world.gameStatus == GameStatus.RUNNING) {
                if(visual) {
                    this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
                    /*TODO Get the current frame and make a HTTP request*/
                    ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                    matrix = imgPre.getGrayscaleMatrix();

                    //System.out.println(this.world.coins);
                    //this.world.
                    //test.getActions(matrix, this.world.currentTick);



                }

                if(!this.pause) {
                    //get actions
                    agentTimer = new MarioTimer(MarioGame.maxTime);
                    //boolean[] actions = this.agent.getActions(new MarioForwardModel(this.world.clone()), agentTimer);
                    //actions = test.getActions(matrix, test.calculateReward(this.world));

                    if (MarioGame.verbose) {
                        if (agentTimer.getRemainingTime() < 0 && Math.abs(agentTimer.getRemainingTime()) > MarioGame.graceTime) {
                        System.out.println("The Agent is slowing down the game by: "
                            + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                        }
                    }
                // update world
                    //System.out.printf("%f %d \n", world.mario.x, world.currentTick);
                    test.setX(this.world.mario.x);
                    test.setTick(this.world.currentTick);
                    this.world.getEnemies();
                    this.world.update(actions);
                    gameEvents.addAll(this.world.lastFrameEvents);
                    agentEvents.add(new MarioAgentEvent(actions, this.world.mario.x,
                    this.world.mario.y, (this.world.mario.isLarge?1:0) + (this.world.mario.isFire?1:0),
                    this.world.mario.onGround, this.world.currentTick));
                    //System.out.printf("%f %d \n\n", world.mario.x, world.currentTick);
                    /*TODO after updating the world mario might die but the server is not notified. Maybe communication with the server needs to be moved
                    here instead so that the first action is to do nothing in the game.
                     */
                    //actions = test.getActions(matrix, test.calculateReward(this.world));
                    //ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                    //matrix = imgPre.getGrayscaleMatrix();
                    //System.out.println(this.world.mario.alive);

                }

                //render world
                /*if(visual) {
                    this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
                    /*TODO Get the current frame and make a HTTP request/
                    ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                    int[][] matrix = imgPre.getGrayscaleMatrix();
                    MLAgent test = new ServerMLAgent("http://127.0.0.1:5000/");
                    test.getActions(matrix);
                    System.exit(0);
                } */

                //check if delay needed
                if (this.getDelay(fps) > 0) {
                      try {
                          currentTime += this.getDelay(fps);
                          Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                      } catch (InterruptedException e) {
                          break;
                      }
                }
                /*Set the new X and the new tick of the Mario*/

            }
        return new MarioResult(this.world, gameEvents, agentEvents);
    }



    /**
     * Initialises the game environment and the start
     * state for the agent. Returns the starting state.
     */
    @PostMapping(path = "/init", consumes = "application/json", produces = "application/json")
    public Observation initGameEnv(@RequestBody Init init) {
        boolean visual = init.isVisual();
        float scale = init.getScale();
        int marioState = init.getMarioState();
        int timer = init.getTimer();
        this.fps = init.getFps();

        String level = PlayLevel.getLevel(init.getLevel());
        if (visual) {
            this.window = new JFrame("Mario AI Framework");
            this.render = new MarioRender(scale);
            this.window.setContentPane(this.render);
            this.window.pack();
            this.window.setResizable(false);
            this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.render.init();
            this.window.setVisible(true);
        }
        //this.setAgent(agent);
        this.world = new MarioWorld(this.killEvents);
        this.world.visuals = visual;
        this.world.initializeLevel(level, 1000 * timer);
        if (visual) {
            this.world.initializeVisuals(this.render.getGraphicsConfiguration());

        }
        this.world.mario.isLarge = marioState > 0;
        this.world.mario.isFire = marioState > 1;
        this.world.update(new boolean[MarioActions.numberOfActions()]);
        long currentTime = System.currentTimeMillis();
        this.agentTimer = new MarioTimer(MarioGame.maxTime);
        //this.agent.initialize(new MarioForwardModel(this.world.clone()), this.agentTimer);
        this.gameEvents = new ArrayList<>();
        this.agentEvents = new ArrayList<>();

        State[] states = new State[4];
        int[][][] currentFrame = null;
        Reward finalReward = null;
        State state = new State();
        //TODO after initialising the game visuals, take 3 empty actions to get a stack of 4 frames
        /*Initialises the game visuals*/
        if(visual) {

            this.renderTarget = this.render.createVolatileImage(MarioGame.width, MarioGame.height);
            this.backBuffer = this.render.getGraphics();
            this.currentBuffer = this.renderTarget.getGraphics();
            this.render.addFocusListener(this.render);
            this.render.renderWorld(this.world, this.renderTarget, this.backBuffer, this.currentBuffer);

            ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
            currentFrame = imgPre.getGrayscaleMatrix();

            state.setFrame(currentFrame);
            state.setGameStatus(this.world.gameStatus);
            states[0] = state;

            int cumReward = 0;
            boolean[] dummyStep = {false, false, false, false, false};
            for (int i = 1; i < this.FRAME_STACK; i++) {
                Observation obs = this.step(dummyStep);
                cumReward += obs.getReward().getReward();
                states[i] = obs.getState();
            }

            finalReward = new Reward();
            finalReward.setReward(cumReward);




        }




        return new Observation(finalReward, state, states);
    }



    /**
     * Executes an action in the game environment.
     * @param action
     *
     * Returns a new state and the reward associated with that action.
     */
    @PostMapping(path = "/action", consumes = "application/json", produces = "application/json")
    public Observation executeAction(@RequestBody boolean[] actions) {
        Reward reward = new Reward();
        State state = new State();
        int[][][] currentFrame = null;
        long currentTime = System.currentTimeMillis();

        if (this.world.gameStatus == GameStatus.RUNNING) {

            this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
            /*TODO Get the current frame and make a HTTP request*/
            //ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
            //currentFrame = imgPre.getGrayscaleMatrix();



            if(!this.pause) {

                agentTimer = new MarioTimer(MarioGame.maxTime);


                if (MarioGame.verbose) {
                    if (agentTimer.getRemainingTime() < 0 && Math.abs(agentTimer.getRemainingTime()) > MarioGame.graceTime) {
                        System.out.println("The Agent is slowing down the game by: "
                                + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                    }
                }
                // update world
                //System.out.printf("%f %d \n", world.mario.x, world.currentTick);

                reward.setX(this.world.mario.x);
                reward.setTick(this.world.currentTick);
                this.world.getEnemies();
                this.world.update(actions);
                this.gameEvents.addAll(this.world.lastFrameEvents);
                this.agentEvents.add(new MarioAgentEvent(actions, this.world.mario.x,
                        this.world.mario.y, (this.world.mario.isLarge?1:0) + (this.world.mario.isFire?1:0),
                        this.world.mario.onGround, this.world.currentTick));

                //System.out.printf("%f %d \n\n", world.mario.x, world.currentTick);
                reward.calculateReward(this.world);

                ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                currentFrame = imgPre.getGrayscaleMatrix();
                state.setFrame(currentFrame);
                state.setGameStatus(this.world.gameStatus);

            }

            //render world
                /*if(visual) {
                    this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
                    /*TODO Get the current frame and make a HTTP request/
                    ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                    int[][] matrix = imgPre.getGrayscaleMatrix();
                    MLAgent test = new ServerMLAgent("http://127.0.0.1:5000/");
                    test.getActions(matrix);
                    System.exit(0);
                } */

            //check if delay needed
            if (this.getDelay(this.fps) > 0) {
                try {
                    currentTime += this.getDelay(this.fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    return null;
                }
            }
            /*Set the new X and the new tick of the Mario*/

        }

        int cumReward = 0;
        boolean[] dummyStep = {false, false, false, false, false};
        State[] states = new State[4];
        for (int i = 1; i < this.FRAME_STACK; i++) {
            Observation obs = this.step(dummyStep);
            cumReward += obs.getReward().getReward();
            states[i] = obs.getState();
        }
        states[0] = state;
        Reward finalReward = new Reward();
        finalReward.setReward(cumReward);
        return new Observation(finalReward, state, states);

    }

    @RequestMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @PostMapping(path = "/inits", consumes = "application/json", produces = "application/json")
    public String init(@RequestBody boolean b, @RequestBody boolean c) {
        return "You sent me: " + b + " : " + c;
    }

    /**
     * Performs another step in the game.
     * @param actions
     * @return
     */
    private Observation step(boolean[] actions) {
        Reward reward = new Reward();
        State state = new State();
        int[][][] currentFrame = null;
        long currentTime = System.currentTimeMillis();
        if (this.world.gameStatus == GameStatus.RUNNING) {

            this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
            /*TODO Get the current frame and make a HTTP request*/
            //ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
            //currentFrame = imgPre.getGrayscaleMatrix();



            if(!this.pause) {

                agentTimer = new MarioTimer(MarioGame.maxTime);


                if (MarioGame.verbose) {
                    if (agentTimer.getRemainingTime() < 0 && Math.abs(agentTimer.getRemainingTime()) > MarioGame.graceTime) {
                        System.out.println("The Agent is slowing down the game by: "
                                + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                    }
                }
                // update world
                //System.out.printf("%f %d \n", world.mario.x, world.currentTick);

                reward.setX(this.world.mario.x);
                reward.setTick(this.world.currentTick);
                this.world.getEnemies();
                this.world.update(actions);
                this.gameEvents.addAll(this.world.lastFrameEvents);
                this.agentEvents.add(new MarioAgentEvent(actions, this.world.mario.x,
                        this.world.mario.y, (this.world.mario.isLarge?1:0) + (this.world.mario.isFire?1:0),
                        this.world.mario.onGround, this.world.currentTick));

                //System.out.printf("%f %d \n\n", world.mario.x, world.currentTick);
                reward.calculateReward(this.world);

                ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                currentFrame = imgPre.getGrayscaleMatrix();
                state.setFrame(currentFrame);
                state.setGameStatus(this.world.gameStatus);

            }

            //render world
                /*if(visual) {
                    this.render.renderWorld(this.world, renderTarget, backBuffer, currentBuffer);
                    /*TODO Get the current frame and make a HTTP request/
                    ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget);
                    int[][] matrix = imgPre.getGrayscaleMatrix();
                    MLAgent test = new ServerMLAgent("http://127.0.0.1:5000/");
                    test.getActions(matrix);
                    System.exit(0);
                } */

            //check if delay needed
            if (this.getDelay(this.fps) > 0) {
                try {
                    currentTime += this.getDelay(this.fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    return null;
                }
            }
            /*Set the new X and the new tick of the Mario*/

        }
        return new Observation(reward, state);
    }


    @RequestMapping(path= "/close", produces = "application/json")
    public String close() {
        this.window.dispatchEvent(new WindowEvent(this.window, WindowEvent.WINDOW_CLOSING));
        return "Window closed";
    }

    @RequestMapping(path = "/timeout", produces = "application/json")
    public String timeout() {
        this.world.timeout();
        return "Game timed out";
    }

    @RequestMapping(path = "/lose", produces = "application/json")
    public String lose() {
        this.world.lose();
        return "Game lost";
    }

    @RequestMapping(path = "/win", produces = "application/json")
    public String win() {
        this.world.win();
        return "Game won";
    }


}
