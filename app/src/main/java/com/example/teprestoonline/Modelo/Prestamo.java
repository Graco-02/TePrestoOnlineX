package com.example.teprestoonline.Modelo;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Prestamo extends Prestamo_cuota implements Serializable {

    private String id;
    private String fecha_alta_humana;
    private long fecha_alta_unix;
    private String fecha_modificacion_humana;
    private long fecha_modificacion_unix;
    private String id_cliente;
    private String id_usuario;

    private double monto_financiado;
    private int tasa;
    private double restante;
    private String fecha_ult_cobro;
    private String fecha_ult_pago;
    private int periodo;
    private int tipo;
    private int porcentage_atrazo;
    private int monto_fijo;

    private String contrato_ruta;

    public String getContrato_ruta() {
        return contrato_ruta;
    }

    public void setContrato_ruta(String contrato_ruta) {
        this.contrato_ruta = contrato_ruta;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public String getId() {
        return id;
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

    public double getMonto_financiado() {
        return monto_financiado;
    }

    public void setMonto_financiado(double monto_financiado) {
        this.monto_financiado = monto_financiado;
    }

    public int getTasa() {
        return tasa;
    }

    public void setTasa(int tasa) {
        this.tasa = tasa;
    }

    public double getRestante() {
        return restante;
    }

    public void setRestante(double restante) {
        this.restante = restante;
    }

    public String getFecha_ult_cobro() {
        return fecha_ult_cobro;
    }

    public void setFecha_ult_cobro(String fecha_ult_cobro) {
        this.fecha_ult_cobro = fecha_ult_cobro;
    }

    public String getFecha_ult_pago() {
        return fecha_ult_pago;
    }

    public void setFecha_ult_pago(String fecha_ult_pago) {
        this.fecha_ult_pago = fecha_ult_pago;
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

    public int getPorcentage_atrazo() {
        return porcentage_atrazo;
    }

    public void setPorcentage_atrazo(int porcentage_atrazo) {
        this.porcentage_atrazo = porcentage_atrazo;
    }

    public int getMonto_fijo() {
        return monto_fijo;
    }

    public void setMonto_fijo(int monto_fijo) {
        this.monto_fijo = monto_fijo;
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

    public String get_periodo_string(){
        switch (getPeriodo()){
            case 1:
                return "DIARIO";
            case 7:
                return "SEMANAL";
            case 15:
                return "QUINCENAL";
            default:
                return "MENSUAL";
        }
    }

}
