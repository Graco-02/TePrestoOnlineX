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
import com.example.teprestoonline.Modelo.Usuario;
import com.example.teprestoonline.utilidades.Fecha_utiliti;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
    private BarChart grafico_baras;

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
    private ArrayList<Entry> lineEntries_finaciamientos = new ArrayList<Entry>();
    private double total_recibido;
    private double num_total_pagos;
    private double num_total_financiamientos;
    private double total_financiado;
    private BarData data;
    private ArrayList<BarEntry> financiamientos_lista;
    private ArrayList<BarEntry> pagos_lista;

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

       // estadisticas_fr_ctr = new estadisticas_firebase_ctr(); //inicio la instancia del controlados de BBDD
        grafico_baras = (BarChart) findViewById(R.id.barra_graficas);
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


    public void set_refrescar(View v){
        lineChart.removeAllViews();
        grafico_baras.removeAllViews();

        set_listar_pretamos(selector_mes.getSelectedItemPosition()+1
                ,Integer.parseInt(txt_year.getText().toString())
        );
    }



    private void set_listar_pretamos(int mes, int year) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME);
        final Query usuQuery = ref.orderByChild("id");
        total_recibido=0;
        num_total_pagos=0;
        num_total_financiamientos=0;
        total_financiado=0;

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot hijo : dataSnapshot.getChildren()) {
                        Prestamo p = hijo.getValue(Prestamo.class);
                        if(p.getId_usuario().equalsIgnoreCase(Usuario.usuario_logueado.getId())) {
                            if (op_financiamientos.isChecked()) {
                                String fecha_split[] = p.getFecha_alta_humana().substring(0, 10).split("-");
                                int year_f = Integer.parseInt(fecha_split[0]);
                                int mes_f = Integer.parseInt(fecha_split[1]);

                                if (mes_f == mes && year_f == year) {
                                    num_total_financiamientos++;
                                    total_financiado += p.getMonto_financiado();
                                }

                            }

                            if (op_pagos.isChecked()) {
                                get_listado_pagos(mes, year, p);
                            }
                        }//valido que el proceso solo tome en cuenta los prestamos del usuario que lanza
                    }// fin del bucle for


                    grafico_baras.removeAllViews();
                    set_proceso_grafica_en_barras( mes,  year);
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
        lineChart.setVisibility(View.GONE);

        final Query usuQuery = myRef.orderByChild("fecha_alta_unix:");
        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        Pago pago = hijo.getValue(Pago.class);

                        if(pago.getFecha_pago()!=null && pago.getId_prestamo().equalsIgnoreCase(p.getId())) {
                            String fecha_split[] = pago.getFecha_pago().split("-");
                            int mes_pago = Integer.parseInt(fecha_split[1]);
                            int year_pago = Integer.parseInt(fecha_split[0]);;


                            if(mes_pago == mes && year_pago == year ) {
                                total_recibido += pago.getMonto_pagado();
                                num_total_pagos++;
                            }
                        }
                    }//fin bucle

                }else{
                    System.out.println("SIN DATOS PARA MOSTRAR TOODOS LOS PAGOS..:" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void set_proceso_grafica_en_barras(int mes, int year){

        pagos_lista = new ArrayList<>();
        financiamientos_lista = new ArrayList<>();

        if(num_total_pagos> 0){
              pagos_lista.add( new BarEntry(1,(float) total_recibido) );
        }

        if(num_total_financiamientos > 0){
            financiamientos_lista.add(new BarEntry( 5 , (float) total_financiado ));
        }

        BarDataSet barDataSet_pagos = new BarDataSet(pagos_lista,"PAGOS RECIBIDOS");
        barDataSet_pagos.setColor(Color.BLUE);
        barDataSet_pagos.setDrawValues(true);
        barDataSet_pagos.setBarBorderColor(Color.BLUE);

        BarDataSet barDataSet_f = new BarDataSet(financiamientos_lista,"FINANCIAMIENTO REALIZADOS");
        barDataSet_f.setColor(Color.RED);
        barDataSet_f.setDrawValues(true);

        data = new BarData(barDataSet_pagos,barDataSet_f);
        data.groupBars(0,0,1);

        grafico_baras.setData(data);



    }



}