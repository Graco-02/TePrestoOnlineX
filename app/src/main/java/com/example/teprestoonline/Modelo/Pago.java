package com.example.teprestoonline.Modelo;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Pago {
    private String id;
    private String fecha_alta_humana;
    private long fecha_alta_unix;
    private String fecha_modificacion_humana;
    private long fecha_modificacion_unix;

    private String id_cliente;
    private String id_usuario;

    private String id_prestamo;
    private String fecha_pago;
    private double monto_pagado;
    private double monto_restante;
    private double capital_amortizado;
    private double interes_amortizado;
    private String tipo;

    private String recibo_rutta="";

    public String getRecibo_rutta() {
        return recibo_rutta;
    }

    public void setRecibo_rutta(String recibo_rutta) {
        this.recibo_rutta = recibo_rutta;
    }

    public String getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(String id_prestamo) {
        this.id_prestamo = id_prestamo;
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

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }


    public String getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(String fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public double getMonto_pagado() {
        return monto_pagado;
    }

    public void setMonto_pagado(double monto_pagado) {
        this.monto_pagado = monto_pagado;
    }

    public double getMonto_restante() {
        return monto_restante;
    }

    public void setMonto_restante(double monto_restante) {
        this.monto_restante = monto_restante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCapital_amortizado() {
        return capital_amortizado;
    }

    public void setCapital_amortizado(double capital_amortizado) {
        this.capital_amortizado = capital_amortizado;
    }

    public double getInteres_amortizado() {
        return interes_amortizado;
    }

    public void setInteres_amortizado(double interes_amortizado) {
        this.interes_amortizado = interes_amortizado;
    }

    public void set_datos_unicos(){
        setId(UUID.randomUUID().toString());
        Calendar calendario =  Calendar.getInstance();
        setFecha_alta_unix((calendario.getTimeInMillis() * -1));
        setFecha_modificacion_unix((calendario.getTimeInMillis() * -1));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendario.getTime());
        setFecha_alta_humana(formattedDate);
        setFecha_modificacion_humana(formattedDate);
        setId_usuario(Usuario.usuario_logueado.getId());
    }

    public void set_datos_ultima_modificaion(){
        Calendar calendario =  Calendar.getInstance();
        setFecha_modificacion_unix((calendario.getTimeInMillis() * -1));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendario.getTime());
        setFecha_modificacion_humana(formattedDate); // seteo la fecha de ultima modificacion legible
    }
}
