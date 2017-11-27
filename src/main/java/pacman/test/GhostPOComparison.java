package pacman.test;

import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.comms.BasicMessenger;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.POType;
import pacman.game.internal.PacMan;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GhostPOComparison {

    public static void main(String[] args) {
        int sightLimit = 50;
        int junction = 165;

        JFrame frame = new JFrame("PO Views");
        GridLayout layout = new GridLayout(1, 3);
        JPanel panel = new JPanel(layout);

        for(POType type : POType.values()){
            // Create game from INKY perspective
            Game game = new Game(0, 0, new BasicMessenger(), type, sightLimit).copy(Game.INKY);
            // Force location of INKY and set the pacman
            GameInfo info = game.getBlankGameInfo();
            info.setPacman(new PacMan(game.getPacManInitialNodeIndex(), Constants.MOVE.LEFT, 1, false));
            info.fixGhosts(g -> new Ghost(
                    g,
                    ((g == Constants.GHOST.INKY) ? junction : game.getGhostInitialNodeIndex()),
                    0,
                    0,
                    ((g == Constants.GHOST.INKY) ? Constants.MOVE.DOWN : Constants.MOVE.NEUTRAL)));
            Game copy = game.getGameFromInfo(info);
            GameView view = new GameView(copy);
            view.setPO(true, Constants.GHOST.INKY);
            panel.add(view);
        }

        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
