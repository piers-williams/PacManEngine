package pacman.game.info;

import pacman.game.Constants;
import pacman.game.internal.Ghost;
import pacman.game.internal.PacMan;

import java.util.BitSet;
import java.util.EnumMap;

/**
 * Stores the information needed to populate an empty game into at least a partially full game.
 *
 * Missing info about pills will be: left out of the simulation
 * Missing info about ghosts will be: left out of the simulation
 *
 * So if you don't say it is there, it wont be anywhere at all.
 *
 * An empty game made from a game will contain everything but:
 * locations of pills of any sort
 * Locations of PacMan or ghosts of any sort
 * The messenger
 *
 */
public class GameInfo {

    private BitSet pills;
    private BitSet powerPills;
    private PacMan pacman;

    private EnumMap<Constants.GHOST, Ghost> ghosts;

    public GameInfo(int pillsLength){
        ghosts = new EnumMap<Constants.GHOST, Ghost>(Constants.GHOST.class);
        pills = new BitSet(pillsLength);
        powerPills = new BitSet(4);
    }

    public void setPillAtIndex(int index, boolean value){
        pills.set(index, value);
    }

    public void setPowerPillAtIndex(int index, boolean value){
        powerPills.set(index, value);
    }

    public void setGhostIndex(Constants.GHOST ghost, Ghost data){
        ghosts.put(ghost, data);
    }

    public BitSet getPills() {
        return pills;
    }

    public BitSet getPowerPills() {
        return powerPills;
    }

    public EnumMap<Constants.GHOST, Ghost> getGhosts() {
        return ghosts;
    }

    public PacMan getPacman() {
        return pacman;
    }

    public void setPacman(PacMan pacman) {
        this.pacman = pacman;
    }
}
