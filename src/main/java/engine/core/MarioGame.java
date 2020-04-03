package engine.core;

import java.awt.event.WindowEvent;
import java.awt.image.VolatileImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;
import com.google.common.primitives.Ints;

import run.PlayLevel;
import agents.human.Agent;
import engine.helper.GameStatus;
import engine.helper.MarioActions;
import util.*;



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
    public static final int FRAME_STACK = 1;
    private final int FRAME_SKIP = 4;
    private String level;
    private boolean[] previousAction;
    private float previousReward;
    private int[][][] previousFrame;

    //These two fields represent the state observed by the py4j RL agent
    private int[][][][] frames = new int[FRAME_STACK][][][];
    private float reward;
    private String gameStatus;
    private int scaledWidth;
    private int scaledHeight;
    private boolean rgb;
    private boolean egocentric;

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
            //this.executeAction(right);
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
                ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget, this.scaledWidth, this.scaledHeight, this.world);
                int[][][] matrix = imgPre.getGrayscaleMatrix();
                MLAgent test = new ServerMLAgent("http://127.0.0.1:5000/");
                //test.getActions(matrix, 0.0f);

            }
            //System.exit(0);
            MarioTimer agentTimer = new MarioTimer(MarioGame.maxTime);
            this.agent.initialize(new MarioForwardModel(this.world.clone()), agentTimer);

            ArrayList<MarioEvent> gameEvents = new ArrayList<>();
            ArrayList<MarioAgentEvent> agentEvents = new ArrayList<>();

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
                    ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget, this.scaledWidth, this.scaledHeight, this.world);
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
                /*
                if (this.getDelay(fps) > 0) {
                      try {
                          currentTime += this.getDelay(fps);
                          Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                      } catch (InterruptedException e) {
                          break;
                      }
                } */
                /*Set the new X and the new tick of the Mario*/

            }
        return new MarioResult(this.world, gameEvents, agentEvents);
    }



    /**
     * Initialises the game environment and the start
     * state for the agent. Returns the starting state.
     */

    public Observation initGameEnv(boolean visual, float scale, int marioState, int timer, int fps, String levelPath, int scaledWidth, int scaledHeight, boolean rgb, boolean egocentric) {

        this.fps = fps;
        this.level = levelPath;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.rgb = rgb;
        this.egocentric = egocentric;

        String level = PlayLevel.getLevel(levelPath);
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
        System.out.printf("Mario is at %f, %f\n", this.world.mario.x, this.world.mario.y);
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

            ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget, this.scaledWidth, this.scaledHeight, this.world);
            if (this.rgb) {
                if (this.egocentric) {
                    currentFrame = imgPre.getEgoRGBMatrix();
                } else {
                    currentFrame = imgPre.getRGBMatrix();
                }
            }
            else if (!this.rgb) {
                if (this.egocentric) {
                    currentFrame = imgPre.getEgoGrayscaleMatrix();
                } else {
                    currentFrame = imgPre.getGrayscaleMatrix();
                }
            }


            state.setFrame(currentFrame);
            state.setGameStatus(this.world.gameStatus);
            states[0] = state;

            //Start setting the current frames to be extracted by the RL agent
            this.frames[0] = currentFrame;

            float cumReward = 0;
            Observation obs = null;
            boolean[] dummyStep = {false, false, false, false, false};
            for (int i = 1; i < this.FRAME_SKIP; i++) {
                obs = this.step(dummyStep);
                cumReward += obs.getReward().getReward();
                states[i] = obs.getState();

                //this.frames[i] = obs.getState().getFrame();
            }
            //this.previousFrame = states[FRAME_SKIP-1].getFrame();
            this.gameStatus = this.world.gameStatus.toString();
            finalReward = new Reward();

            //initial reward is always 0 at first
            finalReward.setReward(0.0f);


        }

        Observation obs = new Observation(finalReward, state, states);
        obs.setFrames(this.frames);
        obs.setGameStatus(this.world.gameStatus.toString());
        return obs;
    }



    /**
     * Executes an action in the game environment.
     * @param action
     *
     * Returns a new state and the reward associated with that action.
     */

    public Observation executeAction(java.util.List<Boolean> actions) {
        this.reward = 0;
        Reward reward = new Reward();
        State state = new State();
        int[][][] currentFrame = null;
        long currentTime = System.currentTimeMillis();
        this.previousAction = this.toPrimitiveArray(actions);

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
                this.world.update(this.previousAction);
                this.gameEvents.addAll(this.world.lastFrameEvents);
                this.agentEvents.add(new MarioAgentEvent(this.previousAction, this.world.mario.x,
                        this.world.mario.y, (this.world.mario.isLarge?1:0) + (this.world.mario.isFire?1:0),
                        this.world.mario.onGround, this.world.currentTick));

                //System.out.printf("%f %d \n\n", world.mario.x, world.currentTick);
                //reward.calculateReward(this.world);

                ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget, this.scaledWidth, this.scaledHeight, this.world);
                if (this.rgb) {
                    if (this.egocentric) {
                        currentFrame = imgPre.getEgoRGBMatrix();
                    } else {
                        currentFrame = imgPre.getRGBMatrix();
                    }
                }
                else if (!this.rgb) {
                    if (this.egocentric) {
                        currentFrame = imgPre.getEgoGrayscaleMatrix();
                    } else {
                        currentFrame = imgPre.getGrayscaleMatrix();
                    }
                }

                state.setFrame(currentFrame);
                state.setGameStatus(this.world.gameStatus);

                this.gameStatus = this.world.gameStatus.toString();
                this.frames[0] = currentFrame;

            }



            //check if delay needed
            /*
            if (this.getDelay(this.fps) > 0) {
                try {
                    currentTime += this.getDelay(this.fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    return null;
                }
            } */
            /*Set the new X and the new tick of the Mario*/

        }

        float cumReward = 0.0f;

        boolean[] dummyStep = {false, false, false, false, false};
        State[] states = new State[4];
        Observation obs = null;
        for (int i = 1; i < this.FRAME_SKIP; i++) {
            obs = this.step(this.previousAction);
            cumReward += obs.getReward().getReward();
            states[i] = obs.getState();

            //set fields for the py4j RL agent to extract
            //this.frames[i] = obs.getState().getFrame();
            this.reward += obs.getReward().getReward();
        }

        states[0] = state;
        Reward finalReward = new Reward();
        finalReward.setReward(cumReward);
        this.previousReward = finalReward.getReward();

        reward.calculateReward(this.world);

        Observation finalObs = new Observation(reward, state, states);
        finalObs.setFrames(this.frames);
        finalObs.setGameStatus(this.world.gameStatus.toString());

        System.out.printf("Mario is at %f, %f\n", this.world.mario.x, this.world.mario.y);
        return finalObs;

    }


    public String hello(String name) {
        return "Hello, " + name + "!";
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

            if(!this.pause) {

                agentTimer = new MarioTimer(MarioGame.maxTime);


                if (MarioGame.verbose) {
                    if (agentTimer.getRemainingTime() < 0 && Math.abs(agentTimer.getRemainingTime()) > MarioGame.graceTime) {
                        System.out.println("The Agent is slowing down the game by: "
                                + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                    }
                }
                // update world


                reward.setX(this.world.mario.x);
                reward.setTick(this.world.currentTick);

                this.world.getEnemies();
                this.world.update(actions);
                this.gameEvents.addAll(this.world.lastFrameEvents);
                this.agentEvents.add(new MarioAgentEvent(actions, this.world.mario.x,
                        this.world.mario.y, (this.world.mario.isLarge?1:0) + (this.world.mario.isFire?1:0),
                        this.world.mario.onGround, this.world.currentTick));


                reward.calculateReward(this.world);

                ImagePreprocesser imgPre = new ImagePreprocesser(renderTarget, this.scaledWidth, this.scaledHeight, this.world);
                if (this.rgb) {
                    if (this.egocentric) {
                        currentFrame = imgPre.getEgoRGBMatrix();
                    } else {
                        currentFrame = imgPre.getRGBMatrix();
                    }
                }
                else if (!this.rgb) {
                    if (this.egocentric) {
                        currentFrame = imgPre.getEgoGrayscaleMatrix();
                    } else {
                        currentFrame = imgPre.getGrayscaleMatrix();
                    }
                }

                this.previousFrame = currentFrame;
                state.setFrame(currentFrame);
                state.setGameStatus(this.world.gameStatus);
                this.frames[0] = currentFrame;

            }


            //check if delay needed
            /*
            if (this.getDelay(this.fps) > 0) {
                System.out.println("Delaying fps");
                try {
                    currentTime += this.getDelay(this.fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    return null;
                }
            } */
            /*Set the new X and the new tick of the Mario*/

        }
        Observation observation = new Observation(reward, state);
        observation.setGameStatus(this.world.gameStatus.toString());
        return observation;
    }



    public String close() {
        this.window.dispatchEvent(new WindowEvent(this.window, WindowEvent.WINDOW_CLOSING));
        return "Window closed";
    }


    public String timeout() {
        this.world.timeout();
        return "Game timed out";
    }


    public String lose() {
        this.world.lose();
        return "Game lost";
    }


    public String win() {
        this.world.win();
        return "Game won";
    }


    public GameState status() {
        return new GameState(this.level, this.world.gameStatus, this.world.currentTick, this.world.mario.y, this.world.mario.x, this.world.mario.alive, this.fps, this.world.currentTimer, this.previousAction, this.previousReward, this.previousFrame);
    }

    private boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }

    public int[][][][] getFrames() {

        return this.frames;
    }

    public float getReward() {
        return this.reward;
    }

    public String getGameStatus() {
        return this.gameStatus;
    }

}
