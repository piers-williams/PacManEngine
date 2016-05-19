package pacman;

import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.controllers.examples.po.POGhosts;
import pacman.controllers.examples.po.POPacMan;
import pacman.controllers.examples.po.mcts.MCTSPacMan;
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
    protected final boolean ghostsMessage;

    protected Messenger messenger;

    public Executor() {
        this.pacmanPO = false;
        this.ghostsMessage = false;
    }

    public Executor(boolean pacmanPO) {
        this.pacmanPO = pacmanPO;
        this.ghostsMessage = false;
    }

    public Executor(boolean pacmanPO, boolean ghostsMessage) {
        this.pacmanPO = pacmanPO;
        this.ghostsMessage = ghostsMessage;
        if (this.ghostsMessage) {
            this.messenger = new BasicMessenger(0, 1, 1);
        }
    }

    /**
     * The main method. Several options are listed - simply remove comments to use the option you want.
     *
     * @param args the command line arguments
     * @throws IOException It just does
     */
    public static void main(String[] args) throws IOException {
        Executor exec = new Executor();

        Executor poExec = new Executor(true, true);
        Executor nonPOCommExec = new Executor(false, true);

//        exec.runGameTimed(new POPacMan(), new StarterGhosts(), true);

//        poExec.runGame(new MCTSPacMan(), new POGhosts(), true, 40);
        int n = 10;
//
//        Stats[] data = poExec.runExperimentTicks(new POPacMan(), new POCommGhosts(50), n, "POP Vs POGC");
//        for(Stats stat : data){
//            System.out.println(stat);
//        }

        int ticks = 4000;

        Stats[][] results = new Stats[8][2];
        results[0] = poExec.runExperiment(new POPacMan(), new POGhosts(), n, "POP Vs POG", ticks);
        results[1] = exec.runExperiment(new StarterPacMan(), new POGhosts(), n, "COP Vs POG", ticks);
        results[2] = poExec.runExperiment(new POPacMan(), new StarterGhosts(), n, "POP Vs COG", ticks);
        results[3] = exec.runExperiment(new StarterPacMan(), new StarterGhosts(), n, "COP Vs COG", ticks);
        results[4] = nonPOCommExec.runExperiment(new StarterPacMan(), new POCommGhosts(50), n, "COP Vs POGC", ticks);
        results[5] = poExec.runExperiment(new POPacMan(), new POCommGhosts(50), n, "POP Vs POGC", ticks);
        results[7] = poExec.runExperiment(new MCTSPacMan(), new POCommGhosts(50), n, "MCTS Vs POGC", ticks);
        results[6] = poExec.runExperiment(new MCTSPacMan(), new StarterGhosts(), n, "MCTS  Vs COG", ticks);
//
//
////
        System.out.println("\n");
        for (int i = 0; i < results.length; i++) {
            System.out.println("\t" + results[i][0]);
            System.out.println("\t" + results[i][1]);
        }

//        File file = new File("results/thresholdCOP.csv");
//        file.createNewFile();
//        File filePop = new File("results/thresholdPOP.csv");
//        filePop.createNewFile();
//
//        FileWriter writerCOP = new FileWriter(file);
//        FileWriter writerPOP = new FileWriter(filePop);
//        String format = "AI, n, avg, sum, sumsq, sd, n, min, max, stdErr, ms\n";
//        writerCOP.write(format);
//        writerPOP.write(format);
//
//        for(int i = 0; i <= 200; i+=1){
//            System.out.println("Starting variable: " + i);
//            Stats stat = (nonPOCommExec.runExperiment(new StarterPacMan(), new POCommGhosts(i), n, "COP VS POGC"));
//            writeStat(writerCOP, stat, i);
//            stat = (poExec.runExperiment(new POPacMan(), new POCommGhosts(i), n, "POP VS POGC"));
//            writeStat(writerPOP, stat, i);
//        }
//
//        writerCOP.flush();
//        writerPOP.flush();
//        writerCOP.close();
//        writerPOP.close();
        // Test effectiveness of comm ghost with increasing message delay
//        Executor risingComm = new MultiExecutor(false, true);
//        for(int i = 0; i < 25; i++){
//            risingComm.setMessenger(new BasicMessenger(0, i, 0));
//            System.out.println(risingComm.runExperiment(new StarterPacMan(), new POCommGhosts(), n, "Delay: " + i));
//        }


		/*
        //run multiple games in batch mode - good for testing.
		int numTrials=10;
		exec.runExperiment(new RandomPacMan(),new RandomGhosts(),numTrials);
		 */

		/*
        //run a game in synchronous mode: game waits until controllers respond.
		int delay=5;
		boolean visual=true;
		exec.runGame(new RandomPacMan(),new RandomGhosts(),visual,delay);
  		 */

        ///*
        //run the game in asynchronous mode.
//		boolean visual=true;
//		exec.runGameTimed(new NearestPillPacMan(),new AggressiveGhosts(),visual);
//		exec.runGameTimed(new StarterPacMan(),new StarterGhosts(),visual);
//		exec.runGameTimed(new HumanController(new KeyBoardInput()),new StarterGhosts(),visual);
        //*/

		/*
        //run the game in asynchronous mode but advance as soon as both controllers are ready  - this is the mode of the competition.
		//time limit of DELAY ms still applies.
		boolean visual=true;
		boolean fixedTime=false;
		exec.runGameTimedSpeedOptimised(new RandomPacMan(),new RandomGhosts(),fixedTime,visual);
		*/

		/*
        //run game in asynchronous mode and record it to file for replay at a later stage.
		boolean visual=true;
		String fileName="replay.txt";
		exec.runGameTimedRecorded(new HumanController(new KeyBoardInput()),new RandomGhosts(),visual,fileName);
		//exec.replayGame(fileName,visual);
		 */
    }

    private static void writeStat(FileWriter writer, Stats stat, int i) throws IOException {
        writer.write(String.format("%s, %d, %f, %f, %f, %f, %d, %f, %f, %f, %d\n",
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
        try {
            FileOutputStream outS = new FileOutputStream(name, append);
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

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String input = br.readLine();

            while (input != null) {
                if (!input.equals(""))
                    replay.add(input);

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
    public Stats[] runExperiment(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, int trials, String description, int tickLimit) {
        Stats stats = new Stats(description);
        Stats ticks = new Stats(description + " Ticks");
        Random rnd = new Random(0);
        Game game;

        Long startTime = System.currentTimeMillis();
        for (int i = 0; i < trials; ) {
            try {
                game = (this.ghostsMessage) ? new Game(rnd.nextLong(), messenger.copy()) : new Game(rnd.nextLong());

                while (!game.gameOver()) {
                    if (tickLimit != -1 && tickLimit < game.getCurrentLevelTime()) break;
                    game.advanceGame(
                            pacManController.getMove(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY),
                            ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
                }
                stats.add(game.getScore());
                ticks.add(game.getCurrentLevelTime());
                i++;
                System.out.println("Game finished: " + i + "   " + description);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        long timeTaken = System.currentTimeMillis() - startTime;
        stats.setMsTaken(timeTaken);
        ticks.setMsTaken(timeTaken);

        return new Stats[]{stats, ticks};
    }

    public Stats[] runExperiment(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, int trials, String description) {
        return runExperiment(pacManController, ghostController, trials, description, -1);
    }

    public Stats[] runExperimentTicks(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, int trials, String description) {
        Stats stats = new Stats(description);
        Stats ticks = new Stats(description);

        Random rnd = new Random(0);
        Game game;

        Long startTime = System.currentTimeMillis();
        for (int i = 0; i < trials; i++) {
            game = (this.ghostsMessage) ? new Game(rnd.nextLong(), messenger.copy()) : new Game(rnd.nextLong());

            while (!game.gameOver()) {
                game.advanceGame(
                        pacManController.getMove(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY),
                        ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
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
    public void runGame(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual, int delay) {
        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;

        if (visual)
            gv = new GameView(game).showGame();

        while (!game.gameOver()) {
            game.advanceGame(pacManController.getMove(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), -1), ghostController.getMove(game.copy(), -1));

            try {
                Thread.sleep(delay);
            } catch (Exception e) {
            }

            if (visual)
                gv.repaint();
        }
    }

    /**
     * Run the game with time limit (asynchronous mode). This is how it will be done in the competition.
     * Can be played with and without visual display of game states.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param visual           Indicates whether or not to use visuals
     */
    public void runGameTimed(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual) {
        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;

        if (visual)
            gv = new GameView(game).showGame();

        if (pacManController instanceof HumanController)
            gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());

        new Thread(pacManController).start();
        new Thread(ghostController).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY);
            ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            game.advanceGame(pacManController.getMove(), ghostController.getMove());

            if (visual)
                gv.repaint();
        }

        pacManController.terminate();
        ghostController.terminate();
    }

    /**
     * Run the game in asynchronous mode but proceed as soon as both controllers replied. The time limit still applies so
     * so the game will proceed after 40ms regardless of whether the controllers managed to calculate a turn.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param fixedTime        Whether or not to wait until 40ms are up even if both controllers already responded
     * @param visual           Indicates whether or not to use visuals
     * @return int score achieved by Ms. Pac-Man
     */
    public int runGameTimedSpeedOptimised(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean fixedTime, boolean visual) {
        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;

        if (visual)
            gv = new GameView(game).showGame();

        if (pacManController instanceof HumanController)
            gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());

        new Thread(pacManController).start();
        new Thread(ghostController).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY);
            ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                int waited = DELAY / INTERVAL_WAIT;

                for (int j = 0; j < DELAY / INTERVAL_WAIT; j++) {
                    Thread.sleep(INTERVAL_WAIT);

                    if (pacManController.hasComputed() && ghostController.hasComputed()) {
                        waited = j;
                        break;
                    }
                }

                if (fixedTime)
                    Thread.sleep(((DELAY / INTERVAL_WAIT) - waited) * INTERVAL_WAIT);

                game.advanceGame(pacManController.getMove(), ghostController.getMove());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (visual)
                gv.repaint();
        }

        pacManController.terminate();
        ghostController.terminate();
        return game.getScore();
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
    public Stats runGameTimedRecorded(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual, String fileName) {
        Stats stats = new Stats("");
        StringBuilder replay = new StringBuilder();

        Game game = (this.ghostsMessage) ? new Game(0, messenger.copy()) : new Game(0);

        GameView gv = null;

        if (visual) {
            gv = new GameView(game).showGame();

            if (pacManController instanceof HumanController)
                gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
        }

        new Thread(pacManController).start();
        new Thread(ghostController).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy((pacmanPO) ? GHOST.values().length + 1 : -1), System.currentTimeMillis() + DELAY);
            ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            game.advanceGame(pacManController.getMove(), ghostController.getMove());

            if (visual)
                gv.repaint();

            replay.append(game.getGameState() + "\n");
        }
        stats.add(game.getScore());

        pacManController.terminate();
        ghostController.terminate();

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

        if (visual)
            gv = new GameView(game).showGame();

        for (int j = 0; j < timeSteps.size(); j++) {
            game.setGameState(timeSteps.get(j));

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (visual)
                gv.repaint();
        }
    }
}