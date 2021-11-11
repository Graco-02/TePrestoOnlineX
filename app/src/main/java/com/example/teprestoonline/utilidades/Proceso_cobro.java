package com.example.teprestoonline.utilidades;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Prestamo_ctr;

import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.amortizacion_cuota;
import com.example.teprestoonline.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Proceso_cobro {
    private AppCompatActivity applicationContext;
    private static final double CT_100 = 100.00;
    private LinearLayout ln = null;
    private Prestamo_ctr controlador;
    final Fecha_utiliti gestor_fechas = new Fecha_utiliti();
    final String fecha_dia = gestor_fechas.getFechaSystemaYYMMDD();

    public Proceso_cobro(AppCompatActivity applicationContext) {
        this.applicationContext = applicationContext;
        ln = (LinearLayout) this.applicationContext.findViewById(R.id.inicio_pantalla_proceso);
        ln.removeAllViews();
        controlador = new Prestamo_ctr();
    }

    public void set_validaciones_prestamos_regulares(Prestamo p) {
        String fecha_proceso = new Fecha_utiliti().getFechaSystemaYYMMDD();
        int dias_transcurridos = 0;

        if(!p.getFecha_ult_cobro().equals("0001-01-01")){
            dias_transcurridos = get_dias_trascurridos2(p.getFecha_ult_cobro());
        }else{
            dias_transcurridos = get_dias_trascurridos2(p.getFecha_alta_humana().substring(0,10));
        }

        if (dias_transcurridos > 0 && dias_transcurridos >= p.getPeriodo()){
            TextView label = new TextView(this.applicationContext);
            TextView label2 = new TextView(this.applicationContext);
            TextView label3 = new TextView(this.applicationContext);
            TextView label4 = new TextView(this.applicationContext);
            label.setPadding(5,5,5,5);
            label2.setPadding(5,5,5,5);
            label3.setPadding(5,5,5,5);
            label4.setPadding(5,5,5,5);

            double anterior_deuda = p.getRestante();
            double nueva_deuda = p.getRestante();
            int dias_transcurridos2 = dias_transcurridos;

            while(dias_transcurridos2 >= p.getPeriodo()){
                nueva_deuda = nueva_deuda + (get_interes(p));
                p.setRestante(nueva_deuda);
                dias_transcurridos2-=p.getPeriodo();
            }

            p.setFecha_ult_cobro(fecha_proceso);
            p.set_datos_ultima_modificaion(this.applicationContext);
            //actualizo el prestamo con el nuevo saldo y la fecha de ultimo cobro a la de la ejecucion
            controlador.set_prestamo(p);

            label.setText("Prestamo con fecha caducada .: "+ p.getId());
            label.setText("Prestamo tipo .: " + p.getTipo());//0 regular 1 cuotas
            label2.setText("Fecha ult cobro .: "+ p.getFecha_ult_cobro());
            label3.setText("Fecha alta .: "+ p.getFecha_alta_humana().substring(0,10));
            label4.setText("Dias Trascurridos .: "+ dias_transcurridos + " deuda anterior .: " + anterior_deuda
            + " nueva deuda .: " + nueva_deuda
            );

            ln.addView(label);
            ln.addView(label2);
            ln.addView(label3);
            ln.addView(label4);
        }

    }

    public void set_validaciones_prestamos_cuotas(Prestamo p) {
        FirebaseDatabase database = null;
        final DatabaseReference myRef;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Prestamo_ctr.BBDD_NAME2).child(p.getId());
        final Query usuQuery = myRef.orderByChild("fecha_alta_unix");


        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        amortizacion_cuota amorizacion = hijo.getValue(amortizacion_cuota.class);
                        double fecha_dia_num = gestor_fechas.get_fecha_numerica(fecha_dia);
                        double fecha_cuota_num = gestor_fechas.get_fecha_numerica(amorizacion.getFecha_cuota());
                        int nuevo_estado = 0;
                        int dias_transcurridos_act = 0;
                        dias_transcurridos_act = get_dias_trascurridos2(
                                amorizacion.getFecha_modificacion_humana().substring(0,10)
                        );

                        if(fecha_dia_num >= fecha_cuota_num && amorizacion.getEstado() != 2 && dias_transcurridos_act > 0) {
                                if (fecha_dia_num == fecha_cuota_num ) {
                                    nuevo_estado=0;//cuota caida
                                } else if (fecha_dia_num > fecha_cuota_num) {
                                    nuevo_estado=1;//cuota no pagada
                                    //proceso para generar interes sobre la cuota atrazada estos son de cobro opcional
                                    //por lo que se procede a solo actualizar el campo interes de la cuota
                                    //al momento del pago se presentara el monto en atrazo por igual al listar las amortizaciones
                                    int dias_transcurridos = 0;
                                    dias_transcurridos = get_dias_trascurridos2(amorizacion.getFecha_cuota());

                                    //si han pasado menos dias desde la ultima actualizacion que la fecha de la cuota es que ya se ha actualizado
                                    if(dias_transcurridos_act<dias_transcurridos){
                                        dias_transcurridos = dias_transcurridos_act;
                                    }

                                    double nuevo_interes = 0;
                                    nuevo_interes = set_proceso_cuota_en_atrazo(amorizacion.getInteres(),
                                            amorizacion.getCapital(),p,dias_transcurridos);
                                    amorizacion.setInteres(nuevo_interes);
                                }

                            if(nuevo_estado != amorizacion.getEstado()) {
                                amorizacion.setEstado(nuevo_estado);
                                amorizacion.set_datos_ultima_modificaion();

                                TextView label = new TextView(applicationContext);
                                TextView label2 = new TextView(applicationContext);
                                TextView label3 = new TextView(applicationContext);
                                TextView label4 = new TextView(applicationContext);
                                TextView label5 = new TextView(applicationContext);
                                label.setPadding(5, 5, 5, 5);
                                label2.setPadding(5, 5, 5, 5);
                                label3.setPadding(5, 5, 5, 5);
                                label4.setPadding(5, 5, 5, 5);
                                label5.setPadding(5, 5, 5, 5);

                                label.setText("Prestamo cuotas con fecha caducada .: " + p.getId());
                                label5.setText("Prestamo tipo .: " + p.getTipo());//0 regular 1 cuotas
                                label3.setText("Fecha alta .: " + p.getFecha_alta_humana().substring(0, 10));
                                label2.setText("Caducada .: " + amorizacion.getFecha_cuota());
                                label4.setText("Dias Trascurridos .: " + (fecha_dia_num - fecha_cuota_num));

                                ln.addView(label);
                                ln.addView(label2);
                                ln.addView(label3);
                                ln.addView(label4);
                                ln.addView(label5);
                            }

                            controlador.set_prestamo_amortizaciones(amorizacion); //actualizo el estado de la cuota
                        }else{
                            controlador.set_prestamo_amortizaciones(amorizacion); //actualizo el estado de la cuota
                        }

                    }
                }else{
                    Toast.makeText(applicationContext,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public double get_interes(Prestamo prestamo){
        if(prestamo.getTipo()==0){
            double periodo = prestamo.getPeriodo();

            double tasa = (prestamo.getTasa()/ CT_100);
            double tasa_periodificada = (  tasa / periodo);

            double interes = prestamo.getRestante() * tasa_periodificada;
            return interes;
        }else{
            double periodo = prestamo.getPeriodo();

            double tasa = (prestamo.getTasa()/ CT_100);
            double tasa_periodificada = (  tasa / periodo);

            double interes = prestamo.getCuota() * tasa_periodificada;
            return interes;
        }

    }

    private double get_interes_cuota(Prestamo prestamo,double monto){

        double periodo = prestamo.getPeriodo();

        double tasa = (prestamo.getTasa()/ CT_100);
        double tasa_periodificada = (  tasa / periodo);

        double interes = monto * tasa_periodificada;
        return interes;


    }


    public int get_dias_trascurridos2(String fecha){
        int dias = 0;
        dias = new Fecha_utiliti().getTiempo_Transcurido_DIAS_YYYYMMDD(
                fecha , new Fecha_utiliti().getFechaSystemaYYMMDD() ) ;

        return dias;
    }


    protected double set_proceso_cuota_en_atrazo(double monto_capital,double monto_interes,Prestamo p,int dias_atrazo){
        double cuota = monto_capital + monto_interes;
        double nuevo_interes = 0;

        for (int i=0;i<dias_atrazo;i++){
            nuevo_interes = get_interes_cuota(p,cuota);
            cuota = cuota + nuevo_interes;
        }

        nuevo_interes = get_interes_cuota(p,cuota);

        return nuevo_interes;
    }
}
