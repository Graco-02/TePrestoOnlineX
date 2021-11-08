package com.example.teprestoonline.utilidades;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.teprestoonline.Controladores.Prestamo_ctr;

import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.R;

import androidx.appcompat.app.AppCompatActivity;

public class Proceso_cobro {
    private AppCompatActivity applicationContext;
    private static final double CT_100 = 100.00;
    private LinearLayout ln = null;
    private Prestamo_ctr controlador;

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

        if (dias_transcurridos>0 && dias_transcurridos > p.getPeriodo()){
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

    public double get_interes_cuota(Prestamo prestamo){

        double periodo = prestamo.getPeriodo();

        double tasa = (prestamo.getTasa()/ CT_100);
        double tasa_periodificada = (  tasa / periodo);

        double interes = prestamo.getCuota() * tasa_periodificada;
        return interes;


    }

    public int get_dias_trascurridos(Prestamo p,String fecha_ult_pago){
        int dias = 0;
        dias =
                new Fecha_utiliti().getTiempo_Transcurido_DIAS_YYYYMMDD(
                        p.getFecha_alta_humana().substring(0,10) ,
                        new Fecha_utiliti().getFechaSystemaYYMMDD() ) ;

        return dias;
    }

    public int get_dias_trascurridos2(String fecha){
        int dias = 0;
        dias =
                new Fecha_utiliti().getTiempo_Transcurido_DIAS_YYYYMMDD(
                        fecha ,
                        new Fecha_utiliti().getFechaSystemaYYMMDD() ) ;

        return dias;
    }
}
