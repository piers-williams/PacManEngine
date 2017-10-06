package pacman.controllers;

import com.fossgalaxy.object.annotations.ObjectDefStatic;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.EnumMap;

/**
 * Created by Piers on 11/11/2015.
 * <p>
 * Makes the game PO for each controller
 */
public class MASController extends Controller<EnumMap<GHOST, MOVE>> {

    private final boolean po;
    protected EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
    private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

    public MASController(boolean po, EnumMap<GHOST, IndividualGhostController> controllers) {
        this.po = po;
        this.controllers = controllers;
    }

    public MASController(EnumMap<GHOST, IndividualGhostController> controllers) {
        this(true, controllers);
    }

    @ObjectDefStatic("MASController")
    public static MASController masControllerFactory(boolean po, IndividualGhostController blinky, IndividualGhostController inky, IndividualGhostController pinky, IndividualGhostController sue) {
        EnumMap<GHOST, IndividualGhostController> ghosts = new EnumMap<>(GHOST.class);
        ghosts.put(GHOST.BLINKY, blinky);
        ghosts.put(GHOST.INKY, inky);
        ghosts.put(GHOST.PINKY, pinky);
        ghosts.put(GHOST.SUE, sue);
        return new MASController(po, ghosts);
    }

    @Override
    public final EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        for (GHOST ghost : GHOST.values()) {
            myMoves.put(
                    ghost,
                    controllers.get(ghost).getMove(
                            (po) ? game.copy(ghost) : game.copy(),
                            timeDue));
        }
        return myMoves;
    }

    /**
     * This is a shallow copy used to alter the PO status to force it to a desired value
     *
     * @param po Should the copy enforce PO on the ghosts
     * @return The copy created
     */
    public final MASController copy(boolean po) {
        MASController copy = new MASController(po, controllers);
        copy.setName(this.getName());
        return copy;
    }
}
