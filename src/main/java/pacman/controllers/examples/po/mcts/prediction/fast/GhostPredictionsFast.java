package pacman.controllers.examples.po.mcts.prediction.fast;

import pacman.controllers.examples.po.mcts.prediction.GhostLocation;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by Piers on 16/05/2016.
 */
public class GhostPredictionsFast {
    // First mazeSize indices are for ghost Ordinal 0 etc ...
    private double[] probabilities;
    private double[] backProbabilities;
    private MOVE[] moves;
    private MOVE[] backMoves;

    private Maze maze;
    private int mazeSize;
    private static final int numGhosts = GHOST.values().length;

    public GhostPredictionsFast(Maze maze) {
        this.maze = maze;
        mazeSize = maze.graph.length;
        probabilities = new double[mazeSize * numGhosts];
        backProbabilities = new double[mazeSize *numGhosts];
        moves = new MOVE[mazeSize * numGhosts];
        backMoves = new MOVE[mazeSize * numGhosts];
    }

    public void observe(GHOST ghost, int index, MOVE lastMoveMade) {
        int startIndex = (ghost.ordinal() * mazeSize);
        int arrayIndex = startIndex + index;
        Arrays.fill(probabilities, startIndex, startIndex + mazeSize, 0);
        Arrays.fill(moves, startIndex, startIndex + mazeSize, null);
        probabilities[arrayIndex] = 1.0d;
        moves[arrayIndex] = lastMoveMade;
    }

    public void observeNotPresent(GHOST ghost, int index) {
        int startIndex = (ghost.ordinal() * mazeSize);
        int arrayIndex = startIndex + index;
        double probabilityAdjustment = (1 - probabilities[arrayIndex]);
        probabilities[arrayIndex] = 0;
        moves[arrayIndex] = null;
        for (int i = startIndex; i < startIndex + mazeSize; i++) {
            probabilities[i] /= probabilityAdjustment;
        }
    }

    public void update() {
        for(int ghost = 0; ghost < numGhosts; ghost++) {
            for (int i = (mazeSize * ghost); i < (mazeSize * (ghost + 1)); i++) {
                if (probabilities[i] > 0) {
                    Node currentNode = maze.graph[i % mazeSize];
                    int numberNodes = currentNode.numNeighbouringNodes;
                    double probability = probabilities[i] / (numberNodes - 1);
                    MOVE back = moves[i].opposite();
                    for (MOVE move : MOVE.values()) {
                        if (move == back) continue;
                        if (currentNode.neighbourhood.containsKey(move)) {
                            int index = currentNode.neighbourhood.get(move);
                            // If we haven't already written to there or what we wrote was less probable
                            if (backProbabilities[(mazeSize * ghost) + index] <= probabilities[(mazeSize * ghost) + index]) {
                                backProbabilities[(mazeSize * ghost) + index] = probability;
                                backMoves[(mazeSize * ghost) + index] = move;
                            }
                        }
                    }
                }
            }
        }

        System.arraycopy(backProbabilities, 0, probabilities, 0, probabilities.length);
        Arrays.fill(backProbabilities, 0.0d);

        System.arraycopy(backMoves, 0, moves, 0, moves.length);
        Arrays.fill(backMoves, null);
    }

    public final double calculate(int index) {
        if(index >= mazeSize) System.out.println("Index was too large: " + index);
        double sum = 0.0d;
        for(int ghost = 0; ghost < numGhosts; ghost++){
            sum += probabilities[(mazeSize * ghost) + index];
        }
        return sum;
    }

    public EnumMap<GHOST, GhostLocation> sampleLocations() {
        EnumMap<GHOST, GhostLocation> results = new EnumMap<GHOST, GhostLocation>(GHOST.class);

        for(int ghost = 0; ghost < numGhosts; ghost++){
            double x = Math.random();
            double sum = 0.0d;
            for(int i = (mazeSize * ghost); i < (mazeSize * (ghost + 1)); i++){
                sum += probabilities[i];
                if(sum >= x){
                    results.put(GHOST.values()[ghost], new GhostLocation(i % mazeSize, moves[i], probabilities[i]));
                    break;
                }

            }
        }
        return results;
    }

    public GhostPredictionsFast copy() {
        GhostPredictionsFast other = new GhostPredictionsFast(this.maze);
        System.arraycopy(this.probabilities, 0, other.probabilities, 0, probabilities.length);
        System.arraycopy(this.backProbabilities, 0, other.backProbabilities, 0, backProbabilities.length);
        System.arraycopy(this.moves, 0, other.moves, 0, moves.length);
        System.arraycopy(this.backMoves, 0, other.backMoves, 0, backMoves.length);
        return other;
    }

    public List<GhostLocation> getGhostLocations(GHOST ghost){
        ArrayList<GhostLocation> locations = new ArrayList<>();
        for(int i = ghost.ordinal() * mazeSize; i < (ghost.ordinal() + 1) * mazeSize; i++){
            if(probabilities[i] > 0){
                locations.add(new GhostLocation(i % mazeSize, moves[i], probabilities[i]));
            }
        }
        return locations;
    }

    public List<GhostLocation> getGhostLocations(){
        ArrayList<GhostLocation> locations = new ArrayList<>();
        for(int i = 0; i < probabilities.length; i++){
            if(probabilities[i] > 0){
                locations.add(new GhostLocation(i % mazeSize, moves[i], probabilities[i]));
            }
        }
        return locations;
    }

    public String getGhostInfo(GHOST ghost){
        List<GhostLocation> ghostLocations = getGhostLocations(ghost);
        return "IndividualLocations{" +
                "length: " + ghostLocations.size() +
                "ghostLocations=" + ghostLocations +
                '}';
    }
}