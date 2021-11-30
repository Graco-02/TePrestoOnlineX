package com.example.teprestoonline;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class estadisticas_pruebas {
    private AppCompatActivity activiti;
    private LineChart lineChart;
    private LineDataSet lineDataSet;

    public estadisticas_pruebas(AppCompatActivity activiti){
        this.activiti = activiti;
        AlertDialog.Builder builder = new AlertDialog.Builder(activiti);
        LayoutInflater inflater = activiti.getLayoutInflater();
        View v = inflater.inflate(R.layout.estadisticas1, null);
        builder.setView(v);
        builder.create();

// Creamos un set de datos
        ArrayList<Entry> lineEntries = new ArrayList<Entry>();
        for (int i = 0; i<31; i++){
            float y = (int) (Math.random() * 8) + 1;
            lineEntries.add(new Entry((float) i,(float)y));
        }

// Unimos los datos al data set
        lineChart = v.findViewById(R.id.lineChart);
        lineDataSet = new LineDataSet(lineEntries, "Platzi");

// Asociamos al gráfico
        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);
        lineChart.setData(lineData);

        final AlertDialog ventana = builder.show();

    }
}
