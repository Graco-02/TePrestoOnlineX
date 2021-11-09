package com.example.teprestoonline.Modelo;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class amortizacion_cuota {
    private String id;
    private String id_prestamo;
    private String fecha_alta_humana;
    private long fecha_alta_unix;
    private String fecha_modificacion_humana;
    private long fecha_modificacion_unix;

    private String fecha_cuota;
    private String fecha_pago;
    private double cuota;
    private double interes;
    private double capital;
    private int estado;
    private String estado_descripcion;

    public amortizacion_cuota(){

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha_alta_humana() {
        return fecha_alta_humana;
    }

    public void setFecha_alta_humana(String fecha_alta_humana) {
        this.fecha_alta_humana = fecha_alta_humana;
    }

    public long getFecha_alta_unix() {
        return fecha_alta_unix;
    }

    public void setFecha_alta_unix(long fecha_alta_unix) {
        this.fecha_alta_unix = fecha_alta_unix;
    }

    public String getFecha_modificacion_humana() {
        return fecha_modificacion_humana;
    }

    public void setFecha_modificacion_humana(String fecha_modificacion_humana) {
        this.fecha_modificacion_humana = fecha_modificacion_humana;
    }

    public long getFecha_modificacion_unix() {
        return fecha_modificacion_unix;
    }

    public void setFecha_modificacion_unix(long fecha_modificacion_unix) {
        this.fecha_modificacion_unix = fecha_modificacion_unix;
    }

    public void set_datos_unicos(){
        setId(UUID.randomUUID().toString());
        Calendar calendario =  Calendar.getInstance();
        setFecha_alta_unix((calendario.getTimeInMillis() ));
        setFecha_modificacion_unix((calendario.getTimeInMillis() ));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendario.getTime());
        setFecha_alta_humana(formattedDate);
        setFecha_modificacion_humana(formattedDate);
    }

    public void set_datos_ultima_modificaion(){
        Calendar calendario =  Calendar.getInstance();
        setFecha_modificacion_unix((calendario.getTimeInMillis() ));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendario.getTime());
        setFecha_modificacion_humana(formattedDate); // seteo la fecha de ultima modificacion legible
    }


    public String getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(String id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    public String getFecha_cuota() {
        return fecha_cuota;
    }

    public void setFecha_cuota(String fecha_cuota) {
        this.fecha_cuota = fecha_cuota;
    }

    public String getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(String fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
        switch (this.estado){
            case 0:
                setEstado_descripcion("CAIDA");
                break;
            case 1:
                setEstado_descripcion("NO PAGADA");
                break;
            case 2:
                setEstado_descripcion("PAGADA");
                break;
            default:
                setEstado_descripcion("PENDIENTE");
                break;
        }
    }

    public String getEstado_descripcion() {
        return estado_descripcion;
    }

    public void setEstado_descripcion(String estado_descripcion) {
        this.estado_descripcion = estado_descripcion;
    }


}
