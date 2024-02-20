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

    public PlotWindow(Vector<Double> x, Vector<Double> y, String chartTitle) {
        plotWindow = new JFrame("Plot");
        plotWindow.setLocationRelativeTo(null);
        plotWindow.setResizable(false);
        plotWindow.setTitle(chartTitle);
        plotWindow.setSize(600, 600);
        
        //Chart window
        Chart chart = new Chart(x ,y);
        chart.setBounds(10, 10, 550, 490);
        
        //Axes description
        JLabel xLabel = new JLabel("X");
        JLabel yLabel = new JLabel("Y");
        xLabel.setBounds(290, 520, 20, 20);
        yLabel.setBounds(70, 290, 20, 20);
        
        plotWindow.add(chart);
        plotWindow.add(xLabel);
        plotWindow.add(yLabel);
        
        plotWindow.setVisible(true);
    }
}
