package com.example.teprestoonline.Modelo;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Persona extends Direccion {
    private String id;
    private String fecha_alta_humana;
    private long fecha_alta_unix;
    private String fecha_modificacion_humana;
    private long fecha_modificacion_unix;
    private String nombres;
    private String apellidos;
    private String identificacion;
    private String tipo_identificacion;
    private Contacto contacto;

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
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

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTipo_identificacion() {
        return tipo_identificacion;
    }

    public void setTipo_identificacion(String tipo_identificacion) {
        this.tipo_identificacion = tipo_identificacion;
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
