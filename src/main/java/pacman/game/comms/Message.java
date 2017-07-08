package pacman.game.comms;

import pacman.game.Constants.GHOST;

/**
 * Represents a message for the game.
 * <p>
 * Very inflexible - only allowed information will be possible.
 */
public interface Message {

    /**
     * Gets the sender of the message
     *
     * @return The sender - any one of the four ghosts
     */
    public GHOST getSender();

    /**
     * Gets the intended recipient of the message
     * <p>
     * If it is null - will be delivered to all ghosts except sender
     *
     * @return The recipient of the message
     */
    public GHOST getRecipient();

    /**
     * Gets the message type of the message
     *
     * @return The message type
     */
    public MessageType getType();

    /**
     * Gets the data associated with the message
     *
     * @return The data
     */
    public int getData();

    /**
     * Gets the tick that the message was created - not all messages arrive the same speed
     *
     * @return The tick the message was created
     */
    public int getTick();


    public String stringRepresentation(String separator);

    /**
     * MessageType - contains information about message delays
     */
    public enum MessageType {
        PACMAN_SEEN(2),
        PACMAN_HEADING(2),
        I_AM(1),
        I_AM_HEADING(1),
        PILL_NOT_SEEN(1);

        private int delay;
        private int cost;

        private MessageType(int delay) {
            this(delay, 0);
        }

        private MessageType(int delay, int cost){
            this.delay = delay;
            this.cost = cost;
        }

        public int getDelay() {
            return delay;
        }
    }
}
