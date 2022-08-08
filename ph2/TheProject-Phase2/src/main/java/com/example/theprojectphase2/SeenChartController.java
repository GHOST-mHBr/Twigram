package com.example.theprojectphase2;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeenChartController {

    @FXML
    LineChart<String, Number> chart;

    public void initialize(LinkedHashMap<String, String> map){

        chart.setLegendVisible(false);

        XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();



        Map.Entry<String,String> entry1 = map.entrySet().iterator().next();
        String date = entry1.getValue();
        int i = 1;


        for(Map.Entry<String, String> entry : map.entrySet()){

            if(entry.equals(map.entrySet().toArray()[map.size()-1]))
                series.getData().add(new XYChart.Data<String,Number>(date,i));
            else {
                if (entry.getValue().equals(date)) {
                    i++;
                } else {
                    series.getData().add(new XYChart.Data<String, Number>(date, i));
                    date = entry.getValue();
                    i = 1;
                }
            }

        }


        chart.getData().add(series);
    }
}
