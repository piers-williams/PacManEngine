package pacman.test;

import pacman.controllers.examples.po.mcts.prediction.GhostPredictions;
import pacman.controllers.examples.po.mcts.prediction.fast.GhostPredictionsFast;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.comms.BasicMessenger;

/**
 * Created by pwillic on 12/05/2016.
 */
public class GhostPredictionsTest {

    public static void main(String[] args) {
        Game game = new Game(System.currentTimeMillis(), new BasicMessenger(0, 1, 1));

        GhostPredictions predictor = new GhostPredictions(game.getCurrentMaze());
        GhostPredictionsFast predictionsFast = new GhostPredictionsFast(game.getCurrentMaze());
        predictor.observe(Constants.GHOST.INKY, 6, game.getPossibleMoves(6)[0]);
        predictionsFast.observe(Constants.GHOST.INKY, 6, game.getPossibleMoves(6)[0]);


        for (int i = 0; i < 100; i++) {
            predictor.update();
            predictionsFast.update();
            System.out.println(predictor.getGhostInfo(Constants.GHOST.INKY));
            System.out.println(predictionsFast.getGhostInfo(Constants.GHOST.INKY));

        }
        System.out.println(predictor.sampleLocations());
        System.out.println(predictor.sampleLocations());
        System.out.println(predictor.sampleLocations());


        System.out.println("\n\n\n Observing unseen things");

        predictor = new GhostPredictions(game.getCurrentMaze());
        predictionsFast = new GhostPredictionsFast(game.getCurrentMaze());
        predictor.observe(Constants.GHOST.INKY, 165, game.getPossibleMoves(165)[0]);
        predictionsFast.observe(Constants.GHOST.INKY, 165, game.getPossibleMoves(165)[0]);
        System.out.println(predictor.getGhostInfo(Constants.GHOST.INKY));
        System.out.println(predictionsFast.getGhostInfo(Constants.GHOST.INKY));
        predictor.update();
        predictionsFast.update();
        System.out.println(predictor.getGhostInfo(Constants.GHOST.INKY));
        System.out.println(predictionsFast.getGhostInfo(Constants.GHOST.INKY));
        predictor.observeNotPresent(Constants.GHOST.INKY, 166);
        predictionsFast.observeNotPresent(Constants.GHOST.INKY, 166);
        System.out.println(predictor.getGhostInfo(Constants.GHOST.INKY));
        System.out.println(predictionsFast.getGhostInfo(Constants.GHOST.INKY));


        System.out.println(predictor.sampleLocations());
    }
}