package plot;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import plot.Chart;

public class PlotWindow {
	JFrame plotWindow;

    public PlotWindow(Vector<Double> x, Vector<Double> y) {
        plotWindow = new JFrame("Plot");
        plotWindow.setLocationRelativeTo(null);
        plotWindow.setResizable(false);
        plotWindow.setSize(600, 600);
        plotWindow.setVisible(true);
        
        Chart chart = new Chart(x ,y);
        plotWindow.add(chart);
    }
}
