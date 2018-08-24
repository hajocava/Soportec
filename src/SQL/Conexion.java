package SQL;
import Frames.Equipo;
import Frames.Cliente;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Conexion {
    private static String dataBase = "reparaciones";
    private static String user = "root";
    private static String pass = "";
    private static String host = "localhost:3306";
    private static String server = "jdbc:mysql://"+host+"/"+dataBase;
    
    public static Connection getConexion(){
        Connection cn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection(server,user,pass);
        }catch(Exception e){
            System.out.println(e);
        }
        return cn;
    }
    
    public static void respaldarBD(){
        JFileChooser fc = new JFileChooser();
        if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            String ruta = fc.getSelectedFile().getAbsolutePath() + ".sql";
            try {
                String comando = "C:\\xampp\\mysql\\bin\\mysqldump --opt -u" + user + " -B " + dataBase + " -r " + ruta;
                Runtime rt = Runtime.getRuntime();
                rt.exec(comando);
                JOptionPane.showMessageDialog(null,"Exportado correctamente");
            } catch (Exception e){
                JOptionPane.showMessageDialog(null,"Ocurrio un error al realizar el Backup");
            }
        }
    }
    
    public static void restaurarBD(){
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("SQL","sql");
        fc.setFileFilter(filtro);
        
        if(fc.showDialog(null,"Seleccionar") == JFileChooser.APPROVE_OPTION){
            String ruta = fc.getSelectedFile().getAbsolutePath();
            System.out.println(ruta);
            try {
                queryUpdate("DROP DATABASE reparaciones;");
                createDatabase("CREATE DATABASE reparaciones");
                
                String query = "CREATE TABLE Clientes(" +
                    "ID_Cliente int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "Nombre VARCHAR(255)," +
                    "Domicilio VARCHAR(255)," +
                    "RFC VARCHAR(255)," +
                    "Email VARCHAR(255)," +
                    "Telefono VARCHAR(255));";
                queryUpdate(query);

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

                queryUpdate(query);
                
                String comando = "C:\\xampp\\mysql\\bin\\mysql -u root -p --database=reparaciones < resp.sql";
                Runtime rt = Runtime.getRuntime();
                rt.exec(comando);
                JOptionPane.showMessageDialog(null,"Importado correctamente");
            }catch (Exception e) {
                JOptionPane.showMessageDialog(null,"Ocurrio un error al restaurar la base de datos");
            }
        }
    }
    
    public static ResultSet selectFrom(String consulta){
        Connection cn = getConexion();
        Statement st;
        ResultSet datos = null;
        try{
            st = cn.createStatement();
            datos = st.executeQuery(consulta);
        }catch(Exception e){
            System.out.println(e);
        }
        return datos;
    }
    
    public static void insertInto(Cliente cliente, Equipo equipo){ 
        Connection cn = getConexion();
        try{
            String query = "INSERT INTO Clientes(Nombre,Domicilio,RFC,Email,Telefono) VALUES(?,?,?,?,?);";
            
            PreparedStatement statement = cn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            statement.setString (1, cliente.getNombre());
            statement.setString (2, cliente.getDomicilio());
            statement.setString (3, cliente.getRfc());
            statement.setString (4, cliente.getEmail());
            statement.setString (5, cliente.getTelefono());
            
            statement.execute();
            
            ResultSet resultado = statement.getGeneratedKeys();
            if(resultado.next()){
                
                int id = resultado.getInt(1);
                            query = "INSERT INTO Registros(FK_Clientes,Articulo,Marca,Modelo,"
                    + "Bateria,Cargador,Password,Falla,Notas,Estado,Costo,Reparador,Fecha) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";
                statement = cn.prepareStatement(query);

                statement.setInt(1, resultado.getInt(1));
                statement.setString(2, equipo.getArticulo());
                statement.setString(3, equipo.getMarca());
                statement.setString(4, equipo.getModelo());
                statement.setInt(5, equipo.getBateria());
                statement.setInt(6, equipo.getCargador());
                statement.setString(7, equipo.getPassword());
                statement.setString(8, equipo.getFalla());
                statement.setString(9, equipo.getNotas());
                statement.setString(10, equipo.getEstado());
                statement.setString(11, equipo.getCosto());
                statement.setString(12, equipo.getReparador());
                statement.setString((13), equipo.getFecha());

                statement.execute();
                System.out.println("Ingresado correctamente");
            }
            cn.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Ocurrio un error al insertar el registro");
            System.out.println(e);
        }
    }
    
    public static void updateRegistro(Cliente cliente, Equipo equipo, int idRegistro){
        Connection cn = Conexion.getConexion();
        try {
            String query = "UPDATE Registros SET Articulo = ?, Marca = ?, "
                    + "Modelo = ?, Bateria = ?, Cargador = ?, Password = ?, "
                    + "Falla = ?, Notas = ?, Estado = ?, Costo = ?, Reparador = ?, Fecha = ?"
                    + "WHERE ID_Registro = " + idRegistro + ";";
            PreparedStatement statement = cn.prepareStatement(query);
            statement.setString(1, equipo.getArticulo());
            statement.setString(2, equipo.getMarca());
            statement.setString(3, equipo.getModelo());
            statement.setInt(4, equipo.getBateria());
            statement.setInt(5, equipo.getCargador());
            statement.setString(6, equipo.getPassword());
            statement.setString(7, equipo.getFalla());
            statement.setString(8, equipo.getNotas());
            statement.setString(9, equipo.getEstado());
            statement.setString(10, equipo.getCosto());
            statement.setString(11, equipo.getReparador());
            statement.setString(12, equipo.getFecha());
            
            statement.executeUpdate();
            cn.close();
            System.out.println("Registro actualizado correctamente");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Ocurrio un error al actualizar el registro");
            System.out.println(e);
        }
    }
    
    public static void updateCliente(Cliente cliente, int idCliente){
        Connection cn = Conexion.getConexion();
        try {
            String query = "UPDATE Clientes SET Nombre = ?, Telefono = ?, "
                    + "Domicilio = ?, RFC = ?, Email = ?"
                    + "WHERE ID_Cliente = " + idCliente + ";";
            PreparedStatement statement = cn.prepareStatement(query);
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getTelefono());
            statement.setString(3, cliente.getDomicilio());
            statement.setString(4, cliente.getRfc());
            statement.setString(5, cliente.getEmail());
            
            statement.executeUpdate();
            cn.close();
            System.out.println("Cliente actualizado correctamente");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Ocurrio un error al actualizar el registro");
            System.out.println(e);
        }
    }
    
    
    public static void queryExecute(String query){
        Connection cn = Conexion.getConexion();
        try {
            Statement statement = cn.createStatement();
            statement.execute(query);
            System.out.println("Comando ejecutado");
            cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error al ejecutar el comando");
        }
    }
    
    public static boolean queryUpdate(String query){
        Connection cn = Conexion.getConexion();
        try {
            Statement statement = cn.createStatement();
            statement.executeUpdate(query);
            System.out.println("update ejecutado");
            cn.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error al ejecutar update ");
            return false;
        }
    }
    
    
    public static void createDatabase(String query){
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/","root","");
            Statement statement = cn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Base de datos creada");
            
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error al crear la base de datos");
        }
    }
    
    
}
