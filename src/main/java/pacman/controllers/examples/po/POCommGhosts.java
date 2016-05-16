package pacman.controllers.examples.po;

import pacman.controllers.Controller;
import pacman.game.Constants.*;
import pacman.game.Game;
import pacman.game.comms.BasicMessage;
import pacman.game.comms.Message;
import pacman.game.comms.Messenger;

import java.util.EnumMap;
import java.util.Random;

/**
 * Created by pwillic on 25/02/2016.
 */
public class POCommGhosts extends Controller<EnumMap<GHOST, MOVE>> {
    private EnumMap<GHOST, POCommGhostImproved> ghosts = new EnumMap<GHOST, POCommGhostImproved>(GHOST.class);
    private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

    public POCommGhosts() {
        this(5);
    }

    public POCommGhosts(int TICK_THRESHOLD){
        ghosts.put(GHOST.BLINKY, new POCommGhostImproved(GHOST.BLINKY, TICK_THRESHOLD));
        ghosts.put(GHOST.INKY, new POCommGhostImproved(GHOST.INKY, TICK_THRESHOLD));
        ghosts.put(GHOST.PINKY, new POCommGhostImproved(GHOST.PINKY, TICK_THRESHOLD));
        ghosts.put(GHOST.SUE, new POCommGhostImproved(GHOST.SUE, TICK_THRESHOLD));
    }

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();
        for (GHOST ghost : ghosts.keySet()) {
            MOVE move = ghosts.get(ghost).getMove(game.copy(ghost), timeDue);
            if (move != null) {
                myMoves.put(ghost, move);
            }
        }
        return myMoves;
    }
}

class POCommGhostImproved {
    private final static float CONSISTENCY = 0.9f;    //attack Ms Pac-Man with this probability
    private final static int PILL_PROXIMITY = 15;        //if Ms Pac-Man is this close to a power pill, back away
    private int TICK_THRESHOLD;

    private GHOST ghost;
    Random rnd = new Random();

    private int lastPacmanIndex = -1;
    private int tickSeen = -1;

    public POCommGhostImproved(GHOST ghost) {
        this(ghost, 5);
    }

    public POCommGhostImproved(GHOST ghost, int TICK_THRESHOLD){
        this.ghost = ghost;
        this.TICK_THRESHOLD = TICK_THRESHOLD;
    }

    public MOVE getMove(Game game, long timeDue) {
        // Housekeeping - throw out old info
        int currentTick = game.getCurrentLevelTime();
        if (currentTick <= 2 || currentTick - tickSeen >= TICK_THRESHOLD) {
            lastPacmanIndex = -1;
            tickSeen = -1;
        }


        // Can we see PacMan? If so tell people and update our info
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        Messenger messenger = game.getMessenger();
        if (pacmanIndex != -1) {
            lastPacmanIndex = pacmanIndex;
            tickSeen = game.getCurrentLevelTime();
            if (messenger != null) {
                messenger.addMessage(new BasicMessage(ghost, null, BasicMessage.MessageType.PACMAN_SEEN, pacmanIndex, game.getCurrentLevelTime()));
            }
        }

        // Has anybody else seen PacMan if we haven't?
        if (pacmanIndex == -1 && game.getMessenger() != null) {
            for (Message message : messenger.getMessages(ghost)) {
                if (message.getType() == BasicMessage.MessageType.PACMAN_SEEN) {
                    if (message.getTick() > tickSeen && message.getTick() < currentTick) { // Only if it is newer information
                        lastPacmanIndex = message.getData();
                        tickSeen = message.getTick();
                    }
                }
            }
        }
        if (pacmanIndex == -1) pacmanIndex = lastPacmanIndex;

        if (game.doesGhostRequireAction(ghost))        //if ghost requires an action
        {
            if (pacmanIndex != -1) {
                if (game.getGhostEdibleTime(ghost) > 0 || closeToPower(game))    //retreat from Ms Pac-Man if edible or if Ms Pac-Man is close to power pill
                    try {
                        return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
                                game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.println(e);
                        System.out.println(pacmanIndex + " : " + currentIndex);
                    }
                else {
                    if (rnd.nextFloat() < CONSISTENCY) {            //attack Ms Pac-Man otherwise (with certain probability)
                        try {
                            MOVE move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
                                    pacmanIndex, game.getGhostLastMoveMade(ghost), DM.PATH);
                            return move;
                        }catch(ArrayIndexOutOfBoundsException e){
                            System.out.println(e);
                            System.out.println(pacmanIndex + " : " + currentIndex);
                        }
                    }
                }
            } else {
                MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
                return possibleMoves[rnd.nextInt(possibleMoves.length)];
            }
        }
        return null;
    }

    //This helper function checks if Ms Pac-Man is close to an available power pill
    private boolean closeToPower(Game game) {
        int[] powerPills = game.getPowerPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
            if (pacmanNodeIndex == -1) pacmanNodeIndex = lastPacmanIndex;
            if (powerPillStillAvailable == null || pacmanNodeIndex == -1) return false;
            if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < PILL_PROXIMITY) {
                return true;
            }
        }

        return false;
    }
}

class POCommGhost {
    private final static float CONSISTENCY = 0.9f;    //attack Ms Pac-Man with this probability
    private final static int PILL_PROXIMITY = 15;        //if Ms Pac-Man is this close to a power pill, back away

    private GHOST ghost;
    Random rnd = new Random();

    public POCommGhost(GHOST ghost) {
        this.ghost = ghost;
    }

    public MOVE getMove(Game game, long timeDue) {

        // Can we see PacMan?
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        Messenger messenger = game.getMessenger();
        if (pacmanIndex != -1 && messenger != null) {
            messenger.addMessage(new BasicMessage(ghost, null, BasicMessage.MessageType.PACMAN_SEEN, pacmanIndex, game.getCurrentLevelTime()));
        }

        // Has anybody else seen PacMan if we haven't?
        if (pacmanIndex == -1 && game.getMessenger() != null) {
            for (Message message : messenger.getMessages(ghost)) {
                if (message.getType() == BasicMessage.MessageType.PACMAN_SEEN) {
                    pacmanIndex = message.getData();
                }
            }
        }

        if (game.doesGhostRequireAction(ghost))        //if ghost requires an action
        {
            if (game.getGhostEdibleTime(ghost) > 0 || closeToPower(game))    //retreat from Ms Pac-Man if edible or if Ms Pac-Man is close to power pill
                return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
                        game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
            else {
                // If can observe pacman and random says ok
//                if(rnd.nextFloat() < CONSISTENCY) {

                if (pacmanIndex != -1 && rnd.nextFloat() < CONSISTENCY) {            //attack Ms Pac-Man otherwise (with certain probability)
                    MOVE move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
                            pacmanIndex, game.getGhostLastMoveMade(ghost), DM.PATH);
//                    if(game.getPacmanCurrentNodeIndex() == -1) System.out.println("Was -1: " + move);
                    return move;
                } else                                    //else take a random legal action (to be less predictable)
                {
                    MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
                    return possibleMoves[rnd.nextInt(possibleMoves.length)];
                }
            }
        }

        return null;
    }

    //This helper function checks if Ms Pac-Man is close to an available power pill
    private boolean closeToPower(Game game) {
        int[] powerPills = game.getPowerPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();

            if (powerPillStillAvailable == null || pacmanNodeIndex == -1) return false;
            if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < PILL_PROXIMITY) {
                return true;
            }
        }

        return false;
    }
}