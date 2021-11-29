package com.example.teprestoonline.Modelo;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Parametros implements Serializable {
    private String id_usuario;
    private String fecultcobro;
    private int pts_perder;
    private int pts_ganar;
    private  String dispo_bt_nombre;
    private  String dispo_bt_mac;
    private int dias_gracia;

    private String id;
    private String fecha_alta_humana;
    private long fecha_alta_unix;
    private String fecha_modificacion_humana;
    private long fecha_modificacion_unix;

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

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getFecultcobro() {
        return fecultcobro;
    }

    public void setFecultcobro(String fecultcobro) {
        this.fecultcobro = fecultcobro;
    }

    public int getPts_perder() {
        return pts_perder;
    }

    public void setPts_perder(int pts_perder) {
        this.pts_perder = pts_perder;
    }

    public int getPts_ganar() {
        return pts_ganar;
    }

    public void setPts_ganar(int pts_ganar) {
        this.pts_ganar = pts_ganar;
    }

    public  String getDispo_bt_nombre() {
        return dispo_bt_nombre;
    }

    public  void setDispo_bt_nombre(String dispo_bt_nombre) {
        this.dispo_bt_nombre = dispo_bt_nombre;
    }

    public  String getDispo_bt_mac() {
        return dispo_bt_mac;
    }

    public  void setDispo_bt_mac(String dispo_bt_mac) {
        this.dispo_bt_mac = dispo_bt_mac;
    }

    public int getDias_gracia() {
        return dias_gracia;
    }

    public void setDias_gracia(int dias_gracia) {
        this.dias_gracia = dias_gracia;
    }



    public void set_datos_unicos(Context contesto){
        setId(UUID.randomUUID().toString());
        Calendar calendario =  Calendar.getInstance();
        setFecha_alta_unix((calendario.getTimeInMillis() * -1));
        setFecha_modificacion_unix((calendario.getTimeInMillis() * -1));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendario.getTime());
        setFecha_alta_humana(formattedDate);
        setFecha_modificacion_humana(formattedDate);

    }

    public void set_datos_ultima_modificaion(Context contesto){
        Calendar calendario =  Calendar.getInstance();
        setFecha_modificacion_unix((calendario.getTimeInMillis() * -1));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendario.getTime());
        setFecha_modificacion_humana(formattedDate); // seteo la fecha de ultima modificacion legible
    }
}
