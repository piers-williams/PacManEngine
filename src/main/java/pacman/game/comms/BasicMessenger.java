package pacman.game.comms;

import pacman.game.Constants;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by pwillic on 20/11/2015.
 * <p>
 * Allows for the creation of messages that will be pollable by other agents when the messages are ready
 */
public class BasicMessenger implements Messenger {
    protected Map<Integer, ArrayList<Message>> messages;
    protected int currentTick;
    protected int delayConstant;
    protected int delayMultiplier;

    public BasicMessenger() {
        this(0, 1, 1);
    }

    public BasicMessenger(int currentTick, int delayConstant, int delayMultiplier) {
        messages = new TreeMap<>();
        this.currentTick = currentTick;
        this.delayConstant = delayConstant;
        this.delayMultiplier = delayMultiplier;
    }

    @Override
    public BasicMessenger copy() {
        BasicMessenger result = new BasicMessenger(this.currentTick, this.delayConstant, this.delayMultiplier);
        for (Integer key : messages.keySet()) {
            result.messages.put(key, (ArrayList<Message>) this.messages.get(key).clone());
        }
        return result;
    }

    @Override
    public void update() {
        // Ditch messages due to be delivered this tick before moving on
        if (messages.containsKey(currentTick)) {
            messages.remove(currentTick);
            //            System.out.println("Removed list: " + currentTick);
        }
        currentTick++;
        //        System.out.println("Messenger updated");
    }

    @Override
    public void addMessage(Message message) {
        int tickToDeliver = currentTick + delayConstant + (delayMultiplier * message.getType().getDelay());
        if (!messages.containsKey(tickToDeliver)) {
            //            System.out.println("Adding new list" + tickToDeliver);
            messages.put(tickToDeliver, new ArrayList<Message>());
        }
        messages.get(tickToDeliver).add(message);
    }

    @Override
    public ArrayList<Message> getMessages(Constants.GHOST querier) {
        ArrayList<Message> results = new ArrayList<>();
        if (!messages.containsKey(currentTick)) {
            //            System.out.println("Wasn't anything here: " + currentTick);
            return results;
        }
        //        System.out.println("Size: " + messages.get(currentTick));
        for (Message message : messages.get(currentTick)) {
            //            System.out.println("Message!");
            if (!message.getSender().equals(querier)) {
                if (message.getRecipient() == null) {
                    //                    System.out.println("Broadcast received I didn't send");
                    results.add(message);
                } else if (message.getRecipient().equals(querier)) {
                    //                    System.out.println("Sent straight to me");
                    results.add(message);
                }

            }
        }

        return results;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public int getDelayConstant() {
        return delayConstant;
    }

    public int getDelayMultiplier() {
        return delayMultiplier;
    }
}
