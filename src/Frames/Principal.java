
package Frames;

import SQL.Conexion;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Label;
import static java.awt.SystemColor.desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author hajoc
 */
public class Principal extends javax.swing.JFrame{

    private String ventanaOrigen = "Inicio";
    private String rutaLogo = "";
    private boolean registrosVacios;
    private boolean tablaClientes;
    
    public Principal(){
        initComponents();
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        cambiarVentana("Inicio");
        cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");

        checarRegistrosVacios();
        
    }
    
    void checarRegistrosVacios(){
        if(jtableRegistros.getRowCount()!=0){
            int registrosTotales = jtableRegistros.getRowCount()-1;
            int idRegistro = Integer.parseInt(String.valueOf(jtableRegistros.getValueAt(registrosTotales, 1)));
            cargarRegistro(idRegistro);
            jtableRegistros.setRowSelectionInterval(registrosTotales, registrosTotales);
            registrosVacios = false;
        }
        else{
            registrosVacios = true;
        }
    }

    private void cargarTablaRegistros(String consulta){

        String titulos[] = {"Estado","Registro"};
        String valor;
        int registro;
        JLabel etiqueta = new JLabel();

        DefaultTableModel modelo = new DefaultTableModel();

        jtableRegistros.setDefaultRenderer(Object.class, new ImageRender());
        jtableRegistros.setRowHeight(20);

        modelo.setColumnIdentifiers(titulos);

        try{
            ResultSet rs = Conexion.selectFrom(consulta);

            while(rs.next())
            {
                valor = rs.getString("Estado");
                registro = rs.getInt("ID_Registro");

                switch(valor)
                {
                    case "Sin reparar":
                    {
                        etiqueta = new JLabel(new ImageIcon(getClass().getResource("/Icons/circuloRojo.png")));
                        break;
                    }
                    case "En reparacion":
                    {
                        etiqueta = new JLabel(new ImageIcon(getClass().getResource("/Icons/circuloAmarillo.png")));
                        break;
                    }
                    case "Listo":
                    {
                        etiqueta = new JLabel(new ImageIcon(getClass().getResource("/Icons/circuloVerde.png")));
                        break;
                    }
                    case "Entregada":
                    {
                        etiqueta = new JLabel(new ImageIcon(getClass().getResource("/Icons/circuloAzul.png")));
                        break;
                    }
                }
                modelo.addRow(new Object[]{etiqueta, registro});
            }
            jtableRegistros.setModel(modelo);
            TableColumnModel columnModel = jtableRegistros.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(0);

        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"No fue posible conectar a la base de datos");
        }
    }

    void cargarTablaUsuarios(String consulta){
        String titulos[] = {"Id","Nombre"};
        String nombre;
        int id;

        DefaultTableModel modelo = new DefaultTableModel();
        jtableRegistros.setRowHeight(20);
        modelo.setColumnIdentifiers(titulos);

        try{
            ResultSet rs = Conexion.selectFrom(consulta);

            while(rs.next()){
                nombre = rs.getString("Nombre");
                id = rs.getInt("ID_Cliente");
                modelo.addRow(new Object[]{id,nombre});
            }
            jtableRegistros.setModel(modelo);
            //CON ESTO HACEMOS QUE NO SEA VISIBLE LA COLUMNA SELECCIONADA (ID_Cliente)
            jtableRegistros.getColumnModel().getColumn(0).setMaxWidth(0);
            jtableRegistros.getColumnModel().getColumn(0).setMinWidth(0);
            jtableRegistros.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
            jtableRegistros.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);

        }catch (Exception e){
            System.out.println(e);
        }
    }

    void cargarCliente(int idCliente){

        try{
            String consulta = "SELECT Nombre, Domicilio, RFC, Email, Telefono "
                    + "FROM Clientes WHERE ID_Cliente = "+ idCliente+ ";";

            ResultSet rs = Conexion.selectFrom(consulta);

            while(rs.next()){
                jtfNombre.setText(rs.getString("Nombre"));
                jtaDomicilio.setText(rs.getString("Domicilio"));
                jtfRFC.setText(rs.getString("RFC"));
                jtfEmail.setText(rs.getString("Email"));
                jtfTelefono.setText(rs.getString("Telefono"));
                jtfFecha.setText("");
            }

        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"No fue posible conectar a la base de datos");
        }
    }


    void cambiarVentana(String ventana){
        switch(ventana)
        {
            case "Inicio":
            {
                jpRegistros.setVisible(false);
                jpInicio.setVisible(true);
                jpTutorial.setVisible(false);
                jpAjustes.setVisible(false);

                ventanaOrigen = "Inicio";
                jpmInicio.setBackground(new java.awt.Color(225, 225, 225));
                break;
            }
            case "Registros":
            {
                jpRegistros.setVisible(true);
                jpInicio.setVisible(false);
                jpTutorial.setVisible(false);
                jpAjustes.setVisible(false);
                jpOpc1.setVisible(true);
                jpOpc2.setVisible(false);

                if(registrosVacios){
                    
                    jpFormulario.setVisible(false);
                    jpTutorial.setVisible(true);
                }
                else{
                    jpTutorial.setVisible(false);
                    jpFormulario.setVisible(true);
                    
                }
                if(tablaClientes){
                    jpTutorial.setVisible(false);
                    jpFormulario.setVisible(true);
                }

                ventanaOrigen = "Registros";
                jpmRegistros.setBackground(new java.awt.Color(225, 225, 225));
                break;
            }
            case "Ajustes":
            {
                jpRegistros.setVisible(false);
                jpInicio.setVisible(false);
                jpTutorial.setVisible(false);
                jpAjustes.setVisible(true);

                ventanaOrigen = "Ajustes";
                cambiarVentanaAjustes("blanco");
                jpmAjustes.setBackground(new java.awt.Color(225, 225, 225));
                break;
            }
        }

    }
    
    void cambiarVentanaAjustes(String opcion){
        switch(opcion){
            case "blanco":
            {
                jpOpcEmpresa.setVisible(false);
                jpOpcBaseDatos.setVisible(false);
                break;
            }
            case "baseDatos":
            {
                jpOpcEmpresa.setVisible(false);
                jpOpcBaseDatos.setVisible(true);
                break;
            }
            case "empresa":
            {
                jpOpcEmpresa.setVisible(true);
                jpOpcBaseDatos.setVisible(false);
                cargarDatosEmpresa();
                break;
            }
            case "licencia":
            {
                break;
            }
            case "Usuarios":
            {
                break;
            }
        }
    }

    void restablecerCampos(boolean clientes){
        
        if(!clientes){//SI NO ESTAMOS EN LA TABLA CLIENTES ENTONCES BORRAMOS
            jtfNombre.setText("");
            jtaDomicilio.setText("");

            jtfRFC.setText("");
            jtfEmail.setText("");
            jtfTelefono.setText("");
        }
        
        jtfArticulo.setText("");
        jtfMarca.setText("");
        jtfModelo.setText("");
        jcbBateria.setSelected(false);
        jcbCargador.setSelected(false);
        jcbPassword.setSelected(true);
        jtfPassword.setEnabled(true);
        jtfPassword.setText("");

        jtaFalla.setText("");
        jtaNotas.setText("");
        jcbEstado.setSelectedIndex(0);
        jtfCosto.setText("");
        jtfReparador.setText("");

        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DATE));
        String month = Integer.toString((c.get(Calendar.MONTH)+1));
        String year = Integer.toString((c.get(Calendar.YEAR)));

        String fecha = year + "-" + month + "-" + day;
        jtfFecha.setText(fecha);

        jtfNombre.setBorder(null);
        jsNombre.setForeground(new java.awt.Color(160,160,160));

        jtfTelefono.setBorder(null);
        jsTelefono.setForeground(new java.awt.Color(160,160,160));

        jtfArticulo.setBorder(null);
        jsArticulo.setForeground(new java.awt.Color(160,160,160));

        jtfPassword.setBorder(null);
        jsPassword.setForeground(new java.awt.Color(160,160,160));

        jtfMarca.setBorder(null);
        jsMarca.setForeground(new java.awt.Color(160,160,160));

        jtfModelo.setBorder(null);
        jsModelo.setForeground(new java.awt.Color(160,160,160));
    }
    
    Cliente obtenerCliente()
    {
        String nombre = jtfNombre.getText();
        String domicilio = jtaDomicilio.getText();
        String rfc = jtfRFC.getText();
        String email = jtfEmail.getText();
        String telefono = jtfTelefono.getText();
        
        Cliente cliente = new Cliente(nombre,domicilio,rfc,email,telefono);
        return cliente;
    }
    
    Equipo obtenerEquipo()
    {
        String articulo = jtfArticulo.getText();
        String marca = jtfMarca.getText();
        String modelo = jtfModelo.getText();
        String password = jtfPassword.getText();

        String falla = jtaFalla.getText();
        String notas = jtaNotas.getText();
        String estado = jcbEstado.getSelectedItem().toString();
        String reparador = jtfReparador.getText();
        String costo = jtfCosto.getText();

        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DATE));
        String month = Integer.toString((c.get(Calendar.MONTH)+1));
        String year = Integer.toString((c.get(Calendar.YEAR)));

        String fecha = year + "-" + month + "-" + day;

        int bateria = 0;
        if(jcbBateria.isSelected()) bateria = 1;

        int cargador = 0;
        if(jcbCargador.isSelected()) cargador = 1;
        
        Equipo equipo = new Equipo(articulo,marca,modelo,password,falla,
                    notas,estado,reparador,bateria,cargador,costo,fecha);
        
        return equipo;
    }

    boolean insertarRegistro(boolean actualizarRegistro){

        Cliente cliente = obtenerCliente();
        Equipo equipo = obtenerEquipo();

        if(!cliente.getNombre().isEmpty() && !cliente.getTelefono().isEmpty() && 
                !equipo.getArticulo().isEmpty() && !equipo.getPassword().isEmpty() && 
                !equipo.getMarca().isEmpty() && !equipo.getModelo().isEmpty())
        {
            int fila, idRegistro;

            if(actualizarRegistro){
                fila = jtableRegistros.getSelectedRow();
                idRegistro = Integer.parseInt(String.valueOf(jtableRegistros.getValueAt(fila, 1)));
                Conexion.updateRegistro(cliente, equipo, idRegistro);
                cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");
                jtableRegistros.setRowSelectionInterval(fila, fila);

            }else{
                Conexion.insertInto(cliente,equipo);
                cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");
                fila = jtableRegistros.getRowCount()-1;
                jtableRegistros.setRowSelectionInterval(fila, fila);
                idRegistro = Integer.parseInt(String.valueOf(jtableRegistros.getValueAt(fila, 1)));
            }

            restablecerCampos(false);
            cargarRegistro(idRegistro);
            jpOpc2.setVisible(false);
            jpOpc1.setVisible(true);
            return true;
        }
        else{

            if(cliente.getNombre().isEmpty()) {
                jtfNombre.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsNombre.setForeground(new java.awt.Color(200,64,104));
            }
            if(cliente.getTelefono().isEmpty()){
                jtfTelefono.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsTelefono.setForeground(new java.awt.Color(200,64,104));
            }
            if(equipo.getArticulo().isEmpty()){
                jtfArticulo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsArticulo.setForeground(new java.awt.Color(200,64,104));
            }
            if(equipo.getPassword().isEmpty()){
                jtfPassword.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsPassword.setForeground(new java.awt.Color(200,64,104));
            }
            if(equipo.getMarca().isEmpty()){
                jtfMarca.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsMarca.setForeground(new java.awt.Color(200,64,104));
            }
            if(equipo.getModelo().isEmpty()){
                jtfModelo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsModelo.setForeground(new java.awt.Color(200,64,104));
            }
            return false;
        }

    }

    void cargarRegistro(int idRegistro){

        DefaultTableModel tm = (DefaultTableModel) jtableRegistros.getModel();

        String query = "SELECT Nombre,Domicilio,RFC,Email,Telefono,Articulo,Marca,Modelo,"
                     + "Falla,Notas,Password,Reparador,Bateria,Cargador,Estado,Costo,Fecha "
                     + "FROM Clientes "
                     + "INNER JOIN Registros ON Clientes.ID_Cliente = Registros.FK_Clientes "
                     + "WHERE ID_Registro = " + idRegistro + ";";

        try {

            ResultSet rs = Conexion.selectFrom(query);

            while(rs.next()){

                jtfNombre.setText(rs.getString("Nombre"));
                jtaDomicilio.setText(rs.getString("Domicilio"));
                jtfRFC.setText(rs.getString("RFC"));
                jtfEmail.setText(rs.getString("Email"));
                jtfTelefono.setText(rs.getString("Telefono"));

                jtfArticulo.setText(rs.getString("Articulo"));
                jtfMarca.setText(rs.getString("Marca"));
                jtfModelo.setText(rs.getString("Modelo"));
                jtaFalla.setText(rs.getString("Falla"));
                jtaNotas.setText(rs.getString("Notas"));
                jtfReparador.setText(rs.getString("Reparador"));
                jtfCosto.setText((rs.getString("Costo")));
                jtfFecha.setText(rs.getString("Fecha"));

                jtfPassword.setText(rs.getString("Password"));
                if(jtfPassword.getText().equals("Sin contraseña")){
                    jcbPassword.setSelected(false);
                    jtfPassword.setEnabled(false);
                }else {
                    jcbPassword.setSelected(true);
                    jtfPassword.setEnabled(true);
                }

                int n;
                n = rs.getInt("Bateria");
                if(n == 1) jcbBateria.setSelected(true);
                else jcbBateria.setSelected(false);


                n = rs.getInt("Cargador");
                if(n == 1) jcbCargador.setSelected(true);
                else jcbCargador.setSelected(false);


                String estado = rs.getString("Estado");
                switch(estado){
                    case "Sin reparar":
                    {
                        jcbEstado.setSelectedIndex(0);
                        break;
                    }
                    case "En reparacion":
                    {
                        jcbEstado.setSelectedIndex(1);
                        break;
                    }
                    case "Listo":
                    {
                        jcbEstado.setSelectedIndex(2);
                        break;
                    }
                    case "Entregada":
                    {
                        jcbEstado.setSelectedIndex(3);
                        break;
                    }
                }


            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void resetearBaseDatos(){
        reproducirAudio("Click");
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog (null, "Esta accion "
                + "no se puede deshacer\nEstas seguro?","Warning",dialogButton);

        if(dialogResult == JOptionPane.YES_OPTION){
            if(Conexion.queryUpdate("DROP DATABASE reparaciones;")){
                Conexion.createDatabase("CREATE DATABASE IF NOT EXISTS reparaciones");

                String query = "CREATE TABLE Clientes(" +
                    "ID_Cliente int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "Nombre VARCHAR(255)," +
                    "Domicilio VARCHAR(255)," +
                    "RFC VARCHAR(255)," +
                    "Email VARCHAR(255)," +
                    "Telefono VARCHAR(255));";
                Conexion.queryUpdate(query);

                query = "CREATE TABLE Registros(" +
                    "ID_Registro int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "FK_Clientes int(11) NOT NULL," +
                    "Fecha DATE," +
                    "Articulo VARCHAR(255)," +
                    "Marca VARCHAR(255)," +
                    "Modelo VARCHAR(255)," +
                    "Bateria tinyint(1)," +
                    "Cargador tinyint(1)," +
                    "Password VARCHAR(255)," +
                    "Falla VARCHAR(255)," +
                    "Notas VARCHAR(255),"+
                    "Estado VARCHAR(255)," +
                    "Costo VARCHAR(255)," +
                    "Reparador VARCHAR(255)," +
                    "FOREIGN KEY (FK_Clientes) REFERENCES Clientes(ID_Cliente));";

                Conexion.queryUpdate(query);

                cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");
                restablecerCampos(false);
                registrosVacios = true;

                reproducirAudio("Guardar");
                JOptionPane.showMessageDialog(null,"Base de datos restaurada");
            }else{
                reproducirAudio("Error");
                JOptionPane.showMessageDialog(null,"Ocurrio un error al restaurar");
            }
        }
    }


    void eliminarRegistro(){

        int fila = jtableRegistros.getSelectedRow();
        if(fila != -1){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Estas seguro "
                    + "de eliminar este registro?","Warning",dialogButton);

            if(dialogResult == JOptionPane.YES_OPTION){
                int id = Integer.parseInt(String.valueOf(jtableRegistros.getValueAt(fila, 1)));
                String query = "DELETE FROM Registros WHERE ID_Registro = " + id + ";";
                Conexion.queryExecute(query);
                restablecerCampos(false);
                cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");

                reproducirAudio("Eliminar");

                if(jtableRegistros.getRowCount() == 0){ //SI NOS QUEDAMOS SIN REGISTROS MOSTRAR VENTANA TUTORIAL
                    registrosVacios = true;
                    tablaClientes = false;
                    cambiarVentana("Registros");
                }
            }

        }
        else{
            reproducirAudio("Error");
            JOptionPane.showMessageDialog(null,"No tienes ningun registro seleccionado");
        }
    }
    
    boolean actualizarCliente(){
        
        String nombre = jtfNombre.getText();
        String domicilio = jtaDomicilio.getText();
        String rfc = jtfRFC.getText();
        String email = jtfEmail.getText();
        String telefono = jtfTelefono.getText();
        
        if(!nombre.isEmpty() && !telefono.isEmpty())
        {
            Cliente cliente = new Cliente(nombre,domicilio,rfc,email,telefono);
        
            int fila = jtableRegistros.getSelectedRow();
            int idRegistro = Integer.parseInt(String.valueOf(jtableRegistros.getValueAt(fila, 0)));
            Conexion.updateCliente(cliente, idRegistro);
            cargarTablaUsuarios("SELECT ID_Cliente, Nombre FROM Clientes;");
            jtableRegistros.setRowSelectionInterval(fila, fila);
            return true;
        }
        else
        {
            if(nombre.isEmpty()) {
                jtfNombre.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsNombre.setForeground(new java.awt.Color(200,64,104));
            }
            if(telefono.isEmpty()){
                jtfTelefono.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200,64,104)));
                jsTelefono.setForeground(new java.awt.Color(200,64,104));
            }
            return false;
        }
            
        
    }
    
    void cargarDatosEmpresa(){
        Empresa datosEmpresa = obtenerEmpresa(true);
        
        if(datosEmpresa != null){
            jtxtNombreEmpresa.setText(datosEmpresa.getNombre());
            jtxtDireccionEmpresa.setText(datosEmpresa.getDireccion());
            jtxtTelefonoEmpresa.setText(datosEmpresa.getTelefono());
            jtxtEmailEmpresa.setText(datosEmpresa.getCorreo());
            jtxtPaginaEmpresa.setText(datosEmpresa.getPagina());
            jtxtFacebookEmpresa.setText(datosEmpresa.getFacebook());
            rutaLogo = datosEmpresa.getRutaLogo();
            File file = new File(rutaLogo);
            jlbLogo.setIcon(cargarIcono(file));
            jtaAnotacionesEmpresa.setText(datosEmpresa.getNotas());
        }
        
    }
    
    Empresa obtenerEmpresa(boolean ventanaAjustes){
        String datos[] = new String [8];
        Empresa empresa;
        try {
            File archivo = new File ("\\datosEmpresa.txt");
            FileReader fr = new FileReader (archivo);
            BufferedReader br = new BufferedReader(fr);
            
            int contador = 0;
            String linea;
            
            while((linea=br.readLine())!=null){
                if(contador < 7){
                    datos[contador] = linea;
                    contador++;
                    if(contador == 7) datos[contador] = "";
                    
                }else datos[contador] += linea;
            }
            empresa = new Empresa(datos[0],datos[1],datos[2],datos[3],datos[4],datos[5],datos[6],datos[7]);
            
            fr.close();
            return empresa;
        }
        catch(Exception e){
            System.out.println(e);
            if(!ventanaAjustes)
                 JOptionPane.showMessageDialog(null,"Para poder imprimir su registro necesita\n"
                         + "configurar los datos de la empresa en la ventana Ajustes/Empresa");
            return null;
        }
        
    }
    
    void escribirDatosEmpresa(){
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        
        try
        {
            fichero = new FileWriter("\\datosEmpresa.txt");
            pw = new PrintWriter(fichero);

            pw.println(jtxtNombreEmpresa.getText());
            pw.println(jtxtDireccionEmpresa.getText());
            pw.println(jtxtTelefonoEmpresa.getText());
            pw.println(jtxtEmailEmpresa.getText());
            pw.println(jtxtPaginaEmpresa.getText());
            pw.println(jtxtFacebookEmpresa.getText());
            pw.println(rutaLogo);
            pw.print(jtaAnotacionesEmpresa.getText());
            
            
            
        } catch (Exception e) {
            System.out.println(e);
            reproducirAudio("Error");
        } finally {
           try{
                if (null != fichero){
                    fichero.close();
                    reproducirAudio("Guardar");
                    JOptionPane.showMessageDialog(null,"Datos guardados");
                }
           } catch (Exception e2) {
               System.out.println(e2);
           }
        }
    }
    
    
    void imprimirRegistro(){
        //OBTENEMOS TODOS LOS DATOS PARA EL REGISTRO
        Cliente cliente = obtenerCliente();
        Equipo equipo = obtenerEquipo();
        Empresa empresa = obtenerEmpresa(false);
        
        if(empresa != null){ //SI YA REGISTRO LOS DATOS DE LA EMPRESA ENTONCES PROCEDEMOS
            //CREAMOS NUESTRO PDF
            String rutaPDF = "\\archivo.pdf";
            iTextPDF.generarPDF(cliente, equipo, empresa,rutaPDF);
            //ABRIMOS EL ARCHIVO PARA MANDARLO A IMPRIMIR DESDE AHI
            try{
                File objetofile = new File (rutaPDF);
                Desktop.getDesktop().open(objetofile);
            }catch (IOException ex) {
                System.out.println(ex);
            }
        }
        
    }
    
    Icon cargarIcono(File file){
        FileInputStream entrada;
        byte[] bytesImg = new byte[1024*100];
        try {
            entrada = new FileInputStream(file);
            entrada.read(bytesImg);
        } catch (Exception e) {
            System.out.println(e);
        }
        ImageIcon image = new ImageIcon(bytesImg);
        Icon icon = new ImageIcon(image.getImage().getScaledInstance(jlbLogo.getWidth(), jlbLogo.getHeight(), Image.DEFAULT));
        return icon;
    }
    
    void buscarImagen(){
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Imagenes","JPG","PNG");
        fc.setFileFilter(filtro);
        
        File file;
        byte[] bytesImg;
        
        if(fc.showDialog(null, "Seleccionar") == JFileChooser.APPROVE_OPTION){
            file = fc.getSelectedFile();
            rutaLogo = fc.getSelectedFile().getAbsolutePath();
            if(file.canRead()){
                if(file.getName().endsWith("JPG")||file.getName().endsWith("PNG")||file.getName().endsWith("jpg")||file.getName().endsWith("png"))
                {
                    jlbLogo.setIcon(cargarIcono(file));
                }else{
                    JOptionPane.showMessageDialog(null, "Seleccione un archivo de imagen.");
                }
            }
        }
    }
    
    
    
    void reproducirAudio(String audio){
        try {
            Clip sonido = AudioSystem.getClip();
            sonido.open(AudioSystem.getAudioInputStream(getClass().getResource("/Sounds/"+audio+".wav")));
            sonido.start();

        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpBackFrame = new javax.swing.JPanel();
        jpTop = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jpmInicio = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jpmRegistros = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jpmCalculadora = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jpmAjustes = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jpMain = new javax.swing.JPanel();
        jpRegistros = new javax.swing.JPanel();
        jpFormulario = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jtaDomicilio = new javax.swing.JTextArea();
        jLabel54 = new javax.swing.JLabel();
        jtfNombre = new javax.swing.JTextField();
        jsNombre = new javax.swing.JSeparator();
        jLabel57 = new javax.swing.JLabel();
        jtfTelefono = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jtfRFC = new javax.swing.JTextField();
        jSeparator14 = new javax.swing.JSeparator();
        jLabel56 = new javax.swing.JLabel();
        jtfEmail = new javax.swing.JTextField();
        jSeparator15 = new javax.swing.JSeparator();
        jsTelefono = new javax.swing.JSeparator();
        jLabel52 = new javax.swing.JLabel();
        jpFormularioEquipo = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jtfArticulo = new javax.swing.JTextField();
        jsArticulo = new javax.swing.JSeparator();
        jLabel59 = new javax.swing.JLabel();
        jtfMarca = new javax.swing.JTextField();
        jsMarca = new javax.swing.JSeparator();
        jLabel60 = new javax.swing.JLabel();
        jtfModelo = new javax.swing.JTextField();
        jsModelo = new javax.swing.JSeparator();
        jcbCargador = new javax.swing.JCheckBox();
        jcbPassword = new javax.swing.JCheckBox();
        jtfPassword = new javax.swing.JTextField();
        jsPassword = new javax.swing.JSeparator();
        jLabel64 = new javax.swing.JLabel();
        jcbEstado = new javax.swing.JComboBox<>();
        jLabel66 = new javax.swing.JLabel();
        jtfReparador = new javax.swing.JTextField();
        jcbBateria = new javax.swing.JCheckBox();
        jScrollPane8 = new javax.swing.JScrollPane();
        jtaFalla = new javax.swing.JTextArea();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jtaNotas = new javax.swing.JTextArea();
        jLabel69 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jtfFecha = new javax.swing.JTextField();
        jtfCosto = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        jpTutorial = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtableRegistros = new javax.swing.JTable();
        jpOpciones = new javax.swing.JPanel();
        jpOpc1 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jpOpc2 = new javax.swing.JPanel();
        jpGuardar = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jpCancelar = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jpBusqueda = new javax.swing.JPanel();
        jtfBuscar = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jpAjustes = new javax.swing.JPanel();
        jpBaseDatos = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jpEmpresa = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jpUsuarios = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jpLicencia = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jpOpcAjustes = new javax.swing.JPanel();
        jpOpcEmpresa = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jtxtFacebookEmpresa = new javax.swing.JTextField();
        jtxtDireccionEmpresa = new javax.swing.JTextField();
        jtxtTelefonoEmpresa = new javax.swing.JTextField();
        jtxtEmailEmpresa = new javax.swing.JTextField();
        jtxtPaginaEmpresa = new javax.swing.JTextField();
        jtxtNombreEmpresa = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaAnotacionesEmpresa = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel41 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jlbLogo = new javax.swing.JLabel();
        jpGuardar2 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jpOpcBaseDatos = new javax.swing.JPanel();
        jpServidor = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jpImportar = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jpRespaldar = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jpBorrar = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jpInicio = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1000, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpBackFrame.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpTop.setBackground(new java.awt.Color(255, 255, 255));
        jpTop.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Decker", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("SoftRepair");
        jpTop.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 140, 40));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Maintenance_40px.png"))); // NOI18N
        jpTop.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jpmInicio.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpmInicioMouseMoved(evt);
            }
        });
        jpmInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpmInicioMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpmInicioMouseExited(evt);
            }
        });
        jpmInicio.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Home_30px.png"))); // NOI18N
        jpmInicio.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 40));

        jLabel4.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setText("Inicio");
        jpmInicio.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 4, 50, 30));

        jpTop.add(jpmInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 130, 40));

        jpmRegistros.setBackground(new java.awt.Color(255, 255, 255));
        jpmRegistros.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpmRegistrosMouseMoved(evt);
            }
        });
        jpmRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpmRegistrosMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpmRegistrosMouseExited(evt);
            }
        });
        jpmRegistros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Computer_30px.png"))); // NOI18N
        jpmRegistros.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 40));

        jLabel22.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(153, 153, 153));
        jLabel22.setText("Registros");
        jpmRegistros.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 4, 80, 30));

        jpTop.add(jpmRegistros, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 160, -1));

        jpmCalculadora.setBackground(new java.awt.Color(255, 255, 255));
        jpmCalculadora.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpmCalculadoraMouseMoved(evt);
            }
        });
        jpmCalculadora.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpmCalculadoraMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpmCalculadoraMouseExited(evt);
            }
        });
        jpmCalculadora.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Calculator_25px_1.png"))); // NOI18N
        jpmCalculadora.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 40));

        jLabel14.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(153, 153, 153));
        jLabel14.setText("Calculadora");
        jpmCalculadora.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 110, 40));

        jpTop.add(jpmCalculadora, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 160, 40));

        jpmAjustes.setBackground(new java.awt.Color(255, 255, 255));
        jpmAjustes.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpmAjustesMouseMoved(evt);
            }
        });
        jpmAjustes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpmAjustesMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpmAjustesMouseExited(evt);
            }
        });
        jpmAjustes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Settings_25px.png"))); // NOI18N
        jpmAjustes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 40));

        jLabel6.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 153, 153));
        jLabel6.setText("Ajustes");
        jpmAjustes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 60, 40));

        jpTop.add(jpmAjustes, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, 140, -1));

        jpBackFrame.add(jpTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 60));

        jpMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpRegistros.setBackground(new java.awt.Color(255, 255, 255));
        jpRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpRegistrosMouseClicked(evt);
            }
        });
        jpRegistros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpFormulario.setBackground(new java.awt.Color(255, 255, 255));
        jpFormulario.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(170, 170, 170)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtaDomicilio.setColumns(20);
        jtaDomicilio.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtaDomicilio.setForeground(new java.awt.Color(50, 50, 50));
        jtaDomicilio.setLineWrap(true);
        jtaDomicilio.setRows(5);
        jtaDomicilio.setCaretColor(new java.awt.Color(153, 153, 153));
        jScrollPane7.setViewportView(jtaDomicilio);

        jPanel7.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 250, 70));

        jLabel54.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(153, 153, 153));
        jLabel54.setText("Nombre:");
        jPanel7.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, -1, -1));

        jtfNombre.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfNombre.setForeground(new java.awt.Color(50, 50, 50));
        jtfNombre.setBorder(null);
        jtfNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfNombreKeyTyped(evt);
            }
        });
        jPanel7.add(jtfNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 250, 20));
        jPanel7.add(jsNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 250, 10));

        jLabel57.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(153, 153, 153));
        jLabel57.setText("Telefono:");
        jPanel7.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, -1, 20));

        jtfTelefono.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfTelefono.setForeground(new java.awt.Color(50, 50, 50));
        jtfTelefono.setBorder(null);
        jtfTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfTelefonoKeyTyped(evt);
            }
        });
        jPanel7.add(jtfTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 20, 170, 20));

        jLabel55.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(153, 153, 153));
        jLabel55.setText("RFC:");
        jPanel7.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 70, -1, -1));

        jtfRFC.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfRFC.setForeground(new java.awt.Color(50, 50, 50));
        jtfRFC.setBorder(null);
        jPanel7.add(jtfRFC, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 70, 170, 20));
        jPanel7.add(jSeparator14, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 90, 170, 10));

        jLabel56.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(153, 153, 153));
        jLabel56.setText("Email:");
        jPanel7.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 120, -1, -1));

        jtfEmail.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfEmail.setForeground(new java.awt.Color(50, 50, 50));
        jtfEmail.setBorder(null);
        jPanel7.add(jtfEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 120, 170, 20));
        jPanel7.add(jSeparator15, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 140, 170, 10));
        jPanel7.add(jsTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 40, 170, 10));

        jLabel52.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(153, 153, 153));
        jLabel52.setText("Domicilio:");
        jPanel7.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, 20));

        jpFormulario.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 720, 170));

        jpFormularioEquipo.setBackground(new java.awt.Color(255, 255, 255));
        jpFormularioEquipo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(170, 170, 170)));
        jpFormularioEquipo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel58.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(153, 153, 153));
        jLabel58.setText("Articulo:");
        jpFormularioEquipo.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jtfArticulo.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfArticulo.setForeground(new java.awt.Color(50, 50, 50));
        jtfArticulo.setBorder(null);
        jtfArticulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfArticuloKeyTyped(evt);
            }
        });
        jpFormularioEquipo.add(jtfArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 140, 20));
        jpFormularioEquipo.add(jsArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 140, 10));

        jLabel59.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(153, 153, 153));
        jLabel59.setText("Marca:");
        jpFormularioEquipo.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 60, -1));

        jtfMarca.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfMarca.setForeground(new java.awt.Color(50, 50, 50));
        jtfMarca.setBorder(null);
        jtfMarca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfMarcaKeyTyped(evt);
            }
        });
        jpFormularioEquipo.add(jtfMarca, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 140, 20));
        jpFormularioEquipo.add(jsMarca, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 140, 10));

        jLabel60.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(153, 153, 153));
        jLabel60.setText("Modelo:");
        jpFormularioEquipo.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 20));

        jtfModelo.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfModelo.setForeground(new java.awt.Color(50, 50, 50));
        jtfModelo.setBorder(null);
        jtfModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfModeloActionPerformed(evt);
            }
        });
        jtfModelo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfModeloKeyTyped(evt);
            }
        });
        jpFormularioEquipo.add(jtfModelo, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 100, 140, 20));
        jpFormularioEquipo.add(jsModelo, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 140, 10));

        jcbCargador.setBackground(new java.awt.Color(255, 255, 255));
        jcbCargador.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jcbCargador.setForeground(new java.awt.Color(80, 80, 80));
        jcbCargador.setText("Cargador");
        jpFormularioEquipo.add(jcbCargador, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 90, -1));

        jcbPassword.setBackground(new java.awt.Color(255, 255, 255));
        jcbPassword.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jcbPassword.setForeground(new java.awt.Color(80, 80, 80));
        jcbPassword.setSelected(true);
        jcbPassword.setText("Contraseña");
        jcbPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbPasswordActionPerformed(evt);
            }
        });
        jpFormularioEquipo.add(jcbPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, 110, -1));

        jtfPassword.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtfPassword.setForeground(new java.awt.Color(50, 50, 50));
        jtfPassword.setBorder(null);
        jtfPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfPasswordKeyTyped(evt);
            }
        });
        jpFormularioEquipo.add(jtfPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 170, 20));
        jpFormularioEquipo.add(jsPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 120, 170, 10));

        jLabel64.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(153, 153, 153));
        jLabel64.setText("Estado:");
        jpFormularioEquipo.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 20, 70, 30));

        jcbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sin reparar", "En reparacion", "Listo", "Entregada" }));
        jpFormularioEquipo.add(jcbEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 20, 120, 30));

        jLabel66.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(153, 153, 153));
        jLabel66.setText("Tecnico en reparacion");
        jpFormularioEquipo.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 70, 190, -1));

        jtfReparador.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jpFormularioEquipo.add(jtfReparador, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 190, 30));

        jcbBateria.setBackground(new java.awt.Color(255, 255, 255));
        jcbBateria.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jcbBateria.setForeground(new java.awt.Color(80, 80, 80));
        jcbBateria.setText("Bateria");
        jpFormularioEquipo.add(jcbBateria, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 80, -1));

        jpFormulario.add(jpFormularioEquipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 720, 150));

        jtaFalla.setColumns(20);
        jtaFalla.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtaFalla.setForeground(new java.awt.Color(50, 50, 50));
        jtaFalla.setLineWrap(true);
        jtaFalla.setRows(5);
        jScrollPane8.setViewportView(jtaFalla);

        jpFormulario.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 410, 230, 100));

        jLabel62.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(153, 153, 153));
        jLabel62.setText("Daño reportado:");
        jpFormulario.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, -1, -1));

        jLabel63.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(153, 153, 153));
        jLabel63.setText("Notas del tecnico:");
        jpFormulario.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 380, 160, -1));

        jtaNotas.setColumns(20);
        jtaNotas.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtaNotas.setForeground(new java.awt.Color(50, 50, 50));
        jtaNotas.setLineWrap(true);
        jtaNotas.setRows(5);
        jScrollPane9.setViewportView(jtaNotas);

        jpFormulario.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 410, 230, 100));

        jLabel69.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(153, 153, 153));
        jLabel69.setText("Fecha de");
        jpFormulario.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 410, 90, 20));

        jLabel68.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(153, 153, 153));
        jLabel68.setText("ingreso");
        jpFormulario.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 430, 70, 20));

        jtfFecha.setEditable(false);
        jtfFecha.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jpFormulario.add(jtfFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 410, 100, 30));
        jpFormulario.add(jtfCosto, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 470, 100, 30));

        jLabel67.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(153, 153, 153));
        jLabel67.setText("Costo");
        jpFormulario.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 480, 50, 20));

        jpRegistros.add(jpFormulario, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 760, 540));

        jpTutorial.setBackground(new java.awt.Color(255, 255, 255));
        jpTutorial.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Decker", 0, 24)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(100, 100, 100));
        jLabel27.setText("Puedes empezar a crear uno desde aqui");
        jpTutorial.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 420, 460, 30));

        jLabel28.setFont(new java.awt.Font("Decker", 0, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(100, 100, 100));
        jLabel28.setText("Ups... parece que no tienes ningun registro");
        jpTutorial.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 230, 480, 30));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Find_and_Replace_100px.png"))); // NOI18N
        jpTutorial.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, -1, -1));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Right_Arrow_100px.png"))); // NOI18N
        jpTutorial.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 390, -1, -1));

        jpRegistros.add(jpTutorial, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 750, 540));

        jtableRegistros.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jtableRegistros.setForeground(new java.awt.Color(100, 100, 100));
        jtableRegistros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtableRegistros.setAlignmentX(0.8F);
        jtableRegistros.setAlignmentY(0.8F);
        jtableRegistros.setSelectionBackground(new java.awt.Color(172, 227, 235));
        jtableRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtableRegistrosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtableRegistros);

        jpRegistros.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 140, 210, 270));

        jpOpciones.setBackground(new java.awt.Color(255, 255, 255));
        jpOpciones.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpOpc1.setBackground(new java.awt.Color(255, 255, 255));
        jpOpc1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/nuevo.png"))); // NOI18N
        jLabel51.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel51.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel51MouseClicked(evt);
            }
        });
        jpOpc1.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Actualizar.png"))); // NOI18N
        jLabel61.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel61.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel61MouseClicked(evt);
            }
        });
        jpOpc1.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, -1, -1));

        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Imprimir.png"))); // NOI18N
        jLabel65.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel65.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel65MouseClicked(evt);
            }
        });
        jpOpc1.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Eliminar.png"))); // NOI18N
        jLabel70.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel70.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel70MouseClicked(evt);
            }
        });
        jpOpc1.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, -1, -1));

        jpOpciones.add(jpOpc1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 230, 90));

        jpOpc2.setBackground(new java.awt.Color(255, 255, 255));
        jpOpc2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpGuardar.setBackground(new java.awt.Color(255, 255, 255));
        jpGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpGuardarMouseClicked(evt);
            }
        });
        jpGuardar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Save_40px.png"))); // NOI18N
        jpGuardar.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel26.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(80, 80, 80));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Guardar");
        jpGuardar.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 60, -1));

        jpOpc2.add(jpGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 70));

        jpCancelar.setBackground(new java.awt.Color(255, 255, 255));
        jpCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpCancelarMouseClicked(evt);
            }
        });
        jpCancelar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Cancel_40px.png"))); // NOI18N
        jpCancelar.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel24.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(80, 80, 80));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Cancelar");
        jpCancelar.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 60, -1));

        jpOpc2.add(jpCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 80, 70));

        jpOpciones.add(jpOpc2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 110));

        jpRegistros.add(jpOpciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 420, 210, 90));

        jpBusqueda.setBackground(new java.awt.Color(255, 255, 255));
        jpBusqueda.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jpBusqueda.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtfBuscar.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jtfBuscar.setForeground(new java.awt.Color(140, 140, 140));
        jtfBuscar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jtfBuscar.setText("Buscar");
        jtfBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfBuscarMouseClicked(evt);
            }
        });
        jtfBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfBuscarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfBuscarKeyTyped(evt);
            }
        });
        jpBusqueda.add(jtfBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 150, 30));

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Search_30px.png"))); // NOI18N
        jpBusqueda.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jpBusqueda.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 80, -1));

        jLabel71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Registro.png"))); // NOI18N
        jLabel71.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel71.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel71MouseClicked(evt);
            }
        });
        jpBusqueda.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Cliente.png"))); // NOI18N
        jLabel72.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel72.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel72MouseClicked(evt);
            }
        });
        jpBusqueda.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, -1));

        jpRegistros.add(jpBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 20, 210, 100));

        jpMain.add(jpRegistros, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 540));

        jpAjustes.setBackground(new java.awt.Color(255, 255, 255));
        jpAjustes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpBaseDatos.setBackground(new java.awt.Color(255, 255, 255));
        jpBaseDatos.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpBaseDatosMouseMoved(evt);
            }
        });
        jpBaseDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpBaseDatosMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpBaseDatosMouseExited(evt);
            }
        });
        jpBaseDatos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(153, 153, 153));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Base de datos");
        jpBaseDatos.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, 120, 30));

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Database_Administrator_80px_1.png"))); // NOI18N
        jpBaseDatos.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 90, -1));

        jpAjustes.add(jpBaseDatos, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, 200, 170));

        jpEmpresa.setBackground(new java.awt.Color(255, 255, 255));
        jpEmpresa.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpEmpresaMouseMoved(evt);
            }
        });
        jpEmpresa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpEmpresaMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpEmpresaMouseExited(evt);
            }
        });
        jpEmpresa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(153, 153, 153));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Empresa");
        jpEmpresa.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 80, 30));

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_New_Job_80px.png"))); // NOI18N
        jpEmpresa.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        jpAjustes.add(jpEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 200, 170));

        jpUsuarios.setBackground(new java.awt.Color(255, 255, 255));
        jpUsuarios.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpUsuariosMouseMoved(evt);
            }
        });
        jpUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpUsuariosMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpUsuariosMouseExited(evt);
            }
        });
        jpUsuarios.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Usuarios");
        jpUsuarios.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 80, 30));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_User_Account_80px.png"))); // NOI18N
        jpUsuarios.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        jpAjustes.add(jpUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 30, 200, 170));

        jpLicencia.setBackground(new java.awt.Color(255, 255, 255));
        jpLicencia.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpLicenciaMouseMoved(evt);
            }
        });
        jpLicencia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpLicenciaMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpLicenciaMouseExited(evt);
            }
        });
        jpLicencia.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel29.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(153, 153, 153));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Licencia");
        jpLicencia.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 90, 30));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Key_80px.png"))); // NOI18N
        jpLicencia.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        jpAjustes.add(jpLicencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 200, 170));

        jpOpcAjustes.setBackground(new java.awt.Color(255, 255, 255));
        jpOpcAjustes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jpOpcAjustes.setForeground(new java.awt.Color(153, 153, 153));
        jpOpcAjustes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpOpcEmpresa.setBackground(new java.awt.Color(255, 255, 255));
        jpOpcEmpresa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel37.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(60, 60, 60));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel37.setText("Pagina de facebook:");
        jpOpcEmpresa.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 120, 20));

        jLabel45.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(60, 60, 60));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel45.setText("Direccion:");
        jpOpcEmpresa.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, 70, 20));

        jLabel46.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(60, 60, 60));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel46.setText("Telefono:");
        jpOpcEmpresa.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, 80, 20));

        jLabel47.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(60, 60, 60));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel47.setText("Email:");
        jpOpcEmpresa.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 140, 50, 20));

        jLabel48.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(60, 60, 60));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel48.setText("Pagina de internet:");
        jpOpcEmpresa.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 110, 20));
        jpOpcEmpresa.add(jtxtFacebookEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, 210, -1));
        jpOpcEmpresa.add(jtxtDireccionEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, 210, -1));
        jpOpcEmpresa.add(jtxtTelefonoEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, 210, -1));
        jpOpcEmpresa.add(jtxtEmailEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 140, 210, -1));
        jpOpcEmpresa.add(jtxtPaginaEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 180, 210, -1));
        jpOpcEmpresa.add(jtxtNombreEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 210, -1));

        jLabel49.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(60, 60, 60));
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel49.setText("Nombre:");
        jpOpcEmpresa.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 60, 20));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jpOpcEmpresa.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 20, 10, 220));

        jLabel36.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(60, 60, 60));
        jLabel36.setText("Anotacion en el pie del reporte");
        jpOpcEmpresa.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 20, -1, -1));

        jtaAnotacionesEmpresa.setColumns(20);
        jtaAnotacionesEmpresa.setLineWrap(true);
        jtaAnotacionesEmpresa.setRows(5);
        jScrollPane1.setViewportView(jtaAnotacionesEmpresa);

        jpOpcEmpresa.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 50, 240, 190));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jpOpcEmpresa.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 20, 10, 220));

        jLabel41.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(60, 60, 60));
        jLabel41.setText("Insertar logo");
        jpOpcEmpresa.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 20, -1, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlbLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Maintenance_40px.png"))); // NOI18N
        jlbLogo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlbLogo.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jlbLogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlbLogoMouseClicked(evt);
            }
        });
        jPanel1.add(jlbLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 70, 60));

        jpOpcEmpresa.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 50, 110, 100));

        jpGuardar2.setBackground(new java.awt.Color(255, 255, 255));
        jpGuardar2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpGuardar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpGuardar2MouseClicked(evt);
            }
        });
        jpGuardar2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Save_40px.png"))); // NOI18N
        jpGuardar2.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 50, -1));

        jLabel53.setFont(new java.awt.Font("Decker", 0, 14)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(80, 80, 80));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText("Guardar");
        jpGuardar2.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 70, -1));

        jpOpcEmpresa.add(jpGuardar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 170, 90, 70));

        jpOpcAjustes.add(jpOpcEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 900, 260));

        jpOpcBaseDatos.setBackground(new java.awt.Color(255, 255, 255));
        jpOpcBaseDatos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpServidor.setBackground(new java.awt.Color(255, 255, 255));
        jpServidor.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpServidorMouseMoved(evt);
            }
        });
        jpServidor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpServidorMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpServidorMouseExited(evt);
            }
        });
        jpServidor.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Server_80px.png"))); // NOI18N
        jpServidor.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 100, -1));

        jLabel43.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(153, 153, 153));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Servidor");
        jpServidor.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 80, 20));

        jpOpcBaseDatos.add(jpServidor, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 70, 140, 130));

        jpImportar.setBackground(new java.awt.Color(255, 255, 255));
        jpImportar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpImportarMouseMoved(evt);
            }
        });
        jpImportar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpImportarMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpImportarMouseExited(evt);
            }
        });
        jpImportar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Database_Import_80px.png"))); // NOI18N
        jpImportar.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 100, -1));

        jLabel35.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(153, 153, 153));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("Restaurar");
        jpImportar.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 100, 30));

        jpOpcBaseDatos.add(jpImportar, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, 140, 130));

        jpRespaldar.setBackground(new java.awt.Color(255, 255, 255));
        jpRespaldar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpRespaldarMouseMoved(evt);
            }
        });
        jpRespaldar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpRespaldarMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpRespaldarMouseExited(evt);
            }
        });
        jpRespaldar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Data_Protection_80px_1.png"))); // NOI18N
        jpRespaldar.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 110, -1));

        jLabel39.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(153, 153, 153));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Respaldar");
        jpRespaldar.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 100, 30));

        jpOpcBaseDatos.add(jpRespaldar, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 70, 140, 130));

        jpBorrar.setBackground(new java.awt.Color(255, 255, 255));
        jpBorrar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jpBorrarMouseMoved(evt);
            }
        });
        jpBorrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpBorrarMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jpBorrarMouseExited(evt);
            }
        });
        jpBorrar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/icons8_Data_Backup_80px.png"))); // NOI18N
        jpBorrar.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 100, -1));

        jLabel44.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(153, 153, 153));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("Reiniciar");
        jpBorrar.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 100, 30));

        jpOpcBaseDatos.add(jpBorrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 70, 140, 130));

        jpOpcAjustes.add(jpOpcBaseDatos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 900, 260));

        jpAjustes.add(jpOpcAjustes, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 920, 280));

        jpMain.add(jpAjustes, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 540));

        jpInicio.setBackground(new java.awt.Color(255, 255, 255));
        jpInicio.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Imac.png"))); // NOI18N
        jpInicio.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, -1, -1));

        jLabel15.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(140, 140, 140));
        jLabel15.setText("Activado");
        jpInicio.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 500, -1, -1));

        jLabel18.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(140, 140, 140));
        jLabel18.setText("Administrador");
        jpInicio.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 470, 190, -1));

        jLabel19.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(140, 140, 140));
        jLabel19.setText("Usuario:");
        jpInicio.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, -1, -1));

        jLabel20.setFont(new java.awt.Font("Decker", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(140, 140, 140));
        jLabel20.setText("Version 1.0");
        jpInicio.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 520, -1, -1));

        jLabel21.setFont(new java.awt.Font("Decker", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(140, 140, 140));
        jLabel21.setText("Producto:");
        jpInicio.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 500, -1, -1));

        jpMain.add(jpInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 540));

        jpBackFrame.add(jpMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1000, 540));

        getContentPane().add(jpBackFrame, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jpmInicioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmInicioMouseClicked
        cambiarVentana("Inicio");
        jpmRegistros.setBackground(new java.awt.Color(255, 255, 255));
        jpmCalculadora.setBackground(new java.awt.Color(255, 255, 255));
        jpmAjustes.setBackground(new java.awt.Color(255, 255, 255));
        reproducirAudio("Click");
    }//GEN-LAST:event_jpmInicioMouseClicked

    private void jpmRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmRegistrosMouseClicked
        jpmInicio.setBackground(new java.awt.Color(255, 255, 255));
        jpmCalculadora.setBackground(new java.awt.Color(255, 255, 255));
        jpmAjustes.setBackground(new java.awt.Color(255, 255, 255));
        cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");
        checarRegistrosVacios();
        cambiarVentana("Registros");
        reproducirAudio("Click");
    }//GEN-LAST:event_jpmRegistrosMouseClicked

    private void jpmCalculadoraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmCalculadoraMouseClicked
        try{
           Runtime.getRuntime().exec("calc");
        }catch(Exception e){
            System.out.println("No es posible abrir la calculadora");
        }
        reproducirAudio("Click");
    }//GEN-LAST:event_jpmCalculadoraMouseClicked

    private void jpmRegistrosMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmRegistrosMouseMoved
        if(!ventanaOrigen.equalsIgnoreCase("Registros"))
            jpmRegistros.setBackground(new java.awt.Color(240, 240, 240));
    }//GEN-LAST:event_jpmRegistrosMouseMoved

    private void jpmCalculadoraMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmCalculadoraMouseMoved
        jpmCalculadora.setBackground(new java.awt.Color(240, 240, 240));
    }//GEN-LAST:event_jpmCalculadoraMouseMoved

    private void jpmAjustesMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmAjustesMouseMoved
        if(!ventanaOrigen.equalsIgnoreCase("Ajustes"))
            jpmAjustes.setBackground(new java.awt.Color(240, 240, 240));
    }//GEN-LAST:event_jpmAjustesMouseMoved

    private void jpmAjustesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmAjustesMouseExited
        if(!ventanaOrigen.equalsIgnoreCase("Ajustes"))
            jpmAjustes.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jpmAjustesMouseExited

    private void jpmCalculadoraMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmCalculadoraMouseExited
        jpmCalculadora.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jpmCalculadoraMouseExited

    private void jpmRegistrosMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmRegistrosMouseExited
        if(!ventanaOrigen.equalsIgnoreCase("Registros"))
            jpmRegistros.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jpmRegistrosMouseExited

    private void jpmInicioMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmInicioMouseExited
        if(!ventanaOrigen.equalsIgnoreCase("Inicio"))
            jpmInicio.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jpmInicioMouseExited

    private void jpmInicioMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmInicioMouseMoved
        if(!ventanaOrigen.equalsIgnoreCase("Inicio"))
            jpmInicio.setBackground(new java.awt.Color(240, 240, 240));
    }//GEN-LAST:event_jpmInicioMouseMoved

    private void jpmAjustesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpmAjustesMouseClicked
        jpmRegistros.setBackground(new java.awt.Color(255, 255, 255));
        jpmCalculadora.setBackground(new java.awt.Color(255, 255, 255));
        jpmInicio.setBackground(new java.awt.Color(255, 255, 255));
        reproducirAudio("Click");
        cambiarVentana("Ajustes");
    }//GEN-LAST:event_jpmAjustesMouseClicked

    private void jtfBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfBuscarMouseClicked
        jtfBuscar.setText("");
    }//GEN-LAST:event_jtfBuscarMouseClicked

    private void jpRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRegistrosMouseClicked
        jtfBuscar.setText("Buscar");
    }//GEN-LAST:event_jpRegistrosMouseClicked

    private void jpGuardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpGuardarMouseClicked
        if(insertarRegistro(false)){
            reproducirAudio("Guardar");
            registrosVacios = false;
            cambiarVentana("Registros");
        }
        else{
            reproducirAudio("Error");
        }
    }//GEN-LAST:event_jpGuardarMouseClicked

    private void jpCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpCancelarMouseClicked
        jpOpc2.setVisible(false);
        jpOpc1.setVisible(true);

        restablecerCampos(false);
        cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");
        checarRegistrosVacios();

        if(registrosVacios){
            cambiarVentana("Registros");
        }else{
            int row = jtableRegistros.getRowCount()-1;
            jtableRegistros.setRowSelectionInterval(row, row);
            cargarRegistro(row);
        }

    }//GEN-LAST:event_jpCancelarMouseClicked

    private void jtableRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtableRegistrosMouseClicked
        restablecerCampos(false);
        if(!tablaClientes){
            cargarRegistro(Integer.parseInt(String.valueOf(jtableRegistros.getValueAt(jtableRegistros.getSelectedRow(), 1))));
        }
        else{
            cambiarVentana("Registros");
            cargarCliente(Integer.parseInt(String.valueOf(jtableRegistros.getValueAt(jtableRegistros.getSelectedRow(), 0))));
        }

    }//GEN-LAST:event_jtableRegistrosMouseClicked

    private void jcbPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbPasswordActionPerformed
        if(jcbPassword.isSelected()){
            jtfPassword.setEnabled(true);
            jtfPassword.setText("");
        }else{
            jtfPassword.setBorder(null);
            jsPassword.setForeground(new java.awt.Color(160,160,160));
            jtfPassword.setText("Sin contraseña");
            jtfPassword.setEnabled(false);
        }


    }//GEN-LAST:event_jcbPasswordActionPerformed

    private void jtfModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfModeloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtfModeloActionPerformed

    private void jtfNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfNombreKeyTyped
        jtfNombre.setBorder(null);
        jsNombre.setForeground(new java.awt.Color(160,160,160));
    }//GEN-LAST:event_jtfNombreKeyTyped

    private void jtfTelefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfTelefonoKeyTyped
        jtfTelefono.setBorder(null);
        jsTelefono.setForeground(new java.awt.Color(160,160,160));
    }//GEN-LAST:event_jtfTelefonoKeyTyped

    private void jtfArticuloKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfArticuloKeyTyped
        jtfArticulo.setBorder(null);
        jsArticulo.setForeground(new java.awt.Color(160,160,160));
    }//GEN-LAST:event_jtfArticuloKeyTyped

    private void jtfMarcaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfMarcaKeyTyped
        jtfMarca.setBorder(null);
        jsMarca.setForeground(new java.awt.Color(160,160,160));
    }//GEN-LAST:event_jtfMarcaKeyTyped

    private void jtfModeloKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfModeloKeyTyped
        jtfModelo.setBorder(null);
        jsModelo.setForeground(new java.awt.Color(160,160,160));
    }//GEN-LAST:event_jtfModeloKeyTyped

    private void jtfPasswordKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfPasswordKeyTyped
        jtfPassword.setBorder(null);
        jsPassword.setForeground(new java.awt.Color(160,160,160));         // TODO add your handling code here:
    }//GEN-LAST:event_jtfPasswordKeyTyped

    private void jpLicenciaMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpLicenciaMouseMoved
        jpLicencia.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpLicenciaMouseMoved

    private void jpLicenciaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpLicenciaMouseExited
        jpLicencia.setBorder(null);
    }//GEN-LAST:event_jpLicenciaMouseExited

    private void jpBaseDatosMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpBaseDatosMouseMoved
        jpBaseDatos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpBaseDatosMouseMoved

    private void jpBaseDatosMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpBaseDatosMouseExited
        jpBaseDatos.setBorder(null);
    }//GEN-LAST:event_jpBaseDatosMouseExited

    private void jpEmpresaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpEmpresaMouseExited
        jpEmpresa.setBorder(null);        // TODO add your handling code here:
    }//GEN-LAST:event_jpEmpresaMouseExited

    private void jpEmpresaMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpEmpresaMouseMoved
        jpEmpresa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpEmpresaMouseMoved

    private void jpUsuariosMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpUsuariosMouseExited
        jpUsuarios.setBorder(null);
    }//GEN-LAST:event_jpUsuariosMouseExited

    private void jpUsuariosMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpUsuariosMouseMoved
        jpUsuarios.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpUsuariosMouseMoved

    private void jpBaseDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpBaseDatosMouseClicked
        reproducirAudio("Click");
        cambiarVentanaAjustes("baseDatos");
    }//GEN-LAST:event_jpBaseDatosMouseClicked

    private void jpLicenciaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpLicenciaMouseClicked
        reproducirAudio("Click");
    }//GEN-LAST:event_jpLicenciaMouseClicked

    private void jpEmpresaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpEmpresaMouseClicked
        reproducirAudio("Click");
        cambiarVentanaAjustes("empresa");
    }//GEN-LAST:event_jpEmpresaMouseClicked

    private void jpUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpUsuariosMouseClicked
        reproducirAudio("Click");
    }//GEN-LAST:event_jpUsuariosMouseClicked

    private void jpBorrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpBorrarMouseClicked
        resetearBaseDatos();
    }//GEN-LAST:event_jpBorrarMouseClicked

    private void jpImportarMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpImportarMouseMoved
        jpImportar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpImportarMouseMoved

    private void jpImportarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpImportarMouseExited
        jpImportar.setBorder(null);
    }//GEN-LAST:event_jpImportarMouseExited

    private void jpRespaldarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRespaldarMouseExited
        jpRespaldar.setBorder(null);
    }//GEN-LAST:event_jpRespaldarMouseExited

    private void jpRespaldarMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRespaldarMouseMoved
        jpRespaldar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpRespaldarMouseMoved

    private void jpBorrarMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpBorrarMouseMoved
        jpBorrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpBorrarMouseMoved

    private void jpBorrarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpBorrarMouseExited
        jpBorrar.setBorder(null);
    }//GEN-LAST:event_jpBorrarMouseExited

    private void jpServidorMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpServidorMouseExited
        jpServidor.setBorder(null);
    }//GEN-LAST:event_jpServidorMouseExited

    private void jpServidorMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpServidorMouseMoved
        jpServidor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(140,140,140)));
    }//GEN-LAST:event_jpServidorMouseMoved

    private void jpImportarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpImportarMouseClicked
        reproducirAudio("Click");
        Conexion.restaurarBD();
        cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");
    }//GEN-LAST:event_jpImportarMouseClicked

    private void jpRespaldarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRespaldarMouseClicked
        reproducirAudio("Click");
        Conexion.respaldarBD();
    }//GEN-LAST:event_jpRespaldarMouseClicked

    private void jpServidorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpServidorMouseClicked
        reproducirAudio("Click");
    }//GEN-LAST:event_jpServidorMouseClicked

    private void jpGuardar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpGuardar2MouseClicked
        escribirDatosEmpresa();
    }//GEN-LAST:event_jpGuardar2MouseClicked

    private void jlbLogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbLogoMouseClicked
        buscarImagen();
    }//GEN-LAST:event_jlbLogoMouseClicked

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
       
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        buscarImagen();
    }//GEN-LAST:event_jPanel1MouseClicked

    private void jLabel51MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel51MouseClicked
        jpOpc1.setVisible(false);
        jpOpc2.setVisible(true);
        jtfBuscar.setText("Buscar");
        if(registrosVacios){
            jpTutorial.setVisible(false);
            jpFormulario.setVisible(true);
        }
        if(!tablaClientes){
            restablecerCampos(false);
        }else{
            restablecerCampos(true);
        }
        
    }//GEN-LAST:event_jLabel51MouseClicked

    private void jLabel61MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel61MouseClicked
        if(tablaClientes){
            if(actualizarCliente()) reproducirAudio("Actualizar");
            
            else reproducirAudio("Error");
        }
        else
        {
            if(insertarRegistro(true)) reproducirAudio("Actualizar");
            
            else reproducirAudio("Error");
        }
        jtfBuscar.setText("Buscar");
        if(jtableRegistros.getSelectedRow() == -1)
                    JOptionPane.showMessageDialog(null,"No tienes ningun registro seleccionado");
    }//GEN-LAST:event_jLabel61MouseClicked

    private void jLabel65MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel65MouseClicked
        int fila = jtableRegistros.getSelectedRow();
        if(fila != -1){
            if(!tablaClientes){
                imprimirRegistro();
            }
            else{
                JOptionPane.showMessageDialog(null,"Para imprimir necesitas seleccionar un registro, no un cliente");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No tienes ningun registro seleccionado");
        }
        jtfBuscar.setText("Buscar");
    }//GEN-LAST:event_jLabel65MouseClicked

    private void jLabel70MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel70MouseClicked
        if(tablaClientes){
            reproducirAudio("Error");
            JOptionPane.showMessageDialog(null,"No es posible eliminar un cliente, "
                    + "puede actualizar sus datos si se requiere");
        }else{
            eliminarRegistro();
        }
        jtfBuscar.setText("Buscar");
    }//GEN-LAST:event_jLabel70MouseClicked

    private void jLabel71MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel71MouseClicked
        tablaClientes = false;
        jtfBuscar.setText("Buscar");
        cambiarVentana("Registros");
        cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros;");
    }//GEN-LAST:event_jLabel71MouseClicked

    private void jLabel72MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel72MouseClicked
        tablaClientes = true;
        jtfBuscar.setText("Buscar");
        cargarTablaUsuarios("SELECT ID_Cliente, Nombre FROM Clientes;");
    }//GEN-LAST:event_jLabel72MouseClicked

    private void jtfBuscarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfBuscarKeyTyped
        
    }//GEN-LAST:event_jtfBuscarKeyTyped

    private void jtfBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfBuscarKeyReleased
        if(tablaClientes){
            cargarTablaUsuarios("SELECT ID_Cliente, Nombre FROM Clientes "
                    + "WHERE Nombre LIKE '%"+jtfBuscar.getText()+"%';");
        }else{
            cargarTablaRegistros("SELECT Estado,ID_Registro FROM Registros "
                    + "WHERE ID_Registro LIKE '%"+jtfBuscar.getText()+"%';");
        }
    }//GEN-LAST:event_jtfBuscarKeyReleased

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JCheckBox jcbBateria;
    private javax.swing.JCheckBox jcbCargador;
    private javax.swing.JComboBox<String> jcbEstado;
    private javax.swing.JCheckBox jcbPassword;
    private javax.swing.JLabel jlbLogo;
    private javax.swing.JPanel jpAjustes;
    private javax.swing.JPanel jpBackFrame;
    private javax.swing.JPanel jpBaseDatos;
    private javax.swing.JPanel jpBorrar;
    private javax.swing.JPanel jpBusqueda;
    private javax.swing.JPanel jpCancelar;
    private javax.swing.JPanel jpEmpresa;
    private javax.swing.JPanel jpFormulario;
    private javax.swing.JPanel jpFormularioEquipo;
    private javax.swing.JPanel jpGuardar;
    private javax.swing.JPanel jpGuardar2;
    private javax.swing.JPanel jpImportar;
    private javax.swing.JPanel jpInicio;
    private javax.swing.JPanel jpLicencia;
    private javax.swing.JPanel jpMain;
    private javax.swing.JPanel jpOpc1;
    private javax.swing.JPanel jpOpc2;
    private javax.swing.JPanel jpOpcAjustes;
    private javax.swing.JPanel jpOpcBaseDatos;
    private javax.swing.JPanel jpOpcEmpresa;
    private javax.swing.JPanel jpOpciones;
    private javax.swing.JPanel jpRegistros;
    private javax.swing.JPanel jpRespaldar;
    private javax.swing.JPanel jpServidor;
    private javax.swing.JPanel jpTop;
    private javax.swing.JPanel jpTutorial;
    private javax.swing.JPanel jpUsuarios;
    private javax.swing.JPanel jpmAjustes;
    private javax.swing.JPanel jpmCalculadora;
    private javax.swing.JPanel jpmInicio;
    private javax.swing.JPanel jpmRegistros;
    private javax.swing.JSeparator jsArticulo;
    private javax.swing.JSeparator jsMarca;
    private javax.swing.JSeparator jsModelo;
    private javax.swing.JSeparator jsNombre;
    private javax.swing.JSeparator jsPassword;
    private javax.swing.JSeparator jsTelefono;
    private javax.swing.JTextArea jtaAnotacionesEmpresa;
    private javax.swing.JTextArea jtaDomicilio;
    private javax.swing.JTextArea jtaFalla;
    private javax.swing.JTextArea jtaNotas;
    private javax.swing.JTable jtableRegistros;
    private javax.swing.JTextField jtfArticulo;
    private javax.swing.JTextField jtfBuscar;
    private javax.swing.JTextField jtfCosto;
    private javax.swing.JTextField jtfEmail;
    private javax.swing.JTextField jtfFecha;
    private javax.swing.JTextField jtfMarca;
    private javax.swing.JTextField jtfModelo;
    private javax.swing.JTextField jtfNombre;
    private javax.swing.JTextField jtfPassword;
    private javax.swing.JTextField jtfRFC;
    private javax.swing.JTextField jtfReparador;
    private javax.swing.JTextField jtfTelefono;
    private javax.swing.JTextField jtxtDireccionEmpresa;
    private javax.swing.JTextField jtxtEmailEmpresa;
    private javax.swing.JTextField jtxtFacebookEmpresa;
    private javax.swing.JTextField jtxtNombreEmpresa;
    private javax.swing.JTextField jtxtPaginaEmpresa;
    private javax.swing.JTextField jtxtTelefonoEmpresa;
    // End of variables declaration//GEN-END:variables

    


}
