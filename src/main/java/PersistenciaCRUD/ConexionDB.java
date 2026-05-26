package PersistenciaCRUD;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConexionDB {
    static Connection conn = null;
    static Statement st = null;
    static ResultSet rs = null;
    static Connection connTienda = null;
    static Statement stTienda = null;
    
    // Variables vacías, ya no están en el código fuente
    static String login = "";
    static String password = "";
    static String url = "";

    // Método para cargar credenciales desde el archivo
    private static void cargarCredenciales() {
        // Solo leemos el archivo si url está vacía (para no leerlo muchas veces)
        if (url.isEmpty()) {
            try {
                Properties props = new Properties();
                // Asegúrate de que config.properties está en la raíz de tu proyecto
                props.load(new FileInputStream("config.properties"));
                url = props.getProperty("db.url");
                login = props.getProperty("db.user");
                password = props.getProperty("db.password");
            } catch (IOException e) {
                System.out.println("No se pudo cargar config.properties. Verifica que exista en la raíz.");
                e.printStackTrace();
            }
        }
    }

    public static Connection Enlace(Connection conn) {
        cargarCredenciales(); // Cargamos antes de intentar conectar
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(url, login, password);
            System.out.println("Conexion exitosa...");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1017) {
                System.out.println("Usuario o contraseña incorrectos");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Statement sta(Statement st) throws SQLException {
        conn = Enlace(conn);
        st = conn.createStatement();
        return st;
    }

    public static ResultSet EnlEst(ResultSet rs) throws SQLException {
        st = sta(st);
        rs = st.executeQuery("select * from productos");
        return rs;
    }

    public static Connection EnlaceTienda(Connection connTienda) {
        cargarCredenciales(); // Cargamos antes de intentar conectar
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            connTienda = DriverManager.getConnection(url, login, password);
            System.out.println("Conexion exitosa a esquema TIENDA...");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1017) {
                System.out.println("Error TIENDA: Usuario o contraseña incorrectos.");
            } else {
                System.out.println("Error conexión TIENDA: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connTienda;
    }

    public static Statement staTienda(Statement stTienda) throws SQLException {
        connTienda = EnlaceTienda(connTienda);
        stTienda = connTienda.createStatement();
        return stTienda;
    }

    public static ResultSet EnlEstTienda(ResultSet rsTienda, String nombreTabla) throws SQLException {
        stTienda = staTienda(stTienda);
        rsTienda = stTienda.executeQuery("select * from " + nombreTabla);
        return rsTienda;
    }
}