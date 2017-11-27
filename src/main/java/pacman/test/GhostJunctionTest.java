package pacman.test;

import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.Node;

import java.util.EnumMap;
import java.util.HashMap;

public class GhostJunctionTest {

    public static void main(String[] args) {
        Game game = new Game(0L);

        Node[] graph = game.getCurrentMaze().graph;
        // Find a junction with 4 neighbours

        System.out.println("Junction at: " + 165);
        System.out.println(graph[165].neighbourhood);

        // Hack game so a ghost is present at the UP having gone DOWN

        for (int i = 0; i < 10; i++) {
            GameInfo info = game.getBlankGameInfo();
            info.fixGhosts(g -> new Ghost(
                    g,
                    ((g == Constants.GHOST.INKY) ? 140 : game.getGhostInitialNodeIndex()),
                    0,
                    0,
                    ((g == Constants.GHOST.INKY) ? Constants.MOVE.DOWN : Constants.MOVE.NEUTRAL)));
            Game copy = game.getGameFromInfo(info);
            copy.updateGhosts(new HashMap<>());
//            System.out.println("Expecting: " + 165+ " Got: " + copy.getGhostCurrentNodeIndex(Constants.GHOST.INKY));
            copy.updateGhosts(new HashMap<>());
            System.out.println("Expecting: " + 247 + " Got: " + copy.getGhostCurrentNodeIndex(Constants.GHOST.INKY));
        }

        int node = 153;
        System.out.println("Junction at: " + node);
        System.out.println(graph[node].neighbourhood);
        // Can go right, left and down. Came from RIGHT, submit move for UP which isn't allowed
        EnumMap<Constants.GHOST, Constants.MOVE> moves = new EnumMap<>(Constants.GHOST.class);
        moves.put(Constants.GHOST.INKY, Constants.MOVE.NEUTRAL);
        for(int i = 0; i < 10; i++){
            GameInfo info = game.getBlankGameInfo();
            info.fixGhosts(g -> new Ghost(
                    g,
                    ((g == Constants.GHOST.INKY) ? node : game.getGhostInitialNodeIndex()),
                    0,
                    0,
                    ((g == Constants.GHOST.INKY) ? Constants.MOVE.RIGHT : Constants.MOVE.NEUTRAL)));
            Game copy = game.getGameFromInfo(info);
//            System.out.println("Expecting: " + 165+ " Got: " + copy.getGhostCurrentNodeIndex(Constants.GHOST.INKY));
            copy.updateGhosts(moves);
            System.out.println("Expecting: " + 154 + " Got: " + copy.getGhostCurrentNodeIndex(Constants.GHOST.INKY));
        }

    }
}
