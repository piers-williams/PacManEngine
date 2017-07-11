package pacman.game.comms;

import pacman.game.Constants.* ;

/**
 * Created by Piers on 08/07/2017.
 *
 * This will contain utility functions to help reduce some of the general boiler plate found in communicating AI's
 *
 * All items in this class are beta and can't be trusted - ensure they work before using them
 */
public class MessageUtilities {

    /**
     * Returns the location of Ms. Pac-Man or -1 if not seen.
     * @param messenger The messenger object from the game
     * @param lastPacmanInfo The last index that Ms. Pac-Man was seen and last tick that she was seen and
     *                       the ordinal of the last move she made
     * @return Returns [lastPacmanIndex, lastTickSeen, lastMove] if there are no messages giving the location
     *          or otherwise returns latest information in the same format
     */
    public static int[] hasPacmanBeenSeen(Messenger messenger, GHOST ghost, int[] lastPacmanInfo){
        if(lastPacmanInfo == null) return null;
        if(lastPacmanInfo.length != 3) return lastPacmanInfo;
        for(Message message : messenger.getMessages(ghost)){
            if(message.getType() == BasicMessage.MessageType.PACMAN_SEEN){
                if(message.getTick() > lastPacmanInfo[1]){
                    lastPacmanInfo[0] = message.getData();
                    lastPacmanInfo[1] = message.getTick();
                }
            }else if(message.getType() == BasicMessage.MessageType.PACMAN_HEADING){
                if(message.getTick() > lastPacmanInfo[1]) {
                    lastPacmanInfo[2] = message.getData();
                }
            }
        }

        return lastPacmanInfo;
    }

    public static int pacmanIndex(int[] lastPacmanInfo){
        return lastPacmanInfo[0];
    }

    public static int pacmanTick(int[] lastPacmanInfo){
        return lastPacmanInfo[1];
    }

    public static MOVE pacmanLastMove(int[] lastPacmanInfo){
        return (lastPacmanInfo[2] == -1) ? null : MOVE.values()[lastPacmanInfo[2]];
    }
}
