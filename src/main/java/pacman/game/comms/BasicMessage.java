package pacman.game.comms;

import pacman.game.Constants.GHOST;

import java.util.Arrays;

/**
 * Represents a message for the game.
 * <p>
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

    public static BasicMessage fromString(String line, String separator) {
        String[] parts = line.split(separator);
//        System.out.println(line);
//        System.out.println(Arrays.toString(parts));
        return new BasicMessage(
                GHOST.valueOf(parts[1]),
                (parts[2].equals("NULL")) ? null : GHOST.valueOf(parts[2]),
                (parts[3].equals("NULL")) ? null : MessageType.valueOf(parts[3]), //TODO are you sure that /messagetype/ can be null?
                Integer.parseInt(parts[4]),
                Integer.parseInt(parts[5])
        );
    }

    @Override
    public GHOST getSender() {
        return sender;
    }

    @Override
    public GHOST getRecipient() {
        return recipient;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public int getData() {
        return data;
    }

    @Override
    public int getTick() {
        return tick;
    }

    @Override
    public String stringRepresentation(String separator) {
        return "Message" + separator
                + sender.name() + separator
                + ((recipient == null) ? "NULL" : recipient.name()) + separator
                + type.name() + separator
                + data + separator
                + tick;
    }

}
