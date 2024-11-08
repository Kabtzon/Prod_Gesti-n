package ExamenFinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/BDMunshi";
    private static final String USER = "root";
    private static final String PASSWORD = "KabeTzon17/";
    
    private static Connection conexion = null;

    // Constructor privado para evitar la instanciación
    private DatabaseConnection() {}

    // Método para obtener una conexión única
    public static Connection conectar() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a la base de datos");//mensaje indicador de cada accion
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        return conexion;
    }

    // Método para cerrar la conexión cuando ya no se necesita
    public static void desconectar() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error de Cierre", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
