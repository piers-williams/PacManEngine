package pacman;

import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.MASController;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.comms.BasicMessenger;
import pacman.game.comms.Messenger;
import pacman.game.util.Stats;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.*;

/**
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class Executor {
    protected final boolean pacmanPO;
    protected final boolean ghostPO;
    protected final boolean ghostsMessage;
    protected boolean ghostsPresent = true;
    protected boolean pillsPresent = true;
    protected boolean powerPillsPresent = true;
    protected Messenger messenger;
    private double scaleFactor = 1.0d;
    private boolean setDaemon = false;

    /**
     * Creates an Executor with:
     * Ms. Pac-Man Full Observability
     * No Ghost Messaging
     * Ghost Full Observability
     */
    public Executor() {
        this(false);
    }

    /**
     * Creates an Executor with:
     * Specified Ms. Pac-Man Observability
     * No Ghost Messaging
     * Ghost Full Observability
     *
     * @param pacmanPO Whether to impose PO on the pacman
     */
    public Executor(boolean pacmanPO) {
        this(pacmanPO, false, false);
    }

    /**
     * Creates an Executor with:
     * Specified Ms. Pac-Man Observability
     * Specified Ghost Messaging
     * Ghost Partial Observability
     *
     * @param pacmanPO      Whether to impose PO on the pacman
     * @param ghostsMessage Whether to allow ghost messaging
     */
    public Executor(boolean pacmanPO, boolean ghostsMessage) {
        this(pacmanPO, ghostsMessage, true);
    }

    /**
     * Creates an Executor with:
     * Specified Ms. Pac-Man Observability
     * Specified Ghost Messaging
     * Specified Ghost Observability
     *
     * @param pacmanPO      Whether to impose PO on the pacman
     * @param ghostsMessage Whether to allow ghost messaging
     * @param ghostPO       Whether to impose PO on the ghosts
     */
    public Executor(boolean pacmanPO, boolean ghostsMessage, boolean ghostPO) {
        this.pacmanPO = pacmanPO;
        this.ghostsMessage = ghostsMessage;
        this.ghostPO = ghostPO;
        if (this.ghostsMessage) {
            this.messenger = new BasicMessenger(0, 1, 1);
        }
    }

    private static void writeStat(FileWriter writer, Stats stat, int i) throws IOException {
        writer.write(String.format("%s, %d, %f, %f, %f, %f, %d, %f, %f, %f, %d%n",
                stat.getDescription(),
                i,
                stat.getAverage(),
                stat.getSum(),
                stat.getSumsq(),
                stat.getStandardDeviation(),
                stat.getN(),
                stat.getMin(),
                stat.getMax(),
                stat.getStandardError(),
                stat.getMsTaken()));
    }

    //save file for replays
    public static void saveToFile(String data, String name, boolean append) {
        try (FileOutputStream outS = new FileOutputStream(name, append)) {
            PrintWriter pw = new PrintWriter(outS);

            pw.println(data);
            pw.flush();
            outS.close();

        } catch (IOException e) {
            System.out.println("Could not save data!");
        }
    }

    //load a replay
    private static ArrayList<String> loadReplay(String fileName) {
        ArrayList<String> replay = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            String input = br.readLine();

            while (input != null) {
                if (!input.equals("")) {
                    replay.add(input);
                }

                input = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return replay;
    }

    public void setMessenger(Messenger messenger) {
        if (this.ghostsMessage) {
            if (messenger != null) {
                this.messenger = messenger;
            }
        }
    }

    /**
     * For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
     * against a chosen opponent: the random nature of the game means that performance can vary from game to game.
     * Running many games and looking at the average score (and standard deviation/error) helps to get a better
     * idea of how well the controller is likely to do in the competition.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param trials           The number of trials to be executed
     * @param description      Description for the stats
     * @param tickLimit        Tick limit for the games in the experiment
     * @return Stats[] containing the scores in index 0 and the ticks in position 1
     */
    public Stats[] runExperiment(Controller<MOVE> pacManController, MASController ghostController, int trials, String description, int tickLimit) {
        Stats stats = new Stats(description);
        Stats ticks = new Stats(description + " Ticks");
        Random rnd = new Random(0);
        MASController ghostControllerCopy = ghostController.copy(ghostPO);
        Game game;

        Long startTime = System.currentTimeMillis();
        for (int i = 0; i < trials; ) {
            try {
                game = (this.ghostsMessage) ? new Game(rnd.nextLong(), messenger.copy()) : new Game(rnd.nextLong());

                while (!game.gameOver()) {
                    if (tickLimit != -1 && tickLimit < game.getCurrentLevelTime()) {
                        break;
                    }
                    game.advanceGame(
                            pacManController.getMove(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY),
                            ghostControllerCopy.getMove(game.copy(), System.currentTimeMillis() + DELAY));
                }
                stats.add(game.getScore());
                ticks.add(game.getCurrentLevelTime());
                i++;
                System.out.println("Game finished: " + i + "   " + description);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long timeTaken = System.currentTimeMillis() - startTime;
        stats.setMsTaken(timeTaken);
        ticks.setMsTaken(timeTaken);

        return new Stats[]{stats, ticks};
    }

    public Stats[] runExperiment(Controller<MOVE> pacManController, MASController ghostController, int trials, String description) {
        return runExperiment(pacManController, ghostController, trials, description, -1);
    }

    public Stats[] runExperimentTicks(Controller<MOVE> pacManController, MASController ghostController, int trials, String description) {
        Stats stats = new Stats(description);
        Stats ticks = new Stats(description);

        Random rnd = new Random(0);
        MASController ghostControllerCopy = ghostController.copy(ghostPO);
        Game game;

        Long startTime = System.currentTimeMillis();
        for (int i = 0; i < trials; i++) {
            game = (this.ghostsMessage) ? new Game(rnd.nextLong(), messenger.copy()) : new Game(rnd.nextLong());

            while (!game.gameOver()) {
                game.advanceGame(
                        pacManController.getMove(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY),
                        ghostControllerCopy.getMove(game.copy(), System.currentTimeMillis() + DELAY));
            }
            stats.add(game.getScore());
            ticks.add(game.getTotalTime());
        }
        stats.setMsTaken(System.currentTimeMillis() - startTime);
        ticks.setMsTaken(System.currentTimeMillis() - startTime);

        return new Stats[]{stats, ticks};
    }

    /**
     * Run a game in asynchronous mode: the game waits until a move is returned. In order to slow thing down in case
     * the controllers return very quickly, a time limit can be used. If fasted gameplay is required, this delay
     * should be put as 0.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param visual           Indicates whether or not to use visuals
     * @param delay            The delay between time-steps
     */
    public int runGame(Controller<MOVE> pacManController, MASController ghostController, boolean visual, int delay) {
        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;
        MASController ghostControllerCopy = ghostController.copy(ghostPO);

        if (visual) {
            gv = new GameView(game, setDaemon);
            gv.setScaleFactor(scaleFactor);
            gv.showGame();
            if (pacManController instanceof HumanController) {
                //                System.out.println("Here");
                gv.setFocusable(true);
                gv.requestFocus();
                gv.setPO(true);
                gv.addKeyListener(((HumanController) pacManController).getKeyboardInput());
                //                System.out.println("KeyListener added");
            }
        }

        while (!game.gameOver()) {
            game.advanceGame(pacManController.getMove(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), -1), ghostControllerCopy.getMove(game.copy(), -1));

            try {
                Thread.sleep(delay);
            } catch (Exception e) {
            }

            if (visual) {
                gv.repaint();
            }
        }
        System.out.println(game.getScore());
        return game.getScore();
    }

    /**
     * Run the game with time limit (asynchronous mode). This is how it will be done in the competition.
     * Can be played with and without visual display of game states.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param visual           Indicates whether or not to use visuals
     */
    public void runGameTimed(Controller<MOVE> pacManController, MASController ghostController, boolean visual) {
        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;
        MASController ghostControllerCopy = ghostController.copy(ghostPO);

        if (visual) {
            gv = new GameView(game, setDaemon);
            gv.setScaleFactor(scaleFactor);
            gv.showGame();
        }

        if (gv != null && pacManController instanceof HumanController) {
            gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
        }

        new Thread(pacManController).start();
        new Thread(ghostControllerCopy).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY);
            ghostControllerCopy.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            game.advanceGame(pacManController.getMove(), ghostControllerCopy.getMove());

            if (visual) {
                gv.repaint();
            }
        }

        pacManController.terminate();
        ghostControllerCopy.terminate();
    }

    /**
     * Run the game in asynchronous mode but proceed as soon as both controllers replied. The time limit still applies so
     * so the game will proceed after 40ms regardless of whether the controllers managed to calculate a turn.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param fixedTime        Whether or not to wait until 40ms are up even if both controllers already responded
     * @param visual           Indicates whether or not to use visuals
     * @param desc             the description for the stats
     * @return Stat score achieved by Ms. Pac-Man
     */
    public Stats runGameTimedSpeedOptimised(Controller<MOVE> pacManController, MASController ghostController, boolean fixedTime, boolean visual, String desc) {
        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;
        MASController ghostControllerCopy = ghostController.copy(ghostPO);
        Stats stats = new Stats(desc);

        if (visual) {
            gv = new GameView(game, setDaemon);
            gv.setScaleFactor(scaleFactor);
            gv.showGame();
        }

        if (gv != null && pacManController instanceof HumanController) {
            gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
        }

        new Thread(pacManController).start();
        new Thread(ghostControllerCopy).start();
        int ticks = 0;
        while (!game.gameOver()) {
            pacManController.update(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY);
            ghostControllerCopy.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                long waited = DELAY / INTERVAL_WAIT;

                for (int j = 0; j < DELAY / INTERVAL_WAIT; j++) {
                    Thread.sleep(INTERVAL_WAIT);

                    if (pacManController.hasComputed() && ghostControllerCopy.hasComputed()) {
                        waited = j;
                        break;
                    }
                }

                if (fixedTime) {
                    Thread.sleep(((DELAY / INTERVAL_WAIT) - waited) * INTERVAL_WAIT);
                }

                game.advanceGame(pacManController.getMove(), ghostControllerCopy.getMove());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (visual) {
                gv.repaint();
            }

            ticks++;
            if (ticks > 4000) {
                break;
            }
        }

        pacManController.terminate();
        ghostControllerCopy.terminate();
        stats.add(game.getScore());
        return stats;
    }

    /**
     * Run a game in asynchronous mode and recorded.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param visual           Whether to run the game with visuals
     * @param fileName         The file name of the file that saves the replay
     * @return Stats the statistics for the run
     */
    public Stats runGameTimedRecorded(Controller<MOVE> pacManController, MASController ghostController, boolean visual, String fileName) {
        Stats stats = new Stats("");
        StringBuilder replay = new StringBuilder();

        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;
        MASController ghostControllerCopy = ghostController.copy(ghostPO);

        if (visual) {
            gv = new GameView(game, setDaemon);
            gv.setScaleFactor(scaleFactor);
            gv.showGame();

            if (pacManController instanceof HumanController) {
                gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
            }
        }

        new Thread(pacManController).start();
        new Thread(ghostControllerCopy).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY);
            ghostControllerCopy.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            game.advanceGame(pacManController.getMove(), ghostControllerCopy.getMove());

            if (visual) {
                gv.repaint();
            }

            replay.append(game.getGameState() + "\n");
        }
        stats.add(game.getScore());

        pacManController.terminate();
        ghostControllerCopy.terminate();

        saveToFile(replay.toString(), fileName, false);
        return stats;
    }

    /**
     * Replay a previously saved game.
     *
     * @param fileName The file name of the game to be played
     * @param visual   Indicates whether or not to use visuals
     */
    public void replayGame(String fileName, boolean visual) {
        ArrayList<String> timeSteps = loadReplay(fileName);

        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;

        if (visual) {
            gv = new GameView(game, setDaemon);
            gv.setScaleFactor(scaleFactor);
            gv.showGame();
        }

        for (int j = 0; j < timeSteps.size(); j++) {
            game.setGameState(timeSteps.get(j));

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (visual) {
                gv.repaint();
            }
        }
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public void setDaemon(boolean daemon) {
        this.setDaemon = daemon;
    }
}