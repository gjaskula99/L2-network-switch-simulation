package plot;

import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XYLineChart {
	public XYLineChart(Vector<Double> xData, Vector<Double> yData, String chartTitle, String xTitle, String yTitle)
	{
		XYSeriesCollection result = new XYSeriesCollection();
	    XYSeries series = new XYSeries(chartTitle);
	    for (int i = 0; i < xData.size(); i++) {
	        double x = xData.get(i);
	        double y = yData.get(i);
	        series.add(x, y);
	    }
	    result.addSeries(series);

        // create a chart...
        JFreeChart chart = ChartFactory.createScatterPlot(
            chartTitle, // chart title
            xTitle, // x axis label
            yTitle, // y axis label
            result, // data  ***-----PROBLEM------***
            PlotOrientation.VERTICAL,
            true, // include legend
            true, // tooltips
            false // urls
            );

        // create and display a frame...
        ChartFrame frame = new ChartFrame(chartTitle, chart);
        frame.pack();
        frame.setVisible(true);
	}
}
