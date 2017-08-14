package pacman.test;

import pacman.Executor;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.controllers.examples.po.POPacMan;

/**
 * Created by piers on 29/09/16.
 */
public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setPeek(g -> "Score: " + g.getScore())
                .build();

        executor.runExperiment(
                new POPacMan(),
                new POCommGhosts(50),
                10,
                "POP Vs POGC"
        );
    }
}
