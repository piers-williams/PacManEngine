package pacman.entries.ghostMAS;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Sue extends IndividualGhostController {

    public Sue() {
        super(Constants.GHOST.SUE);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return null;
    }
}
