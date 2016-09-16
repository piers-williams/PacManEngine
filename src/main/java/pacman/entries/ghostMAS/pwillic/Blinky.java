package pacman.entries.ghostMAS.pwillic;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Blinky extends IndividualGhostController {

    private POCommGhost poCommGhost;

    public Blinky() {
        super(Constants.GHOST.BLINKY);
        poCommGhost = new POCommGhost(Constants.GHOST.BLINKY, 50);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return poCommGhost.getMove(game, timeDue);
    }
}
