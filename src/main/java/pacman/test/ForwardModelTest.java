package pacman.test;

import pacman.game.Game;
import pacman.game.comms.BasicMessenger;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.PacMan;

import java.util.EnumMap;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * Created by pwillic on 09/05/2016.
 */
public class ForwardModelTest {

    public static void main(String[] args) {

        Game game = new Game(System.currentTimeMillis(), new BasicMessenger(0, 1, 1));

        game.copy(Game.PACMAN);

        GameInfo info = game.getBlankGameInfo();
        // Just forward the game itself

        info.setPacman(new PacMan(game.getPacmanCurrentNodeIndex(), MOVE.DOWN, game.getPacmanNumberOfLivesRemaining(), true));
        info.setGhost(GHOST.INKY, new Ghost(GHOST.INKY, 10, 0, 0, MOVE.NEUTRAL));

//        info.fixGhosts((x) -> new Ghost(x, 10, 0, 0, MOVE.NEUTRAL));

        Game next = game.getGameFromInfo(info);
        for (int i = 0; i < 100; i++) {
            EnumMap<GHOST, MOVE> inkyMove = new EnumMap<>(GHOST.class);
            inkyMove.put(GHOST.INKY, MOVE.LEFT);
            next.advanceGame(MOVE.DOWN, inkyMove);
            System.out.println(next.getGhostCurrentNodeIndex(GHOST.INKY));
        }

        System.out.println("Finished");

    }
}
