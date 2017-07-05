package pacman.controllers.examples.po;

import com.fossgalaxy.object.annotations.ObjectDef;
import pacman.controllers.Controller;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.*;

/**
 * Created by Piers on 15/02/2016.
 */
public class POGhosts extends MASController {

    @ObjectDef("POG")
    public POGhosts() {
        super(true, new EnumMap<GHOST, IndividualGhostController>(GHOST.class));
        controllers.put(GHOST.BLINKY, new POGhost(GHOST.BLINKY));
        controllers.put(GHOST.INKY, new POGhost(GHOST.INKY));
        controllers.put(GHOST.PINKY, new POGhost(GHOST.PINKY));
        controllers.put(GHOST.SUE, new POGhost(GHOST.SUE));
    }

    @Override
    public String getName() {
        return "POG";
    }
}

