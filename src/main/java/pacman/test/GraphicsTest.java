package pacman.test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by pwillic on 09/05/2016.
 */
public class GraphicsTest extends JComponent {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Graphics Test");
        frame.add(new GraphicsTest());

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D other = (Graphics2D) image.getGraphics();

        other.setColor(Color.CYAN);
        other.fillRect(0, 0, 800, 600);

        BufferedImage cutout = new BufferedImage(800, 600, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D cut = (Graphics2D) image.getGraphics();

        cut.setColor(Color.BLACK);
        //        cut.fillRect(0, 0, 800, 600);
        cut.setColor(Color.WHITE);
        cut.fillRect(0, 350, 800, 100);

        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.CLEAR);
        other.setComposite(ac);
        other.drawImage(cutout, 0, 0, 800, 600, null);

        g.drawImage(image, 0, 0, 800, 600, null);
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}
