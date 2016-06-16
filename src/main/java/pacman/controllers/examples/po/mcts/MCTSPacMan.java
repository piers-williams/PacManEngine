package pacman.controllers.examples.po.mcts;

import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.mcts.prediction.GhostLocation;
import pacman.controllers.examples.po.mcts.prediction.fast.GhostPredictionsFast;
import pacman.game.Game;
import pacman.game.internal.Maze;
import pacman.game.util.Stats;

import java.util.*;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by pwillic on 06/05/2016.
 */
public class MCTSPacMan extends PacmanController {

    public static int DEATH_PENALTY = 1;
    public static int PILL_GAIN = 1;
    private int maxDepth = 150;
    private int treeLimit = 50;
    private Random random = new Random();
    private Maze maze;
    private int numberOfLives;
    private PillModel pillModel;

    private List<GhostPredictionsFast> ghostPredictions = new ArrayList<>();

    private boolean enableStatistics = false;
    // Statistics - [ms for start, iterations in loop, refreshStats]
    private Stats[] stats;

    public MCTSPacMan(int maxDepth, int treeLimit) {
        this.maxDepth = maxDepth;
        this.treeLimit = treeLimit;
    }

    public MCTSPacMan() {

    }

    public void setEnableStatistics(boolean enableStatistics) {
        this.enableStatistics = enableStatistics;
        if (this.enableStatistics) {
            stats = new Stats[3];
            stats[0] = new Stats("Turn maintenance timing");
            stats[1] = new Stats("Iterations in loop");
            stats[2] = new Stats("Statistics Refresh");
        }
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        long timeStart = System.currentTimeMillis();
        long endTime = (timeDue == -1) ? timeStart + 35 : timeDue - 5;
        boolean mapChanged = maze != game.getCurrentMaze();
        if (mapChanged) {
            ghostPredictions.clear();
            pillModel = null;
            System.out.println("Next Maze");
        }
        maze = game.getCurrentMaze();
        if (pillModel == null) {
            pillModel = new PillModel(game.getNumberOfNodes());
            for (int index : maze.pillIndices) {
                pillModel.observe(index, true);
            }
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

//        //Update the pill model
        for (int index : game.getPillIndices()) {
            pillModel.observe(index, true);
        }

        // With any luck we can keep it
        boolean updated = false;
        boolean ghostSeen = false;
        // Update the predictions
        GhostPredictionsFast first = ghostPredictions.get(0);
        for (GHOST ghost : GHOST.values()) {
            int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
            if (ghostIndex != -1) {
                first.observe(ghost, ghostIndex, game.getGhostLastMoveMade(ghost));
                updated = true;
                ghostSeen = true;
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
            if (enableStatistics) {
                stats[2].add(1);
            }
        } else {
            // Otherwise just extend it by one
            GhostPredictionsFast next = ghostPredictions.get(ghostPredictions.size() - 1).copy();
            next.update();
            ghostPredictions.add(new GhostPredictionsFast(game.getCurrentMaze()));
        }
        if (enableStatistics) {
            stats[0].add(System.currentTimeMillis() - timeStart);
        }

        Node root = new Node(this, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade(), pillModel);
        if (root.decisionNeeded() || ghostSeen) {
            while (System.currentTimeMillis() < endTime) {
                Node current = root.select();
                double value = current.rollout();
                value++;
                value /= 2;
//                System.out.println("Value: " + value);
                current.updateValues(value);
            }
            if (enableStatistics) {
//                System.out.println("Completed: " + root.getNumberOfVisits() + " Updated: " + updated);
                stats[1].add(root.getNumberOfVisits());
            }
        }
        // Always need to throw away the first one at the end of the turn
        ghostPredictions.remove(0);

        if (root.decisionNeeded() || ghostSeen) {
            printMoves(root);
            return getBestMove(root);
        } else {
            return root.getMoveToThisState();
        }
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
        System.out.println("Moves\n");
        for (Node child : root.children) {
            System.out.println("\t Child: " + child.getMoveToThisState() + " Ghost Penalty: " + child.getGhostPenalty());
            System.out.println("\t Child: " + child.getMoveToThisState() + " Pill Score: " + child.getPillScore());
            System.out.println("\t Child: " + child.getMoveToThisState() + " Total Value: " + child.getTotalValue());
            System.out.println("\t Child: " + child.getMoveToThisState() + " Number Visits: " + child.getNumberOfVisits());
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
//        return 0;
        return ghostPredictions.get(depth).calculate(index) * DEATH_PENALTY;
    }

    public Stats[] getStats() {
        return stats;
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

    // This is the depth in tree
    private int currentDepth = 0;
    private PacManLocation pacManLocation;
    private double pillScore = 0;
    private double ghostPenalty = 0;


    private PillModel pillModel;

    public Node(MCTSPacMan mctsPacMan, int index, MOVE moveToThisNode, PillModel pillModel) {
        this.mctsPacMan = mctsPacMan;
        pacManLocation = new PacManLocation(index, moveToThisNode, mctsPacMan.getMaze());
        this.pillModel = pillModel;
    }

    public Node(Node parent, int index, MOVE moveToThisNode) {
        this.parent = parent;
        this.currentDepth = parent.currentDepth++;
        this.mctsPacMan = parent.mctsPacMan;
        pacManLocation = new PacManLocation(index, moveToThisNode, mctsPacMan.getMaze());
        this.pillScore = parent.pillScore;
        this.ghostPenalty = parent.ghostPenalty;
    }

    public boolean decisionNeeded() {
        return pacManLocation.possibleMoves().length > 1;
    }

    public Node select() {
        Node current = this;

        while (current.currentDepth < mctsPacMan.getTreeLimit()) {
            if(current.isLeaf()) return current;
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
        double discount = 1;
        while (current.parent != null) {
            current.totalValue += value * discount;
            current.numberOfVisits++;
            current = current.parent;
            discount *= DISCOUNT_FACTOR;
        }
        // Root - update visits
        current.numberOfVisits++;
    }

    public Node expand() {
        int bestAction = 0;
        double bestValue = -Double.MAX_VALUE;
        if (children == null) {
            children = new Node[(parent == null) ? pacManLocation.allPossibleMovesIncludingBackwards().length : pacManLocation.possibleMoves().length];
//            children = new Node[pacManLocation.possibleMoves().length];
        }
        Random random = mctsPacMan.getRandom();
        for (int i = 0; i < children.length; i++) {
            double x = random.nextDouble();
            if (children[i] == null && x > bestValue) {
                bestAction = i;
                bestValue = x;
            }
        }
        PacManLocation next = pacManLocation.copy();
        // Loop until we get to the next decision
        MOVE bestMOVE = (parent == null) ? pacManLocation.allPossibleMovesIncludingBackwards()[bestAction] : pacManLocation.possibleMoves()[bestAction];
//        MOVE bestMOVE =  pacManLocation.possibleMoves()[bestAction];
        int depth = currentDepth++;
        next.update(bestMOVE);
        PillModel childModel = pillModel.copy();
        double ghostPenalty = 0.0d;
        while (true) {
            depth++;
            if (depth >= mctsPacMan.getMaxDepth() - 1) break;
            // decision
            if (next.possibleMoves().length >= 2) break;
            // That move no longer available
            if (next.possibleMoves()[0] != bestMOVE) break;
            next.update(bestMOVE);
            childModel.update(next.getIndex());
            ghostPenalty += mctsPacMan.getPredictions(depth, next.getIndex());
            if(ghostPenalty >= MCTSPacMan.DEATH_PENALTY) break;
        }
        children[bestAction] = new Node(this, next.getIndex(), next.getLastMoveMade());
        children[bestAction].ghostPenalty += -ghostPenalty;
        children[bestAction].pillModel = childModel;
        children[bestAction].pillScore = childModel.getPillsFraction() * MCTSPacMan.PILL_GAIN;
//        System.out.println("Pill Score: " + children[bestAction].pillScore );
        childrenExpandedSoFar++;
        return children[bestAction];
    }

    public double rollout() {
        int depth = currentDepth;
        double rolloutGhostPenalty = ghostPenalty;
        PacManLocation next = pacManLocation.copy();
        PillModel rolloutPillModel = pillModel.copy();

        while (depth < mctsPacMan.getMaxDepth() - 1) {
            if (rolloutGhostPenalty >= MCTSPacMan.DEATH_PENALTY) break;
            int numPossibleMoves = next.possibleMoves().length;
            next.update(next.possibleMoves()[mctsPacMan.getRandom().nextInt(numPossibleMoves)]);
            rolloutPillModel.update(next.getIndex());
            rolloutGhostPenalty -= mctsPacMan.getPredictions(depth, next.getIndex());
            depth++;
        }
//        System.out.println(rolloutGhostPenalty + ": " + rolloutPillScore);
        return rolloutGhostPenalty + rolloutPillModel.getPillsFraction();
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

    public boolean isLeaf(){
        return (ghostPenalty >= MCTSPacMan.DEATH_PENALTY);
    }

    public double getPillScore() {
        return pillScore;
    }

    public double getGhostPenalty() {
        return ghostPenalty;
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

    // Returns all possible moves except the last move made.
    public MOVE[] possibleMoves() {
        return maze.graph[index].allPossibleMoves.get(lastMoveMade);
    }

    public MOVE[] allPossibleMovesIncludingBackwards() {
        return maze.graph[index].neighbourhood.keySet().toArray(new MOVE[maze.graph[index].neighbourhood.keySet().size()]);
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
    private double totalPills;

    public PillModel(int indices) {
        this.totalPills = indices;
        this.pills = new BitSet(indices);
    }

    // Pacman visited this index
    public void update(int index) {
        if (pills.get(index)) {
            pillsEaten++;
            pills.set(index, false);
//            pills.flip(index);
        }
    }

    // There is a pill here!
    public void observe(int index, boolean pillThere) {
        pills.set(index, pillThere);
    }

    public int getPillsEaten() {
        return pillsEaten;
    }

    public double getPillsFraction(){
        return pillsEaten / totalPills;
    }

    public PillModel copy() {
        PillModel other = new PillModel((int)this.totalPills);
        other.pills = (BitSet) this.pills.clone();
        other.totalPills = this.totalPills;
        other.pillsEaten = pillsEaten;
        return other;
    }
}
