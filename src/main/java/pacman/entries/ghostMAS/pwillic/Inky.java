package pacman.entries.ghostMAS.pwillic;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Inky extends IndividualGhostController {

    private POCommGhost ghost;

    public Inky(MASController controller) {
        super(Constants.GHOST.INKY, controller);ghost = new POCommGhost(Constants.GHOST.INKY, 50);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return null;
    }
}
