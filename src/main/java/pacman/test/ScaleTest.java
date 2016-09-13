package pacman.test;

import pacman.game.Game;
import pacman.game.GameView;

/**
 * Created by newowner on 13/09/2016.
 */
public class ScaleTest {

    public static void main(String[] args) {
        Game game = new Game(0);

        GameView view = new GameView(game);
        view.setScaleFactor(2.5);

        view.showGame();
    }
}
