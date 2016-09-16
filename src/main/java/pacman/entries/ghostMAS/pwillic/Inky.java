package pacman.entries.ghostMAS.pwillic;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Inky extends IndividualGhostController {

    private POCommGhost poCommGhost;

    public Inky() {
        super(Constants.GHOST.INKY);
        poCommGhost = new POCommGhost(Constants.GHOST.INKY, 50);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return null;
    }
}
