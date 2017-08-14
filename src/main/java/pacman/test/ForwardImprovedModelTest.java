package pacman.test;

import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.comms.BasicMessenger;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.PacMan;

import java.util.EnumMap;

/**
 * Created by piers on 22/02/17.
 */
public class ForwardImprovedModelTest {

    public static void main(String[] args) {

        Game game = new Game(System.currentTimeMillis(), new BasicMessenger(0, 1, 1));

        game.copy(Game.PACMAN);

        GameInfo info = game.getPopulatedGameInfo();
        // Just forward the game itself

        info.fixGhosts(ForwardImprovedModelTest::defaultGhost);

        Game next = game.getGameFromInfo(info);
        for (int i = 0; i < 100; i++) {
            EnumMap<Constants.GHOST, Constants.MOVE> inkyMove = new EnumMap<>(Constants.GHOST.class);
            inkyMove.put(Constants.GHOST.INKY, Constants.MOVE.LEFT);
            next.advanceGame(Constants.MOVE.DOWN, inkyMove);
            System.out.println(next.getGhostCurrentNodeIndex(Constants.GHOST.INKY));
        }

        System.out.println("Finished");

    }

    private static Ghost defaultGhost(Constants.GHOST ghost){
        return new Ghost(ghost, 10, 0, 0, Constants.MOVE.NEUTRAL);
    }

}
