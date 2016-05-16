package pacman.entries.ghostMAS;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Pinky extends IndividualGhostController {

    public Pinky(MASController controller) {
        super(Constants.GHOST.PINKY, controller);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return null;
    }
}
