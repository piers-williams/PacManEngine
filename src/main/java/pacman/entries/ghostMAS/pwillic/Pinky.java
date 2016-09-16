package pacman.entries.ghostMAS.pwillic;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Pinky extends IndividualGhostController {
    private POCommGhost poCommGhost;

    public Pinky() {
        super(Constants.GHOST.PINKY);
        poCommGhost = new POCommGhost(Constants.GHOST.PINKY, 50);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return poCommGhost.getMove(game, timeDue);
    }
}
