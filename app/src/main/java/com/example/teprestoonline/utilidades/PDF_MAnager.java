package com.example.teprestoonline.utilidades;

import android.os.Environment;
import android.util.Log;

import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Prestamo;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class PDF_MAnager {

    private  String archivo_nombre;
    public static final String RUTA_RAIZ = "/storage/emulated/0/PRESTAMOS_APP";
    public static final String RUTA_ARCHIVOS_PDF = "/storage/emulated/0/PRESTAMOS_APP/PDF";
    public static final String RUTA_ARCHIVOS_EST = "/storage/emulated/0/PRESTAMOS_APP/ESTADOS";
    public static final String RUTA_CONTRATOS = "CONTRATOS";
    private Document documento;
    private File archivo_pdf;
    private PdfWriter escritor_pdf;
    private Paragraph parrafo;
    private Font f_encabesado = new Font(Font.getFamilyIndex("TIMES NEW ROMAN"),11,Font.BOLD);
    private Font fs = new Font(Font.getFamilyIndex("TIMES NEW ROMAN"),11,Font.NORMAL);
    private String fecha_dia = new Fecha_utiliti().getFechaSystemaYYMMDD();
    private long hora = new  Fecha_utiliti().getTime();
    private Prestamo prestamo=null;
    private Cliente cliente=null;


    public void set_proceso_generar_contrato(String nombre_archivo,Prestamo p,Cliente cliente){
        cliente=cliente;
        prestamo=p;
        archivo_nombre =  nombre_archivo;

        if(set_crear_archivo_contrato() ){
            if(set_abrir_archivo()){
                try {
                    documento = new Document(PageSize.A4);
                    escritor_pdf = PdfWriter.getInstance(documento, new FileOutputStream(archivo_pdf));
                    documento.open();
                    set_dato_to_archivo();
                    documento.close();
                }catch (Exception e){
                    Log.e("abrir  escritor",e.toString());
                }
            }
        }
    }

    private boolean set_abrir_archivo(){
        try{
            //nadaarch aqui
        }catch (Exception e){
            Log.e("open document",e.toString());
            return false;
        }

        return true;
    }

    private boolean set_cerrar_archivo(){
        try{
            documento.close();
        }catch (Exception e){
            Log.e("close document",e.toString());
        }

        return true;
    }

    private boolean set_crear_archivo_contrato(){
        try {
            File folder = new File(Environment.getExternalStorageDirectory().toString(),
                    RUTA_ARCHIVOS_PDF);

            File folder2 = new File(folder.getAbsolutePath(),
                    RUTA_CONTRATOS);

            if(!folder.exists()){
                folder.mkdirs();
            }
            if(!folder2.exists()){
                folder2.mkdirs();
            }
            archivo_pdf = new File(folder2,archivo_nombre);
        }catch (Exception e){
            Log.e("ERROR CREANDO ARCHIVO",e.toString());
            return false;
        }

        return true;
    }

    private boolean set_dato_to_archivo(){

        try {

            documento.addTitle("Contrato Prestatario");
            documento.addSubject("CONTRATO");
            documento.addAuthor("PrestamosApp");

            parrafo = new Paragraph();

            set_parrafo_hijo(new Paragraph("ACTO AUTENTICO NUMERO "+prestamo.getId(),f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));

            Paragraph parrafito = new Paragraph(get_cuerpo_documento(),fs);

            set_parrafo_hijo(parrafito);

            parrafo.setSpacingBefore(100);
            parrafo.setSpacingAfter(100);

            set_parrafo_hijo(new Paragraph(" ",f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));
            set_parrafo_hijo(new Paragraph(" ",f_encabesado));

            Paragraph parrafito2 = new Paragraph("___________________________________________",f_encabesado);
            parrafito2.setAlignment(Element.ALIGN_JUSTIFIED);
            set_parrafo_hijo(parrafito2);
            set_parrafo_hijo(new Paragraph("Testigo",f_encabesado));

            parrafo.setSpacingBefore(50);
            parrafo.setSpacingAfter(50);

            set_parrafo_hijo(new Paragraph("___________________________________________",f_encabesado));
            set_parrafo_hijo(new Paragraph("Testigo",f_encabesado));

            parrafo.setSpacingBefore(50);
            parrafo.setSpacingAfter(50);

            set_parrafo_hijo(new Paragraph("___________________________________________",f_encabesado));
            set_parrafo_hijo(new Paragraph("Compareciente",f_encabesado));

            parrafo.setSpacingBefore(50);
            parrafo.setSpacingAfter(50);


            set_parrafo_hijo(new Paragraph("___________________________________________",f_encabesado));
            set_parrafo_hijo(new Paragraph("Notario Público",f_encabesado));

            documento.add(parrafo);

        } catch (DocumentException e) {
            Log.e("ERROR AGREGANDO PARRAFO",e.toString());
            return false;
        }

        return true;
    }

    private void set_parrafo_hijo(Paragraph parrafo_hijo){
        parrafo_hijo.setAlignment(Element.ALIGN_CENTER);
        parrafo.add(parrafo_hijo);

    }

    private void set_parrafo(Paragraph parrafo,String text) throws DocumentException {
        parrafo = new Paragraph(text,fs);
        parrafo.setSpacingAfter(5);
        parrafo.setSpacingBefore(5);
        documento.add(parrafo);
    }

    private String get_cuerpo_documento(){
        Fecha_utiliti fecha = new Fecha_utiliti();
        Numeros_a_letras nm = new Numeros_a_letras();

        return  "En la ciudad y municipio de __________________, Provincia ________________, República Dominicana, a los  ( "
                +fecha.getDia()+" ) días del "
                +"mes de "+fecha.get_nombre_del_mes(fecha.getMes())+" del año ( " + fecha.getAno()+ " ), " +
                "siendo las ( " + fecha.getHora() + " ) horas del dia en transcurso, por ante mí "
                +"_____________________________, dominicano, mayor de edad, de estado civil _________________, Notario Público. "
                +"de los del número para el municipio de _________, inscrito en el Colegio  _________________________, domiciliado y "
                +"residente en esta Ciudad y estudio profesional abierto en la casa No.______ de la calle __________________, "
                +"de esta Ciudad, debidamente asistido por los señores ___________________________ y _____________________   , "
                +"dominicanos, mayores de edad, de estado civil ________________ y ________________, de profesión _______ y _________, "
                +"portadores de las cédulas de identidad y electoral Nos. ______________________ y ________________________, "
                +"ambos domiciliados y residentes en la ciudad de Santo Domingo, testigos instrumentales requeridos al efecto, libres de tachas "
                +"y excepciones, compareció libre y voluntariamente el señor "
                + cliente.getPersona().getNombres()
                + " , " + cliente.getPersona().getApellidos() + " dominicano, mayor de edad, de estado civil "
                +"___________________, de ocupación _________________________, portador de la cedula de identidad y "
                +"electoral No. "+ cliente.getPersona().getIdentificacion() + " , domiciliado y residente en "
                + cliente.getPersona().getDireccion().get_direccion_unificada()
                + " de esta Ciudad, para que haga constar en un acto auténtico, como al efecto "
                +"lo hago constar, lo siguiente: PRIMERO: Que DEBE Y PAGARA al señor ____________________, dominicano, mayor de edad, "
                +"soltero, Estudiante, portador de cédula de identidad y electoral ________________, domiciliado y residente en la casa No.___, "
                +" de la calle _______, del Residencial ____________, del Municipio ____________, la suma de RD$ "+prestamo.getRestante()
                +" ("+nm.Convertir(""+prestamo.getRestante(),true)
                +" PESOS CON 00/100), dinero que ha recibido de manos del  "
                +"señor _____________________, en calidad de préstamo; SEGUNDO: el señor "
                + cliente.getPersona().getNombres()
                + " , " + cliente.getPersona().getApellidos() +", se compromete a pagar en manos del señor _____________,  un "
                +"interés de un "+prestamo.getTasa()+"% ( "
                + nm.Convertir(""+prestamo.getTasa(),true) +
                " POR CIENTO)  por la suma de "+prestamo.getMonto_financiado()
                +" , dinero adeudada; TERCERO: El señor "+  cliente.getPersona().getNombres()
                + " , " + cliente.getPersona().getApellidos()+" se compromete a "
                +"pagar en manos del señor ________________, la suma adeudada de capital y los intereses generados en un término de  "
                +"______________________ (             ) _________________, contados a partir de la firma del presente acto con  "
                +"vencimiento el _______________ (      ) del mes de  _______________________ del año "
                +"_______________________________ (            ). " +
                "Mediante el pago de "+ prestamo.getCantidad_cuotas()  + " cuotas  "
                +" de RD$ "+ prestamo.getCuota()+ " ( "+ prestamo.getCuota() + " PESOS CON /100),"
                +"cada una; CUARTO: Que reconoce el derecho que le asiste a el señor _________________,  de proceder a ejecutar la obligación "
                +"asumida y reconocida en el presente acto, en el caso de que una vez vencido el término antes indicado no se haya "
                +"realizado el pago de la suma de capital señalada y/o no se haya cumplido con la obligación del pago de las cuotas "
                +"de capital e intereses generados por la suma prestada y en tal virtud, RECONOCE, CONSIENTE Y ACEPTA "
                +"que el presente acto tiene la fuerza ejecutoria del ART. 545 del CODIGO PROCEDIMIENTO CIVIL "
                +"DOMINICANO; SEXTO: me declara el señor "+"________________________________________________________," +
                " que para el fiel cumplimiento de la presente "
                +"obligación de pago quedan afectados todos sus bienes, muebles e inmuebles, habidos y por haber. HECHO Y "
                +"FIRMADO ha sido el presente acto en mi estudio, en la fecha y hora indicadas, el cual fue leído por mí en voz alta "
                +"al compareciente en presencia de los testigos indicados, quienes también lo leyeron por sí mismos, y me declararon "
                +"estar conformes con el mismo, y tras aprobarlo, lo firmaron ante mí, y junto conmigo NOTARIO PUBLICO  "
                +"infrascrito, de todo lo cual doy fe y verdadero testimonio.  ";

    }


}
