package pacman.controllers.examples.po.mcts;

import pacman.controllers.Controller;
import pacman.controllers.examples.po.mcts.prediction.GhostLocation;
import pacman.controllers.examples.po.mcts.prediction.GhostPredictions;
import pacman.game.Game;
import pacman.game.internal.Maze;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by pwillic on 06/05/2016.
 */
public class MCTSPacMan extends Controller<MOVE> {

    private int maxDepth = 100;
    private int treeLimit = 10;
    private Random random = new Random();
    private Maze maze;
    private static int DEATH_PENALTY = 100;

    private List<GhostPredictions> ghostPredictions = new ArrayList<>();

    public MCTSPacMan(int maxDepth, int treeLimit) {
        this.maxDepth = maxDepth;
        this.treeLimit = treeLimit;
    }

    public MCTSPacMan(){

    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        long timeStart = System.currentTimeMillis();
        long endTime = (timeDue == -1)? timeStart + 35 : timeDue - 5;
        maze = game.getCurrentMaze();
        Node root = new Node(this, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());

        ghostPredictions.clear();
        ghostPredictions.add(new GhostPredictions(game.getCurrentMaze()));

        // Update the predictions
        for (GHOST ghost : GHOST.values()) {
            int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
            if (ghostIndex != -1) {
                ghostPredictions.get(0).observe(ghost, ghostIndex, game.getGhostLastMoveMade(ghost));
            } else {
                LinkedList<GhostLocation> locationList = new LinkedList<>(ghostPredictions.get(0).getGhostLocations(ghost));
                for (GhostLocation location : locationList) {
                    if (game.isNodeObservable(location.getIndex())) {
                        ghostPredictions.get(0).observeNotPresent(ghost, location.getIndex());
                    }
                }
            }
        }

        for (int i = 1; i < maxDepth; i++) {
            GhostPredictions temp = ghostPredictions.get(i - 1).copy();
            temp.update();
            ghostPredictions.add(temp);
        }

        int iterations = 0;
//        System.out.println(endTime + ":" + System.currentTimeMillis());
        while (System.currentTimeMillis() < endTime) {
//            System.out.println("Starting: " + iterations);
            Node current = root.select();
//            System.out.println("Finished select");
            double value = current.rollout();
            value *= -1;
            value /= DEATH_PENALTY;
//            System.out.println("Finished rollout");
            current.updateValues(value);
            iterations++;
        }

//        System.out.println("MCTS Took: " + (System.currentTimeMillis() - timeStart) + " Completed: " + iterations);
        return getBestMove(root);
    }

    private MOVE getBestMove(Node root){
        double bestValue = -Double.MAX_VALUE;
        int bestIndex = -1;
        for(int i = 0; i < root.getChildrenExpandedSoFar(); i++){
            if(root.children[i].getTotalValue() > bestValue){
                bestIndex = i;
                bestValue = root.children[i].getTotalValue();
            }
        }
        if(bestIndex == -1) return MOVE.LEFT;
        return root.children[bestIndex].getMoveToThisState();
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getTreeLimit() {
        return treeLimit;
    }

    public Random getRandom() {
        return random;
    }

    public Maze getMaze() {
        return maze;
    }

    public double getPredictions(int depth, int index){
        return Math.min(DEATH_PENALTY, ghostPredictions.get(depth).calculate(index) * DEATH_PENALTY);
    }
}

class Node {
    private static final double EPSILON = 1E-6;
    private static final double DISCOUNT_FACTOR = 0.99;
    protected Node[] children;
    private MCTSPacMan mctsPacMan;
    private Node parent;
    private int childrenExpandedSoFar = 0;
    private double totalValue = 0;
    private int numberOfVisits = 0;
    private int currentDepth = 0;
    private PacManLocation pacManLocation;
    // This is the score considered to have been obtained at this node
    private double rawScore = 0.0d;

    public Node(MCTSPacMan mctsPacMan, int index, MOVE moveToThisNode) {
        this.mctsPacMan = mctsPacMan;
        pacManLocation = new PacManLocation(index, moveToThisNode, mctsPacMan.getMaze());
    }

