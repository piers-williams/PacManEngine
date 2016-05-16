package pacman.entries.ghostMAS.pwillic;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Pinky extends IndividualGhostController {
    private POCommGhost ghost;

    public Pinky(MASController controller) {
        super(Constants.GHOST.PINKY, controller);
        ghost = new POCommGhost(Constants.GHOST.PINKY, 50);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return ghost.getMove(game, timeDue);
    }
}
