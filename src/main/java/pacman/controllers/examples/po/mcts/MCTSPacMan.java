package pacman.controllers.examples.po.mcts;

import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.mcts.prediction.GhostLocation;
import pacman.controllers.examples.po.mcts.prediction.fast.GhostPredictionsFast;
import pacman.game.Game;
import pacman.game.internal.Maze;

import java.util.*;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by pwillic on 06/05/2016.
 */
public class MCTSPacMan extends PacmanController {

    public static int DEATH_PENALTY = 1000;
    private int maxDepth = 100;
    private int treeLimit = 50;
    private Random random = new Random();
    private Maze maze;
    private int numberOfLives;
    private PillModel pillModel;

    private List<GhostPredictionsFast> ghostPredictions = new ArrayList<>();

    public MCTSPacMan(int maxDepth, int treeLimit) {
        this.maxDepth = maxDepth;
        this.treeLimit = treeLimit;
    }

    public MCTSPacMan() {

    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        long timeStart = System.currentTimeMillis();
        long endTime = (timeDue == -1) ? timeStart + 35 : timeDue - 5;
        boolean mapChanged = maze != game.getCurrentMaze();
        if (mapChanged) {
            ghostPredictions.clear();
            System.out.println("Next Maze");
        }
        maze = game.getCurrentMaze();
        if (pillModel == null) {
            pillModel = new PillModel(game.getNumberOfNodes());
        }

        // Tracking lives - need to throw away ghost information on death as they go back to the lair
        boolean died = numberOfLives != game.getPacmanNumberOfLivesRemaining();
        numberOfLives = game.getPacmanNumberOfLivesRemaining();
        if (died) {
            ghostPredictions.clear();
        }


        // Populate the list if needed
        if (ghostPredictions.isEmpty()) {
            ghostPredictions.add(new GhostPredictionsFast(game.getCurrentMaze()));
            for (int i = 1; i < maxDepth; i++) {
                GhostPredictionsFast temp = ghostPredictions.get(i - 1).copy();
                temp.update();
                ghostPredictions.add(temp);
            }
        }

        //Update the pill model
        for (int index : game.getPillIndices()) {
            pillModel.observe(index, true);
        }

        // With any luck we can keep it
        boolean updated = false;
        // Update the predictions
        GhostPredictionsFast first = ghostPredictions.get(0);
        for (GHOST ghost : GHOST.values()) {
            int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
            if (ghostIndex != -1) {
                first.observe(ghost, ghostIndex, game.getGhostLastMoveMade(ghost));
                updated = true;
            } else {
                LinkedList<GhostLocation> locationList = new LinkedList<>(first.getGhostLocations(ghost));
                for (GhostLocation location : locationList) {
                    if (game.isNodeObservable(location.getIndex())) {
                        first.observeNotPresent(ghost, location.getIndex());
                        updated = true;
                    }
                }
            }
        }

        if (updated) {
            // Ditch all but the first
            ghostPredictions.clear();
            ghostPredictions.add(first);
            // And repopulate the list
            for (int i = 1; i < maxDepth; i++) {
                GhostPredictionsFast temp = ghostPredictions.get(i - 1).copy();
                temp.update();
                ghostPredictions.add(temp);
            }
        } else {
            // Otherwise just extend it by one
            GhostPredictionsFast next = ghostPredictions.get(ghostPredictions.size() - 1).copy();
            next.update();
            ghostPredictions.add(new GhostPredictionsFast(game.getCurrentMaze()));
        }

        Node root = new Node(this, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade(), pillModel);
        while (System.currentTimeMillis() < endTime) {
            Node current = root.select();
            double value = current.rollout();
            current.updateValues(value);
        }
        ghostPredictions.remove(0);

        // Always need to throw away the first one at the end of the turn
//        System.out.println("Completed: " + root.getNumberOfVisits() + " Updated: " + updated);
        return getBestMove(root);
    }