    public Node(Node parent, int index, MOVE moveToThisNode) {
        this.parent = parent;
        this.mctsPacMan = parent.mctsPacMan;
        pacManLocation = new PacManLocation(index, moveToThisNode, mctsPacMan.getMaze());
    }

    public Node select() {
        Node current = this;

        while (current.currentDepth < mctsPacMan.getTreeLimit()) {
            if (current.isFullyExpanded()) {
                current = current.selectBestChild();
            } else {
                current = current.expand();
                return current;
            }
        }
        return current;
    }

    public Node selectBestChild() {
        int selected = 0;
        double bestValue = children[0].calculateChild();
        for (int child = 1; child < children.length; child++) {
            double childValue = children[child].calculateChild();
            if (childValue > bestValue) {
                bestValue = childValue;
                selected = child;
            }
        }
        return children[selected];
    }

    public void updateValues(double value) {
        Node current = this;
        while (current.parent != null) {
            current.totalValue += value;
            current.numberOfVisits++;
            current = current.parent;
        }
    }

    public Node expand() {
        int bestAction = 0;
        double bestValue = -Double.MAX_VALUE;
        if (children == null) children = new Node[pacManLocation.possibleMoves().length];
        Random random = mctsPacMan.getRandom();
        for (int i = 0; i < children.length; i++) {
            double x = random.nextDouble();
            if (children[i] == null && x > bestValue) {
                bestAction = i;
                bestValue = x;
            }
        }
        PacManLocation next = pacManLocation.copy();
        next.update(pacManLocation.possibleMoves()[bestAction]);
        children[bestAction] = new Node(this, next.getIndex(), next.getLastMoveMade());
        children[bestAction].rawScore = rawScore + (Math.pow(DISCOUNT_FACTOR, currentDepth + 1) * -mctsPacMan.getPredictions(currentDepth + 1, next.getIndex()));
        childrenExpandedSoFar++;
        return children[bestAction];
    }

    public double rollout() {
        int depth = currentDepth;
        double score = rawScore;
        PacManLocation next = pacManLocation.copy();
        while (depth < mctsPacMan.getMaxDepth()) {
            int numPossibleMoves = next.possibleMoves().length;
            next.update(next.possibleMoves()[mctsPacMan.getRandom().nextInt(numPossibleMoves)]);
            score += Math.pow(DISCOUNT_FACTOR, depth) * mctsPacMan.getPredictions(depth, next.getIndex());
            depth++;
        }
        return -score;
    }

    private boolean isFullyExpanded() {
        return children != null && childrenExpandedSoFar == children.length;
    }

    private double calculateChild() {
        return totalValue / (numberOfVisits + EPSILON) +
                Math.sqrt(2 * Math.log(parent.numberOfVisits + 1) / (numberOfVisits + EPSILON)) +
                mctsPacMan.getRandom().nextDouble() * EPSILON;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public int getChildrenExpandedSoFar() {
        return childrenExpandedSoFar;
    }

    public MOVE getMoveToThisState(){
        return pacManLocation.getLastMoveMade();
    }
}

class PacManLocation {
    private int index;
    private MOVE lastMoveMade;
    private Maze maze;

    public PacManLocation(int index, MOVE lastMoveMade, Maze maze) {
        this.index = index;
        this.lastMoveMade = lastMoveMade;
        this.maze = maze;
    }

    public MOVE[] possibleMoves() {
//        System.out.println(lastMoveMade);
//        System.out.println(maze);
//        System.out.println(maze.graph);
//        System.out.println(maze.graph[index].allPossibleMoves);
        return maze.graph[index].allPossibleMoves.get(lastMoveMade);
    }

    public void update(MOVE move) {
        index = maze.graph[index].neighbourhood.get(move);
        lastMoveMade = move;
    }

    public PacManLocation copy() {
        return new PacManLocation(this.index, this.lastMoveMade, this.maze);
    }

    public int getIndex() {
        return index;
    }

    public MOVE getLastMoveMade() {
        return lastMoveMade;
    }

    public Maze getMaze() {
        return maze;
    }
}
