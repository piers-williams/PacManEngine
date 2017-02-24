package pacman.game.comms;

import pacman.game.Constants;

import java.util.ArrayList;

/**
 * The Messenger interface for use in PacMan
 */
public interface Messenger {

    /**
     * Get a deep copy of the messenger object
     *
     * @return The copy
     */
    Messenger copy();

    /**
     * Updates the messenger - should only be called once per tick
     */
    void update();

    /**
     * Adds a message to the messenger to be delivered as soon as it can be
     *
     * @param message The message to be added
     */
    void addMessage(Message message);

    /**
     * Get all messages that are due to be delivered to me this tick
     *
     * @param querier The agent doing the querying. Don't collect other peoples mail
     * @return The messages due - could be empty list.
     */
    ArrayList<Message> getMessages(Constants.GHOST querier);
}
