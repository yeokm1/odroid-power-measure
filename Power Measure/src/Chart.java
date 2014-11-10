import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;


public class Chart extends ApplicationFrame{


	private static final int MAX_DATA_COUNT = 20;
	private static final long serialVersionUID = -244453266060112290L;
	private XYSeries dataSeries;
	
	
	public Chart(String chartTitle, String yAxisLabel, double minY, double maxY) {
		super(chartTitle);
		
        dataSeries = new XYSeries(chartTitle);
        dataSeries.setMaximumItemCount(MAX_DATA_COUNT);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(dataSeries);

        //Fill graph with minimum data to ensure the x-axis scales fully
        for(int i = -MAX_DATA_COUNT; i <= 0; i++){
        	addData(i, minY);
        }
	
        JFreeChart chart = createChart(dataset, chartTitle, yAxisLabel, minY, maxY);
		ChartPanel panel = (ChartPanel) new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
		
		
		
		panel.setPreferredSize(new java.awt.Dimension(300, 250));
		setContentPane(panel);
	}


	public void addData(double x, double y){
		 dataSeries.add(x, y);
	}
	
	
	 private JFreeChart createChart(XYDataset dataset, String title, String yAxisLabel, double minY, double maxY) {

        JFreeChart chart = ChartFactory.createXYLineChart(
            title,  // title
            "Elapsed time(s)",             // x-axis label
            yAxisLabel,   // y-axis label
            dataset,
            PlotOrientation.VERTICAL,
            false,
            false,
            false
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);


        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setRange(new Range(minY, maxY));
        return chart;

    }


	

}
