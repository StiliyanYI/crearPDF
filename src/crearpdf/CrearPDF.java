/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package crearpdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author alumno
 */
public class CrearPDF {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection conexion;
        Statement sentencia;
        ResultSet resultado;
        conexion = null;
        conexion = bd.Conexion.mySQL("bdagenda2", "root", "");
        if (conexion == null) {
            System.out.println("No se ha conectado con la BD");
            System.exit(0);
        }

        try {
            //Se crea el documento
            Document documento = new Document();
            try {
                //Se crea el outputstream para el fichero donde queremos el pdf
                FileOutputStream ficheroPdf = new FileOutputStream("fichero.pdf");
                //Se asocia el documento outputstream y se indica el espacio entre lineas sera 20, se debe hacer antes de abrir el documento
                PdfWriter.getInstance(documento, ficheroPdf).setInitialLeading(20);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CrearPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(CrearPDF.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Se abre el documento
            documento.open();
            //Añadir titulo
            Paragraph titulo = new Paragraph("Clasificacion ATP",
                    FontFactory.getFont("arial", // fuente
                            22, // tamaño
                            Font.ITALIC, // estilo
                            BaseColor.BLUE)); // color
            titulo.setAlignment(Chunk.ALIGN_CENTER);
            documento.add(titulo);
            //Añadir una linea en blanco
            documento.add(Chunk.NEWLINE);
            //Añadir párrafos
            documento.add(new Paragraph(
                    "El ranking ATP es una clasificación mundial de tenistas profesionales de la Asociación "
                    + "de Tenistas Profesionales. Se actualiza cada semana y abarca los resultados de las últimas 52 semanas. "
                    + "Se utiliza para seleccionar a los habilitados en cada torneo y a los cabezas de serie, el máximo galardón para "
                    + "cualquier tenista es ser considerado entre los 5 mejores del mundo en el ranking ATP."));
            //Añadir una linea en blanco
            documento.add(Chunk.NEWLINE);
            //Añadir una imagen
            Image foto = Image.getInstance("src/img/logoATP.png");
            foto.scaleToFit(100, 100);
            foto.setAlignment(Chunk.ALIGN_MIDDLE);
            documento.add(foto);
            //Añadir una linea en blanco
            documento.add(Chunk.NEWLINE);
            //Añadir una tabla
            float[] anchoColumnas={3, 2, 1};
            PdfPTable tabla = new PdfPTable(anchoColumnas);
            PdfPCell celda = new PdfPCell(new Phrase("Nombre"));
            celda.setBackgroundColor(GrayColor.PINK);
            tabla.addCell(celda);
            celda.setPhrase(new Phrase("Fecha de nacimiento"));
            tabla.addCell(celda);
            celda.setPhrase(new Phrase("Foto"));
            tabla.addCell(celda);
            
            //Crear tabla usando la bd, nombres y fechas
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery("Select * from tagenda;");
            byte[] bytesFoto;
            LocalDate dateFecha;
            String stringFecha;
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy");
            while (resultado.next()) {                
                tabla.addCell(resultado.getString("nombre"));
                stringFecha = resultado.getString("fecha_nac");
                dateFecha = LocalDate.parse(stringFecha, DateTimeFormatter.ISO_DATE);
                tabla.addCell(formatoFecha.format(dateFecha));
                bytesFoto = resultado.getBytes("Foto");
                Image imageFoto = Image.getInstance(bytesFoto);
                tabla.addCell(imageFoto);
            }
            
            
            /****************
            tabla.addCell("1");
            tabla.addCell("Djokovic, Novac");
            tabla.addCell("15.150");
            tabla.addCell("2");
            tabla.addCell("Murray, Andy");
            tabla.addCell("7.925");
            tabla.addCell("3");
            tabla.addCell("Federer, Roger");
            tabla.addCell("7.535");
            ******************/
            documento.add(tabla);
            //Se cierra el documento y escribe, si no se cierra no escribe nada
            documento.close();
        } catch (DocumentException ex) {
            Logger.getLogger(CrearPDF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CrearPDF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CrearPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
