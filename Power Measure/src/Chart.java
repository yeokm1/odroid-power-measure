import java.awt.Color;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;


public class Chart extends ApplicationFrame{


	private static final long serialVersionUID = -244453266060112290L;
	private TimeSeries dataSeries;
	
	
	public Chart(String chartTitle) {
		super(chartTitle);
		
        dataSeries = new TimeSeries(chartTitle);
        dataSeries.setMaximumItemCount(10);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(dataSeries);

	
        JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel panel = (ChartPanel) new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
		
		
		
		panel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(panel);
	}


	public void addData(double newValue){
		 Second current = new Second();
		 dataSeries.add(current, newValue);  
	}
	

//	static {
//		// set a theme using the new shadow generator feature available in
//		// 1.0.14 - for backwards compatibility it is not enabled by default
//		ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
//	}
	
	 private JFreeChart createChart(XYDataset dataset, String title) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,  // title
            "Seconds Elapsed",             // x-axis label
            "Freq",   // y-axis label
            dataset,            // data
            true,               // create legend?
            false,               // generate tooltips?
            false               // generate URLs?
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

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("ss"));
        axis.setAutoRange(true);

        return chart;

    }


	

}
