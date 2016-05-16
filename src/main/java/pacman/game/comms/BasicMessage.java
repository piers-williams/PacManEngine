package pacman.game.comms;


import pacman.game.Constants.GHOST;

/**
 * Represents a message for the game.
 *
 * Very inflexible - only allowed information will be possible.
 */
public final class BasicMessage implements Message {
    private final GHOST sender;
    private final GHOST recipient;
    private final MessageType type;
    private final int data;
    private final int tick;

    /**
     * Message for Multi-Agent Ghost Team
     *
     * @param sender    The individual ghost that sent this
     * @param recipient The individual ghost that this will be delivered to. if null will be delivered to all
     *                  ghosts except @see{sender}
     * @param type      The message type
     * @param data      The data packet of the message
     * @param tick      The tick the packet was created
     */
    public BasicMessage(GHOST sender, GHOST recipient, MessageType type, int data, int tick) {
        this.sender = sender;
        this.recipient = recipient;
        this.type = type;
        this.data = data;
        this.tick = tick;
    }

    public GHOST getSender() {
        return sender;
    }

    public GHOST getRecipient() {
        return recipient;
    }

    public int getData() {
        return data;
    }

    public MessageType getType() {
        return type;
    }

    public int getTick() {
        return tick;
    }

}
