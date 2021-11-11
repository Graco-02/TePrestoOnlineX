package com.example.teprestoonline.utilidades;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fecha_utiliti  extends Date{

    private static final long serialVersionUID = 1L;
    private int dia;
    private int mes;
    private int ano;
    private String hora;

    public Fecha_utiliti(){
        setDia(getDate());
        setMes(getMonth() + 1);
        setAno(getYear() - 100 + 2000);

        hora = this.getHours()+":"+this.getMinutes()+":"+this.getSeconds();
    }

    public java.sql.Date get_fecha_sql(){
        return new java.sql.Date(new Date().getTime());
    }

    public Time get_hora_sql(){
        return new Time(new Date().getTime());
    }

    public String getFechaSystemaYYMMDD()
    {
        String fecha = String.valueOf(this.ano) + "-" + String.valueOf(this.mes) + "-" + String.valueOf(this.dia);

        return fecha;
    }

    public String getFechaSystemaDDMMYY()
    {
        String fecha = String.valueOf(this.dia) + "-" + String.valueOf(this.mes) + "-" + String.valueOf(this.ano);

        return fecha;
    }


    public int getTiempo_Transcurido_DIAS_YYYYMMDD(String fecha)
    {
        String[] fechaComoArray = fecha.split("-");

        int year = Integer.parseInt(fechaComoArray[0]);
        int mes  = Integer.parseInt(fechaComoArray[1]);
        int dia  = Integer.parseInt(fechaComoArray[2]);

        int resultado = ((this.ano - year) * 12 + (this.mes - mes)) * 30 + (this.dia - dia);

        return resultado;
    }

    public int getTiempo_Transcurido_DIAS_YYYYMMDD(String fecha,String fecha2)
    {
        String[] fechaComoArray = fecha.split("-");

        int year = Integer.parseInt(fechaComoArray[0]);
        int mes  = Integer.parseInt(fechaComoArray[1]);
        int dia  = Integer.parseInt(fechaComoArray[2]);

        fechaComoArray = fecha2.split("-");

        int year2 = Integer.parseInt(fechaComoArray[0]);
        int mes2  = Integer.parseInt(fechaComoArray[1]);
        int dia2  = Integer.parseInt(fechaComoArray[2]);


        int year_t = (year2 - year);
        int mest = 0;

        if (mes == 12 && mes2 < mes && year_t > 0){
            mest = mes2;
        }else{
            mest = mes2 - mes;
        }

        int dia_t = dia2 - dia;
        //2021-03-31 - 2021-03-31
        //2021-2021 = 0
        //02 - 01 = 1 * 30 = 30
        //1 - 31 = 30
        System.out.println("EVALUANDO FECHAS" );
        System.out.println("EVALUANDO FECHAS " + " FECHA 1 = " + fecha + " FECHA 2 " + fecha2);
        System.out.println(dia + " -- " +  dia2 + " -- " +  dia_t);

        /*
        if(dia == get_dias_mes(mes,year)){
            mest  = 0 ;
            if(dia2 < dia){
                dia_t = dia2;
            }else  if(dia2 == dia){
                dia_t = 0;
            }else{
                dia_t =  dia2 - dia;
            }
        }*/

        int resultado = ( ( (year_t * 12) + mest ) * 30) + dia_t;
        System.out.println(" calculo y = " +  year_t + " m " + mest + " d " +  dia_t);
        System.out.println(" calculo2 " +   ( ( (year_t * 12) + mest ) * 30)  + " , "  +  dia_t);
        System.out.println(" resultado " +  resultado);
        return resultado;
    }

    public String suma_meses(String meses){
        int mm = Integer.parseInt(meses);
        String[] fechaComoArray = getFechaSystemaDDMMYY().split("-");

        int year = Integer.parseInt(fechaComoArray[2]);
        int mes = Integer.parseInt(fechaComoArray[1]);
        int dia = Integer.parseInt(fechaComoArray[0]);

        mes += mm;
        year += mes / 12;
        mes -= (12*(mes/12));

        return year+"-"+mes+"-"+dia;

    }

    public Date suma_dias(int dias){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos

    }

    public String suma_dias2(int dias){

        int year = this.ano;
        int mes = this.mes;
        int dia = this.dia;

        int year2=year;
        int mes2 = mes;
        int dia2= dia;


        if(dias>1) {
            for (int i = 0; i < dias; i++) {
                dia2 += 1;

                if (dia2 > get_dias_mes(mes, year)) {
                    dia2 -= get_dias_mes(mes, year);
                    mes2++;
                }

                if (mes2 > 12) {
                    mes2 = 1;
                    year2++;
                }
            }
        }else{
            dia2 += 1;
        }
        return String.valueOf(year2) + "-" + String.valueOf(mes2) + "-" + String.valueOf(dia2) ;
    }


    public String suma_dias3(int dias,String fecha){
        String[] fechaComoArray = fecha.split("-");

        int year = Integer.parseInt(fechaComoArray[0]);
        int mes = Integer.parseInt(fechaComoArray[1]);
        int dia = Integer.parseInt(fechaComoArray[2]);


        int year2=year;
        int mes2 = mes;
        int dia2= dia;


        if(dias>1) {
            for (int i = 0; i < dias; i++) {
                dia2 += 1;

                if (dia2 > get_dias_mes(mes, year)) {
                    dia2 -= get_dias_mes(mes, year);
                    mes2++;
                }

                if (mes2 > 12) {
                    mes2 = 1;
                    year2++;
                }
            }
        }else{
            dia2 += 1;
        }
        return String.valueOf(year2) + "-" + String.valueOf(mes2) + "-" + String.valueOf(dia2) ;
    }

    public String get_fecha_quincena(String fecha){
        String[] fechaComoArray = fecha.split("-");

        int year = Integer.parseInt(fechaComoArray[0]);
        int mes = Integer.parseInt(fechaComoArray[1]);
        int dia = Integer.parseInt(fechaComoArray[2]);

        if(dia<=15){
            dia = 15;
        }else if(dia>=15 && dia <= get_dias_mes(mes,year)){
           dia = get_dias_mes(mes,year);
        }else if(dia >= get_dias_mes(mes,year) ){
            dia = 15;
            if(mes==12){
                year+=1;
                mes = 1;
            }else{
                mes+=1;
            }
        }

        return String.valueOf(year) + "-" + String.valueOf(mes) + "-" + String.valueOf(dia) ;
    }

    public String get_fecha_mensual(String fecha){
        String[] fechaComoArray = fecha.split("-");

        int year = Integer.parseInt(fechaComoArray[0]);
        int mes = Integer.parseInt(fechaComoArray[1]);
        int dia = Integer.parseInt(fechaComoArray[2]);

        if(mes==12){
                year+=1;
                mes = 1;
                dia = get_dias_mes(mes,year);
        }else{
                mes+=1;
                dia = get_dias_mes(mes,year);
        }

        return String.valueOf(year) + "-" + String.valueOf(mes) + "-" + String.valueOf(dia) ;
    }

    public int getDia()
    {
        return this.dia;
    }

    public void setDia(int dia)
    {
        this.dia = dia;
    }

    public int getMes()
    {
        return this.mes;
    }

    public void setMes(int mes)
    {
        this.mes = mes;
    }

    public int getAno()
    {
        return this.ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getHora(){
        return this.hora;
    }

    public void setHora(String hora)
    {
        this.hora = hora;
    }

    private int get_dias_mes(int mes,int year){
        if(mes == 4 || mes == 6 || mes == 9 || mes == 11){
            return 30;
        }else if (mes == 2){
            if (( year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))){
                return 29;
            }else{
                return 28;
            }
        }else{
            return 31;
        }
    }


    public int get_convertir_fecha_a_entero(String fecha){
        String[] fecha2 = fecha.split("-");
        String fecha_1 = fecha2[0]+fecha2[1]+fecha2[2];

        return  Integer.parseInt(fecha_1);
    }


    public String get_nombre_del_mes(int mes){
        switch (mes){
            case 1:
                return "ENERO";
            case 2:
                return "FEBRERO";
            case 3:
                return "MARZO";
            case 4:
                return "ABRIL";
            case 5:
                return "MAYO";
            case 6:
                return "JUNIO";
            case 7:
                return "JULIO";
            case 8:
                return "AGOSTO";
            case 9:
                return "SEPTIEMBRE";
            case 10:
                return "OCTUBRE";
            case 11:
                return "NOVIEMBRE";
            case 12:
                return "DICIEMBRE";
        }

        return "";
    }

    public double get_fecha_numerica(String fecha){
        String[] fecha_spl = fecha.split("-");
        String fecha_num_str = "";
        double year_ini = Integer.parseInt(fecha_spl[0]);
        double mes_ini = Integer.parseInt(fecha_spl[1]);
        double dia_ini = Integer.parseInt(fecha_spl[2]);
        System.out.println("CONVIRTIENDO FECHA");
        System.out.println(fecha);

        double fecha_num = (year_ini + (mes_ini / 12.0 ) ) + (dia_ini / 365.0);

        System.out.println("FECHA CONVERTIDA"+fecha_num);
        return fecha_num;
    }


    public String get_fecha_formateada(String fecha){
        String valor_covertido=null;
        String[] buffer = fecha.split("-");

        int year = Integer.parseInt(buffer[2]);
        int mes = Integer.parseInt(buffer[1]);
        int dia = Integer.parseInt(buffer[0]);

        if(year<=9){
            valor_covertido+=""+"000"+year;
        }
        if(mes<=9){
            valor_covertido+=""+"0"+mes;
        }
        if(dia<=9){
            valor_covertido+=""+"0"+dia;
        }


        return valor_covertido;
    }

}

