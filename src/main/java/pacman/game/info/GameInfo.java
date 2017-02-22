package pacman.game.info;

import pacman.game.Constants;
import pacman.game.internal.Ghost;
import pacman.game.internal.PacMan;

import java.util.BitSet;
import java.util.EnumMap;
import java.util.function.Function;

/**
 * Stores the information needed to populate an empty game into at least a partially full game.
 * <p>
 * Missing info about pills will be: left out of the simulation
 * Missing info about ghosts will be: left out of the simulation
 * <p>
 * So if you don't say it is there, it wont be anywhere at all.
 * <p>
 * An empty game made from a game will contain everything but:
 * locations of pills of any sort
 * Locations of PacMan or ghosts of any sort
 * The messenger
 */
public class GameInfo {

    private BitSet pills;
    private BitSet powerPills;
    private PacMan pacman;

    private EnumMap<Constants.GHOST, Ghost> ghosts;

    public GameInfo(int pillsLength) {
        ghosts = new EnumMap<>(Constants.GHOST.class);
        pills = new BitSet(pillsLength);
        powerPills = new BitSet(4);
    }

    /**
     * Sets whether there is a pill at the index provided
     *
     * @param index The index in the maze node graph
     * @param value The value about the presence of a pill
     */
    public void setPillAtIndex(int index, boolean value) {
        pills.set(index, value);
    }

    /**
     * Sets whether there is a power pill at the index provided
     *
     * @param index The index in the maze node graph
     * @param value The value about the presence of a pill
     */
    public void setPowerPillAtIndex(int index, boolean value) {
        powerPills.set(index, value);
    }

    /**
     * Sets the data about a ghost
     *
     * @param ghost The GHOST that is being set (key)
     * @param data  The Ghost that is being set (value)
     */
    public void setGhost(Constants.GHOST ghost, Ghost data) {
        ghosts.put(ghost, data);
    }

    @Deprecated
    public void setGhostIndex(Constants.GHOST ghost, Ghost data) {
        setGhost(ghost, data);
    }

    /**
     * Gets the pills data stored in this info
     *
     * @return The BitSet for the pills
     */
    public BitSet getPills() {
        return pills;
    }

    /**
     * Gets the powerpills data stored in this info
     *
     * @return The BitSet for the pills
     */
    public BitSet getPowerPills() {
        return powerPills;
    }

    /**
     * Gets the ghost data stored in this info
     *
     * @return The Ghost data
     */
    public EnumMap<Constants.GHOST, Ghost> getGhosts() {
        return ghosts;
    }

    /**
     * Gets the pd acman data stored in this info
     *
     * @return The pacman data
     */
    public PacMan getPacman() {
        return pacman;
    }

    /**
     * Sets the pacman data stored in this info
     *
     * @param pacman The input data
     */
    public void setPacman(PacMan pacman) {
        this.pacman = pacman;
    }

    public void fixGhosts(Function<Constants.GHOST, Ghost> f){
        for(Constants.GHOST ghost : Constants.GHOST.values()){
            if(!ghosts.containsKey(ghost)){
                ghosts.put(ghost, f.apply(ghost));
            }
        }
    }
}
