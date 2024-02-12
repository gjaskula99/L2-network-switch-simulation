package plot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Vector;

import javax.swing.JPanel;

public class Chart extends JPanel {
    Vector<Double> cord;
    int marg = 20;
    
    public Chart(Vector<Double> x, Vector<Double> y)
    {
    	cord = x;
    	//TD y;
    }
    
    protected void paintComponent(Graphics grf) {
        super.paintComponent(grf);
        Graphics2D graph = (Graphics2D) grf;
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        graph.draw(new Line2D.Double(marg, marg, marg, height - marg));
        graph.draw(new Line2D.Double(marg, height - marg, width - marg, height - marg));

        double x = (double) (width - 2 * marg) / (cord.size() - 1);
        double scale = (double) (height - 2 * marg) / getMax();

        graph.setPaint(Color.RED);

        for (int i = 0; i < cord.size(); i++) {
            double x1 = marg + i * x;
            double y1 = height - marg - scale * cord.get(i);
            graph.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
        }
    }

    private double getMax() {
        Double max = -Double.MAX_VALUE;
        for (int i = 0; i < cord.size(); i++) {
            if (cord.get(1) > max)
                max = cord.get(i); //UNSAFE
        }
        return max;
    }
}