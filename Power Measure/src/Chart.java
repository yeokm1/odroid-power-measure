import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
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
	private List<XYSeriesCollection> dataCollection;
	private XYPlot plot;

	public Chart(String chartTitle, String yAxisLabel, String[] dataSetLabels, double minY, double maxY) {
		super(chartTitle);

		dataCollection = new ArrayList<XYSeriesCollection>();


		XYSeriesCollection dataset = createDataset(chartTitle);

		JFreeChart chart = createChart(dataset, chartTitle, yAxisLabel, minY, maxY);

		for(int i = 0; i < dataSetLabels.length; i++){
			createAdditionalDataset(dataSetLabels[i]);

		}


		ChartPanel panel = (ChartPanel) new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);

		panel.setPreferredSize(new java.awt.Dimension(300, 250));

		setContentPane(panel);
	}

	private static XYSeriesCollection createDataset(String name) {
		XYSeries series = new XYSeries(name);
		series.setMaximumItemCount(MAX_DATA_COUNT);
		//Fill graph with minimum data to ensure the x-axis scales fully
		for(int i = -MAX_DATA_COUNT; i <= 0; i++){
			series.add(i, 0);
		}


		return new XYSeriesCollection(series);
	}

	public void createAdditionalDataset(String setTitle) {
		dataCollection.add(createDataset(setTitle));
		plot.setDataset(dataCollection.size() - 1, dataCollection.get(dataCollection.size() - 1));

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);
		renderer.setDrawSeriesLineAsPath(true);
		plot.setRenderer(dataCollection.size() - 1, renderer);
	}



	public void addData(int datasetIndex, double x, double y){
		dataCollection.get(datasetIndex).getSeries(0).add(x, y);
	}


	private JFreeChart createChart(XYDataset dataset, String title, String yAxisLabel, double minY, double maxY) {

		JFreeChart chart = ChartFactory.createXYLineChart(
				title,  // title
				"Elapsed time(s)",             // x-axis label
				yAxisLabel,   // y-axis label
				dataset,
				PlotOrientation.VERTICAL,
				true,
				false,
				false
				);

		chart.setBackgroundPaint(Color.white);

		plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);


		//		XYItemRenderer r = plot.getRenderer();
		//		if (r instanceof XYLineAndShapeRenderer) {
		//			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		//			renderer.setBaseShapesVisible(true);
		//			renderer.setBaseShapesFilled(true);
		//			renderer.setDrawSeriesLineAsPath(true);
		//		}

		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(new Range(minY, maxY));
		return chart;

	}




}
