import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
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
	
	private String labelFormat;

	public Chart(String chartTitle, String yAxisLabel, String[] dataSetLabels, int chartWidth, int chartHeight, double minY, double maxY, boolean showPointValues, int numDecimalPlaces, boolean showLegend) {
		super(chartTitle);

		dataCollection = new ArrayList<XYSeriesCollection>();


		XYSeriesCollection dataset = createDataset(chartTitle);

		JFreeChart chart = createChart(dataset, chartTitle, yAxisLabel, minY, maxY, showLegend);

		labelFormat = "%." + numDecimalPlaces + "f";

		for(int i = 0; i < dataSetLabels.length; i++){
			createAdditionalDataset(dataSetLabels[i], showPointValues);
		}


		ChartPanel panel = (ChartPanel) new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);

		panel.setPreferredSize(new java.awt.Dimension(chartWidth, chartHeight));

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

	public void createAdditionalDataset(String setTitle, boolean showDataLabels) {
		dataCollection.add(createDataset(setTitle));
		plot.setDataset(dataCollection.size() - 1, dataCollection.get(dataCollection.size() - 1));

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		if(showDataLabels){
			XYItemLabelGenerator itemLabelGenerator = new CustomItemLabelGenerator();
			renderer.setBaseItemLabelGenerator(itemLabelGenerator);
			renderer.setSeriesItemLabelsVisible(0, true);
		}
		
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);
		renderer.setDrawSeriesLineAsPath(true);
		plot.setRenderer(dataCollection.size() - 1, renderer);
	}
	
	class CustomItemLabelGenerator extends StandardXYItemLabelGenerator{

		private static final long serialVersionUID = 4826373198488610515L;
		
		@Override
		public String generateLabel(XYDataset arg0, int arg1, int arg2){
			double value = arg0.getYValue(arg1, arg2);
			String result = String.format(labelFormat, value);
			return result;
		}
		
	}



	public void addData(int datasetIndex, double x, double y){
		XYSeries series = dataCollection.get(datasetIndex).getSeries(0);
		series.add(x, y);
	}


	private JFreeChart createChart(XYDataset dataset, String title, String yAxisLabel, double minY, double maxY, boolean showLegend) {

		JFreeChart chart = ChartFactory.createXYLineChart(
				title,  // title
				"Elapsed time(s)",             // x-axis label
				yAxisLabel,   // y-axis label
				dataset,
				PlotOrientation.VERTICAL,
				showLegend,
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

		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(new Range(minY, maxY));
		return chart;

	}




}
