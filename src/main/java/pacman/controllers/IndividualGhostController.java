package pacman.controllers;

import pacman.game.Constants.*;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public abstract class IndividualGhostController {

    protected final GHOST ghost;
    protected final MASController controller;

    public IndividualGhostController(GHOST ghost, MASController controller) {
        this.ghost = ghost;
        this.controller = controller;
    }

    public abstract MOVE getMove(Game game, long timeDue);
}
