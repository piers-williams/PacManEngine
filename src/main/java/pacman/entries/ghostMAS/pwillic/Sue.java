package pacman.entries.ghostMAS.pwillic;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Sue extends IndividualGhostController {

    private POCommGhost poCommGhost;

    public Sue() {
        super(Constants.GHOST.SUE);
        poCommGhost = new POCommGhost(Constants.GHOST.SUE, 50);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return poCommGhost.getMove(game, timeDue);
    }
}
