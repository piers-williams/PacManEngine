package pacman.test;

import pacman.Executor;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.controllers.examples.po.POCommGhosts;

/**
 * Created by Piers on 22/06/2016.
 */
public class HumanTest {

    public static void main(String[] args) {
        Executor executor = new Executor(true, true);

        KeyBoardInput input = new KeyBoardInput();

        executor.runGame(new HumanController(input), new POCommGhosts(50), true, 40);

        System.out.println("Ended");
    }
}
