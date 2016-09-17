package pacman.test;

import pacman.game.Game;
import pacman.game.GameView;

import java.awt.*;

/**
 * Created by Piers on 17/09/2016.
 */
public class GameViewLocationTest {

    public static void main(String[] args) {
        Game game = new Game(0);

        GameView view = new GameView(game);
        view.setDesiredLocation(new Point(100, 100));

        view.showGame();
    }
}
