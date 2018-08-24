
package Frames;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.scene.paint.Color;


public class iTextPDF{
   
    public static void generarPDF(Cliente cliente,Equipo equipo, Empresa empresa,String rutaPDF){
        try {
            Document document = new Document(PageSize.A4,50,50,25,25);
            PdfWriter.getInstance(document, new FileOutputStream(rutaPDF));
            document.open();
            
            Font Bold = new Font(Font.FontFamily.COURIER,20,Font.BOLD);
            Font Normal = new Font(Font.FontFamily.COURIER,11,Font.NORMAL);
            
            if(!empresa.getNombre().equals(null)){
                document.add(getHeader(empresa.getNombre(),Bold,25)); //ATRIBUTOS SON EMPRESA,FUENTE,INTERLIENADO
            }
            if(!empresa.getRutaLogo().equals(null)){
                document.add(getImage(empresa.getRutaLogo(),50,50));
            }
            if(!empresa.getTelefono().equals(null)){
                document.add(getHeader(empresa.getTelefono(),Normal,15));
            }
            if(!empresa.getDireccion().equals(null)){
                document.add(getHeader(empresa.getDireccion(),Normal,15));
            }
            if(!empresa.getPagina().equals(null)){
                document.add(getHeader(empresa.getPagina(),Normal,15));
            }
            if(!empresa.getFacebook().equals(null)){
                document.add(getHeader(empresa.getFacebook(),Normal,15));
            }
            document.add(tablaClientes(cliente));
            document.add(tablaRegistro(equipo));
            
            Bold.setSize(10);
            document.add(footer(empresa.getNotas(),Bold,15));
            
            Bold.setSize(12);
            document.add(getParagraph("_________________", Bold, 65));
            document.add(getParagraph("FIRMA DEL CLIENTE", Bold, 15));
            
            document.close();
            
            
        } catch (Exception e) {
            
        }
    }
    
    
    public static PdfPTable tablaClientes(Cliente cliente){
        
        try {
            PdfPTable table = simpleTable(2,new int[]{6,4},35,100,8);
            
            Font Bold = new Font(Font.FontFamily.COURIER,14,Font.BOLD);
            table.addCell(cell(Bold,"Datos del cliente",2,25));
            
            table.getDefaultCell().setMinimumHeight(30);//ALTO DE CELDA
            Font Normal = new Font(Font.FontFamily.COURIER,12,Font.NORMAL);
            table.addCell(new Phrase("Nombre: "+cliente.getNombre(),Normal));
            table.addCell(new Phrase("Telefono: "+cliente.getTelefono(),Normal));
            table.addCell(new Phrase("Direccion: "+cliente.getDomicilio(),Normal));
            table.addCell(new Phrase("RFC: "+cliente.getRfc(),Normal));
            
            return table;
        } catch (Exception e) {
            return null;
        }
        
    }
    
    public static PdfPTable tablaRegistro(Equipo equipo){
        try {
            PdfPTable table = simpleTable(2,new int[]{6,4},20,100,8);
            
            Font Bold = new Font(Font.FontFamily.COURIER,14,Font.BOLD);
            table.addCell(cell(Bold,"Datos del Registro",2,25));
            
            table.getDefaultCell().setMinimumHeight(30);//ALTO DE CELDA
            Font Normal = new Font(Font.FontFamily.COURIER,12,Font.NORMAL);
            table.addCell(new Phrase("Articulo: "+equipo.getArticulo(),Normal));
            table.addCell(new Phrase("Marca: "+equipo.getMarca(),Normal));
            table.addCell(new Phrase("Modelo: "+equipo.getModelo(),Normal));
            
            int n = equipo.getBateria();
            if(n == 1){
                n = equipo.getCargador();
                if(n ==1){
                    table.addCell(new Phrase("Bateria: SI  Cargador: SI",Normal));
                }
                else{
                    table.addCell(new Phrase("Bateria: SI  Cargador: NO",Normal));
                }
            }
            else{
                n = equipo.getCargador();
                if(n ==1){
                    table.addCell(new Phrase("Bateria: NO  Cargador: SI",Normal));
                }
                else{
                    table.addCell(new Phrase("Bateria : NO  Cargador: NO",Normal));
                }
            }
            PdfPCell cell = new PdfPCell(new Phrase("Problema reportado: "+equipo.getFalla(),Normal));
            cell.setColspan(2);
            cell.setMinimumHeight(50);
            table.addCell(cell);
            
            return table;
        } catch (Exception e) {
            return null;
        }
    }
    
    
    
    
    public static Image getImage(String rutaImagen, int width, int height){
        try {
            Image imagen = Image.getInstance(rutaImagen);
            imagen.scaleAbsolute(width, height);
            imagen.setAlignment(Element.ALIGN_CENTER);
            return imagen;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        
    }
    
    public static PdfPCell cell(Font font, String title, int Colspan, int Height){
        PdfPCell cell = new PdfPCell(new Phrase(title,font));
        cell.setBackgroundColor(new GrayColor(0.85f));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(Colspan);
        cell.setMinimumHeight(Height);
        return cell;
    }
    
    public static PdfPTable simpleTable(int totalColumns, int columnWidths[], int interlineado, int widthPercentage, int fixedHeight){
        try {
            PdfPTable table = new PdfPTable(totalColumns);
            table.setSpacingBefore(interlineado);
            table.setWidthPercentage(widthPercentage);
            table.setWidths(columnWidths);
            table.getDefaultCell().setFixedHeight(fixedHeight);
            
            return table;
        } catch (Exception e) {
            return null;
        }
        
    }
    
    private static Paragraph getHeader(String text, Font font, int interlineado){
        Paragraph p = new Paragraph(interlineado);
        Chunk c = new Chunk();
        c.append(text);
        c.setFont(font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.add(c);
        return p;
    }
    
    private static Paragraph getParagraph(String text, Font font, int interlineado){
        Paragraph p = new Paragraph(interlineado);
        Chunk c = new Chunk();
        c.append(text);
        c.setFont(font);
        p.setAlignment(Element.ALIGN_RIGHT);
        p.add(c);
        return p;
    }
    
    private static Paragraph footer(String text,Font font,int interlineado){
        Paragraph p = new Paragraph(interlineado);
        Chunk c = new Chunk();
        c.append(text);
        c.setFont(font);
        p.setAlignment(Element.ALIGN_BASELINE);
        p.add(c);
        
        return p;
    }

    
}