    private MOVE getBestMove(Node root) {
        double bestValue = -Double.MAX_VALUE;
        int bestIndex = -1;
        if (root == null || root.children == null) return MOVE.LEFT;
        for (int i = 0; i < root.children.length; i++) {
            if (root.children[i] != null) {
                if (root.children[i].getTotalValue() > bestValue) {
                    bestIndex = i;
                    bestValue = root.children[i].getTotalValue();
                }
            }
        }
        if (bestIndex == -1) return MOVE.LEFT;
        return root.children[bestIndex].getMoveToThisState();
    }

    private void printMoves(Node root) {
        if (root.children == null) return;
        for (Node child : root.children) {
            System.out.println("\t Child: " + child.getMoveToThisState() + " Value: " + child.getTotalValue());
        }
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

    public double getPredictions(int depth, int index) {
        return Math.min(DEATH_PENALTY, ghostPredictions.get(depth).calculate(index) * DEATH_PENALTY);
    }
}

class Node {
    private static final double EPSILON = 1E-6;
    private static final double DISCOUNT_FACTOR = 0.95;
    protected Node[] children;
    private MCTSPacMan mctsPacMan;
    private Node parent;
    private int childrenExpandedSoFar = 0;
    private double totalValue = 0;
    private int numberOfVisits = 0;
    private int currentDepth = 0;
    private PacManLocation pacManLocation;
    // This is the score considered to have been obtained by the time we reach this node
    private double rawScore = 0.0d;
    private PillModel pillModel;

    public Node(MCTSPacMan mctsPacMan, int index, MOVE moveToThisNode, PillModel pillModel) {
        this.mctsPacMan = mctsPacMan;
        pacManLocation = new PacManLocation(index, moveToThisNode, mctsPacMan.getMaze());
        this.pillModel = pillModel;
    }

    public Node(Node parent, int index, MOVE moveToThisNode) {
        this.parent = parent;
        this.mctsPacMan = parent.mctsPacMan;
        pacManLocation = new PacManLocation(index, moveToThisNode, mctsPacMan.getMaze());
        this.pillModel = parent.pillModel.copy();
        this.pillModel.update(index);
    }

    public boolean decisionNeeded() {
        return pacManLocation.possibleMoves().length > 1;
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
        // Root - update visits
        current.numberOfVisits++;
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
        children[bestAction].rawScore += (Math.pow(DISCOUNT_FACTOR, currentDepth + 1) * -mctsPacMan.getPredictions(currentDepth + 1, next.getIndex()));
        childrenExpandedSoFar++;
        return children[bestAction];
    }

    public double rollout() {
        int depth = currentDepth;
        double score = rawScore;
        PacManLocation next = pacManLocation.copy();
        PillModel rolloutPillModel = pillModel.copy();
        while (depth < mctsPacMan.getMaxDepth()) {
            int numPossibleMoves = next.possibleMoves().length;
            next.update(next.possibleMoves()[mctsPacMan.getRandom().nextInt(numPossibleMoves)]);
            rolloutPillModel.update(next.getIndex());
            // Penalty for ghosts
            score -= Math.pow(DISCOUNT_FACTOR, depth) * mctsPacMan.getPredictions(depth, next.getIndex());
            depth++;
        }
        return score + rolloutPillModel.getPillsEaten() * 10;
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

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public int getChildrenExpandedSoFar() {
        return childrenExpandedSoFar;
    }

    public MOVE getMoveToThisState() {
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

class PillModel {
    private BitSet pills;
    private int pillsEaten;

    public PillModel(int indices) {
        this.pills = new BitSet(indices);
    }

    // Pacman visited this index
    public void update(int index) {
        if (pills.get(index)) pillsEaten++;
    }

    // There is a pill here!
    public void observe(int index, boolean pillThere) {
        pills.set(index, pillThere);
    }

    public int getPillsEaten() {
        return pillsEaten;
    }

    public PillModel copy() {
        PillModel other = new PillModel(this.pills.length());
        other.pills = (BitSet) this.pills.clone();
        return other;
    }
}
