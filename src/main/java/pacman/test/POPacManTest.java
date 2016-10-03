package pacman.test;

import pacman.controllers.examples.po.POCommGhosts;
import pacman.controllers.examples.po.POPacMan;
import pacman.game.Game;
import pacman.game.GameView;

/**
 * Created by piers on 03/10/16.
 */
public class POPacManTest {

    public static void main(String[] args) throws InterruptedException {
        Game game = new Game(0);

        GameView view = new GameView(game);

        POPacMan pacman = new POPacMan();
        POCommGhosts ghosts = new POCommGhosts(50);

        view.showGame();
        while(!game.gameOver()){
            Thread.sleep(40);
            game.advanceGame(pacman.getMove(game.copy(5), 40), ghosts.getMove(game.copy(), 40));
            view.repaint();
        }
    }
}
