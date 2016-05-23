package pacman.entries.ghostMAS.pwillic;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Sue extends IndividualGhostController {

    private POCommGhost ghost;

    public Sue() {
        super(Constants.GHOST.SUE);ghost = new POCommGhost(Constants.GHOST.SUE, 50);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return ghost.getMove(game, timeDue);
    }
}
