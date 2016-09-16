package pacman.test;

import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.controllers.examples.po.POPacMan;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;

import java.util.EnumMap;

/**
 * Created by piers on 12/09/16.
 */
public class GhostViewTest {

    /**
     * Just a test that shows 5 views, one for pacman !po, one each for the ghosts po
     *
     * @param args Not used
     */
    public static void main(String[] args) {
        Game primaryGame = new Game(0);

        GameView primaryView = new GameView(primaryGame).showGame();
        GameView[] ghostViews = new GameView[]{
                new GameView(primaryGame, false).showGame(),
                new GameView(primaryGame, false).showGame(),
                new GameView(primaryGame, false).showGame(),
                new GameView(primaryGame, false).showGame()
        };

        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            ghostViews[ghost.ordinal()].setPO(true, ghost);
        }

        PacmanController pacman = new POPacMan();
        POCommGhosts ghosts = new POCommGhosts(50);

        while (!primaryGame.gameOver()) {
            try {
                Thread.sleep(40);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Constants.MOVE pacmanMove = pacman.getMove(primaryGame.copy(5), 40);
            EnumMap<Constants.GHOST, Constants.MOVE> ghostMoves = ghosts.getMove(primaryGame.copy(), -1);

            primaryGame.advanceGame(pacmanMove, ghostMoves);

            primaryView.paintImmediately(primaryView.getBounds());

            for (GameView view : ghostViews) {
                view.paintImmediately(view.getBounds());
            }
        }
    }

}
