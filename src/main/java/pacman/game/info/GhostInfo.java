package pacman.game.info;

import static pacman.game.Constants.*;

/**
 * Represents the information needed to create a Ghost
 *
 * Created by pwillic on 06/05/2016.
 */
public class GhostInfo {
    private int currentNodeIndex, edibleTime, lairTime;
    private MOVE lastMoveMade;

    public int getCurrentNodeIndex() {
        return currentNodeIndex;
    }

    public void setCurrentNodeIndex(int currentNodeIndex) {
        this.currentNodeIndex = currentNodeIndex;
    }

    public int getEdibleTime() {
        return edibleTime;
    }

    public void setEdibleTime(int edibleTime) {
        this.edibleTime = edibleTime;
    }

    public int getLairTime() {
        return lairTime;
    }

    public void setLairTime(int lairTime) {
        this.lairTime = lairTime;
    }

    public MOVE getLastMoveMade() {
        return lastMoveMade;
    }

    public void setLastMoveMade(MOVE lastMoveMade) {
        this.lastMoveMade = lastMoveMade;
    }
}
