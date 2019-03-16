import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DataGraph {
    private static final double ERROR = 100;

    private XYSeries quicksortTimes;
    private XYSeries heapsortTimes;
    private XYSeries quicksortOperations;
    private XYSeries heapsortOperations;

    private ArrayList<XYSeries> averageQuicksortTimes;
    private ArrayList<XYSeries> averageHeapsortTimes;
    private ArrayList<XYSeries> averageQuicksortOperations;
    private ArrayList<XYSeries> averageHeapsortOperations;

    DataGraph() {
        quicksortTimes = new XYSeries("Quicksort");
        heapsortTimes = new XYSeries("Heapsort");
        quicksortOperations = new XYSeries("Quicksort");
        heapsortOperations = new XYSeries("Heapsort");

        averageQuicksortTimes = new ArrayList<>();
        averageQuicksortTimes.add(new XYSeries("Quicksort"));

        averageHeapsortTimes = new ArrayList<>();
        averageHeapsortTimes.add(new XYSeries("Heapsort"));

        averageQuicksortOperations = new ArrayList<>();
        averageQuicksortOperations.add(new XYSeries("Quicksort"));

        averageHeapsortOperations = new ArrayList<>();
        averageHeapsortOperations.add(new XYSeries("Heapsort"));
    }

    public void addQuicksortInfo(SortInfo sortInfo) {
        quicksortTimes.add(sortInfo.getInputSize(), sortInfo.getSortTime());
        quicksortOperations.add(sortInfo.getInputSize(), sortInfo.getCntComparisons() + sortInfo.getCntExchanges());
    }

    public void addHeapsortInfo(SortInfo sortInfo) {
        heapsortTimes.add(sortInfo.getInputSize(), sortInfo.getSortTime());
        heapsortOperations.add(sortInfo.getInputSize(), sortInfo.getCntComparisons() + sortInfo.getCntExchanges());
    }

    private XYSeries getSeriesTime(AverageInfo averageInfo, double error, String seriesName) {
        XYSeries seriesTime = new XYSeries(seriesName);
        seriesTime.add(averageInfo.getInputSize() + error, averageInfo.getAverageTime() - averageInfo.getStdDevTime());
        seriesTime.add(averageInfo.getInputSize() + error, averageInfo.getAverageTime() + averageInfo.getStdDevTime());

        return seriesTime;
    }

    private XYSeries getSeriesOperations(AverageInfo averageInfo, double error, String seriesName) {
        XYSeries seriesOperations = new XYSeries(seriesName);
        seriesOperations.add(averageInfo.getInputSize() + error, averageInfo.getAverageOperations() - averageInfo.getStdDevOperations());
        seriesOperations.add(averageInfo.getInputSize() + error, averageInfo.getAverageOperations() + averageInfo.getStdDevOperations());

        return seriesOperations;
    }

    public void addAverageQuicksortInfo(AverageInfo averageInfo) {
        averageQuicksortTimes.get(0).add(averageInfo.getInputSize(), averageInfo.getAverageTime());
        averageQuicksortOperations.get(0).add(averageInfo.getInputSize(), averageInfo.getAverageOperations());

        averageQuicksortTimes.add(getSeriesTime(averageInfo, -ERROR, "Quicksort standard deviation"));
        averageQuicksortOperations.add(getSeriesOperations(averageInfo, -ERROR, "Quicksort standard deviation"));
    }

    public void addAverageHeapsortInfo(AverageInfo averageInfo) {
        averageHeapsortTimes.get(0).add(averageInfo.getInputSize(), averageInfo.getAverageTime());
        averageHeapsortOperations.get(0).add(averageInfo.getInputSize(), averageInfo.getAverageOperations());

        averageHeapsortTimes.add(getSeriesTime(averageInfo, ERROR, "Heapsort standard deviation"));
        averageHeapsortOperations.add(getSeriesOperations(averageInfo, ERROR, "Heapsort standard deviation"));
    }

    private XYSeriesCollection createDataRun(XYSeries series1, XYSeries series2) {
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series1);
        data.addSeries(series2);

        return data;
    }

    private XYSeriesCollection createDataAverage(ArrayList<XYSeries> series1, ArrayList<XYSeries> series2) {
        XYSeriesCollection data = new XYSeriesCollection();

        //series1.size() == series2.size()
        for (int i = 0; i < series1.size(); ++i) {
            data.addSeries(series1.get(i));
            data.addSeries(series2.get(i));
        }

        return data;
    }

    private JFreeChart createChart(String yAxisLabel, String subtitle, XYSeriesCollection data) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Quicksort vs Heapsort Performance",
                "Input size",
                yAxisLabel,
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chart.addSubtitle(new TextTitle(subtitle));

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);

        for (int i = 2; i <= data.getSeriesCount(); ++i) {
            renderer.setSeriesPaint(i, (i % 2 == 0) ? new Color(255,0, 188) : new Color(0, 171, 255));
            renderer.setSeriesShape(i, ShapeUtilities.createLineRegion(new Line2D.Double(), 10));
            if (i > 3) {
                renderer.setSeriesVisibleInLegend(i, false);
            }
        }

        if (data.getSeriesCount() > 2) {
            renderer.setSeriesShapesVisible(0, false);
            renderer.setSeriesShapesVisible(1, false);
        }

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        //chart.setBackgroundPaint(Color.WHITE);

        return chart;
    }

    private void plotGraph(String fileName, JFreeChart chart1, JFreeChart chart2) throws IOException {
        //Combine the two graphs and save the image.
        BufferedImage imageTimes = chart1.createBufferedImage(1000, 400);
        BufferedImage imageOperatons = chart2.createBufferedImage(1000, 400);
        BufferedImage imageGraph = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_RGB);

        Graphics g = imageGraph.getGraphics();
        g.drawImage(imageTimes, 0, 0, null);
        g.drawImage(imageOperatons, 0, 400, null);
        ImageIO.write(imageGraph, "png", new File(fileName + ".png"));
    }

    public void plotRunGraph(String subtitle, String fileName) throws IOException {
        JFreeChart chartTimes = createChart("Sorting time (in seconds)", subtitle, createDataRun(quicksortTimes, heapsortTimes));
        JFreeChart chartOperations = createChart("Number of comparisons and exchanges", subtitle, createDataRun(quicksortOperations, heapsortOperations));
        plotGraph(fileName, chartTimes, chartOperations);
    }

    public void plotAverageGraph(String subtitle, String fileName) throws IOException {
        JFreeChart chartTimes = createChart("Sorting time (in seconds)", subtitle, createDataAverage(averageQuicksortTimes, averageHeapsortTimes));
        JFreeChart chartOperations = createChart("Number of comparisons and exchanges", subtitle, createDataAverage(averageQuicksortOperations, averageHeapsortOperations));
        plotGraph(fileName, chartTimes, chartOperations);
    }
}
