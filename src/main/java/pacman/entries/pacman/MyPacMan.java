package pacman.entries.pacman;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends PacmanController {
    private MOVE myMove = MOVE.NEUTRAL;

    @Override
    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man

        return myMove;
    }
}