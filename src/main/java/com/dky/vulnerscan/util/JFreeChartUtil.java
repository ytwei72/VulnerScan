package com.dky.vulnerscan.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class JFreeChartUtil {

	/**
	 * 创建生成图表(柱形图、折线图以及区域图等)数据集合
	 * 
	 * @return CategoryDataset
	 */
	private static CategoryDataset createDataSet(ArrayList<HashMap<String, String>> data) {

		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		if(data!=null){
			for (int i = 0; i < data.size(); i++) {
				String name = data.get(i).get("name");
				int num = Integer.parseInt(data.get(i).get("num"));
				dataSet.addValue(num, name, name);
			}
		}else{
			dataSet.addValue(0, "", "");
		}
		return dataSet;

	}

	/**
	 * 创建JFreeChart对象(柱状图)
	 * 
	 * @param title  柱状图标题
	 * @param xLabel 横坐标标题
	 * @param yLabel 纵坐标标题
	 * @param data   数据集           
	 * @return JFreeChart
	 */
	public static JFreeChart createBarChart(String title, String xLabel, String yLabel,
			ArrayList<HashMap<String, String>> data) {

		JFreeChart chart = ChartFactory.createBarChart(title, xLabel, yLabel, createDataSet(data),
				PlotOrientation.HORIZONTAL, false, false, false);
		chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);// 抗锯齿关闭
		chart.getTitle().setFont(new Font("宋体", Font.BOLD, 25)); // 设置标题字体

		CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		categoryPlot.setBackgroundPaint(Color.WHITE);
		categoryPlot.setNoDataMessage("无数据");
		categoryPlot.setNoDataMessageFont(new Font("宋体", Font.PLAIN, 18));
		categoryPlot.setNoDataMessagePaint(Color.RED);
		if(createDataSet(data).getColumnCount()==1){
			categoryPlot.getDomainAxis().setLowerMargin(0.4);
			categoryPlot.getDomainAxis().setUpperMargin(0.5);
		}
		NumberAxis numberAxis = (NumberAxis) categoryPlot.getRangeAxis();	// 纵坐标--范围轴
		numberAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));	// 纵坐标y轴坐标字体
		numberAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12)); 	// 纵坐标y轴标题字体

		CategoryAxis categoryAxis = categoryPlot.getDomainAxis();	// 横坐标--类别轴、域
		categoryAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12)); 	//横坐标x轴坐标字体
		categoryAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));		// 横坐标x轴标题字体

		BarRenderer barRenderer = (BarRenderer) categoryPlot.getRenderer(); //中间的部分--渲染
		barRenderer.setSeriesFillPaint(0, Color.CYAN);
		barRenderer.setMaximumBarWidth(0.1);
		categoryPlot.setRenderer(barRenderer);
		return chart;
	}
	public static void getPicUrl(String title, String xLabel, String yLabel,
								   ArrayList<HashMap<String, String>> data,String imgUrl) {

		try {
			OutputStream os = new FileOutputStream(imgUrl);//图片是文件格式的，故要用到FileOutputStream用来输出。
			ChartUtils.writeChartAsJPEG(os, createBarChart(title,xLabel,yLabel,data), 600, 480);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
