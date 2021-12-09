package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Controladores.estadisticas_firebase_ctr;
import com.example.teprestoonline.Modelo.Pago;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.utilidades.Fecha_utiliti;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Mantenimiento_estadisticas extends AppCompatActivity {
    private LineChart lineChart;
    private LineDataSet lineDataSet_pagos;
    private LineDataSet lineDataSet_financiamientos;
    private Fecha_utiliti fecha_controler = new Fecha_utiliti();
    private EditText txt_year;
    private Spinner selector_mes;
    private CheckBox op_pagos;
    private CheckBox op_financiamientos;
    private estadisticas_firebase_ctr estadisticas_fr_ctr;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<Entry> lineEntries_pagos = new ArrayList<Entry>();
    private double total_recibido;
    private double num_total_pagos;
    private double num_total_financiamientos;
    private double total_financiado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimiento_estadisticas);

        selector_mes = (Spinner) findViewById(R.id.estadisticas_meses);
        selector_mes.setAdapter(proceso_spiner());

        //coloco en el texto inicial el anio en curso
        txt_year = (EditText) findViewById(R.id.estadisticas_year);
        txt_year.setText(fecha_controler.getFechaSystemaYYMMDD().substring(0,4));

        //dejo seleccionado el mes en curso
        int mes = fecha_controler.getMes();
        selector_mes.setSelection(mes-1);

        op_financiamientos= (CheckBox) findViewById(R.id.estadisticas_opc_salidas);
        op_pagos= (CheckBox) findViewById(R.id.estadisticas_opc_entradas);

        estadisticas_fr_ctr = new estadisticas_firebase_ctr(); //inicio la instancia del controlados de BBDD

        set_listar_pretamos(selector_mes.getSelectedItemPosition()+1
                ,Integer.parseInt(txt_year.getText().toString())
        );
    }


    private ArrayAdapter<String> proceso_spiner(){
        //lleno el combo tipos de documentos
        List<String> meses = new ArrayList<String>();
        for(int i= 1;i<=12;i++){
            meses.add(new Fecha_utiliti().get_nombre_del_mes(i));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, meses);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapter;
    }

    private void set_estadisticas(int x,int y1){//donde x es la cantidad de dias del mes y y es el valor calculado
        // Creamos un set de datos
        ArrayList<Entry> lineEntries_pagos = new ArrayList<Entry>();
        ArrayList<Entry> lineEntries_financiamientos = new ArrayList<Entry>();

        for (int i = 0; i <= x; i++){
            float y = (int) (Math.random() * 8) + 1;
            lineEntries_pagos.add(new Entry((float) i,(float)y));
        }

        for (int i = 0; i <= x; i++){
            float y = (int) (Math.random() * 8) + 1;
            lineEntries_financiamientos.add(new Entry((float) i,(float)y));
        }

       // Unimos los datos al data set para los pagos
        lineChart = findViewById(R.id.lineChart);
        lineChart.removeAllViews();

        lineDataSet_pagos = new LineDataSet(lineEntries_pagos, "pagos");
       // lineDataSet_pagos = new LineDataSet(lineEntries_pagos_2, "pagos");
        lineDataSet_pagos.setDrawCircles(false);
        lineDataSet_pagos.setColor(Color.BLUE);
        lineDataSet_pagos.setDrawValues(false);

        lineDataSet_financiamientos = new LineDataSet(lineEntries_financiamientos, "financiamientos");
        lineDataSet_financiamientos.setDrawCircles(false);
        lineDataSet_financiamientos.setColor(Color.GREEN);
        lineDataSet_financiamientos.setDrawValues(false);

      // Asociamos al gráfico
        LineData lineData = new LineData();

        if(op_pagos.isChecked()){
            lineData.addDataSet(lineDataSet_pagos);
        }
        if(op_financiamientos.isChecked()){
            lineData.addDataSet(lineDataSet_financiamientos);
        }

        if(!op_pagos.isChecked() && !op_financiamientos.isChecked()){
            Toast.makeText(getApplicationContext(),"Se debe seleccionar al menos una linea",Toast.LENGTH_LONG).show();
        }

        lineChart.setData(lineData);

    }

    public void set_refrescar(View v){
        lineChart.removeAllViews();
        set_listar_pretamos(selector_mes.getSelectedItemPosition()+1
                ,Integer.parseInt(txt_year.getText().toString())
        );
    }



    private void set_listar_pretamos(int mes, int year) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME);
        final Query usuQuery = ref.orderByChild("id");
        lineChart = findViewById(R.id.lineChart);
        lineChart.removeAllViews();

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lineChart.removeAllViews();
                    for (DataSnapshot hijo : dataSnapshot.getChildren()) {
                            Prestamo p = hijo.getValue(Prestamo.class);
                            if(op_pagos.isChecked()) {
                                get_listado_pagos(mes, year, p);
                            }
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void get_listado_pagos(int mes, int year, Prestamo p){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Prestamo_ctr.BBDD_NAME3).child(p.getId());

        final Query usuQuery = myRef.orderByChild("fecha_alta_unix:");
        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        Pago pago = hijo.getValue(Pago.class);

                        if(pago.getFecha_pago()!=null) {
                            String fecha_split[] = pago.getFecha_pago().split("-");
                            int mes_pago = Integer.parseInt(fecha_split[1]);
                            int year_pago = Integer.parseInt(fecha_split[0]);;
                            int dia_pago = Integer.parseInt(fecha_split[2]);;

                                Entry entrada=null;

                                if(mes_pago == mes && year_pago == year ) {
                                    System.out.println("COORDENADAS x,y = " + "( "+dia_pago+" , "+ pago.getMonto_pagado()+" )");
                                     entrada = new Entry(
                                            (float) dia_pago, (float) pago.getMonto_pagado()
                                    );

                                    total_recibido += pago.getMonto_pagado();

                                    lineEntries_pagos.add(entrada);
                                }
                        }
                    }//fin bucle


                    if(lineEntries_pagos.size()>0) {
                        System.out.println("lineEntries_pagos " + lineEntries_pagos.size());


                        ArrayList<Entry> lineEntries_pagos2 = new ArrayList<>();
/*
                        double valor=0;
                        float dia_ant =  lineEntries_pagos.get(0).getX();
                        for(int pos=0;pos<lineEntries_pagos.size();pos++){
                           if(lineEntries_pagos.get(pos).getX() == dia_ant){
                               valor+=  lineEntries_pagos.get(pos).getY();
                           }else{
                               if(lineEntries_pagos.get(pos).getX() > dia_ant){
                                   lineEntries_pagos2.add(new Entry(
                                           (float) dia_ant, (float) valor
                                   ));
                                   valor=0;
                                   dia_ant= lineEntries_pagos.get(pos).getX();
                               }
                           }
                        }*/
                      /*  for(int pos=0;pos<lineEntries_pagos.size();pos++) {
                            for (int dia = 1; dia <= fecha_controler.get_dias_mes(mes, year); dia++) {
                                if (dia == lineEntries_pagos.get(pos).getX()) {
                                   lineEntries_pagos2.add(new Entry(
                                           (float) dia,lineEntries_pagos.get(pos).getY()
                                   ));
                                }else{
                                    lineEntries_pagos2.add(new Entry(
                                            (float) dia,(float) 0
                                    ));
                                }
                            }
                        }*/

                        lineDataSet_pagos = new LineDataSet(lineEntries_pagos, "pagos");
                      //  lineDataSet_pagos = new LineDataSet(lineEntries_pagos2, "pagos");
                        lineDataSet_pagos.setDrawCircles(false);
                        lineDataSet_pagos.setColor(Color.BLUE);
                        lineDataSet_pagos.setDrawValues(false);
                        lineDataSet_pagos.setDrawFilled(false);
                        lineDataSet_pagos.setDrawHighlightIndicators(false);

                        LineData lineData = new LineData();
                        lineData.addDataSet(lineDataSet_pagos);
                        lineChart.removeAllViews();
                        lineChart.setData(lineData);
                    }
                }else{
                    System.out.println("SIN DATOS PARA MOSTRAR TOODOS LOS PAGOS..:" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}