import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PruebaBD {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:vuelos.db";

        String sql = "SELECT v.numero, d.nombre_destino, v.hora, p.codigo_puerta, e.descripcion_estado "
                   + "FROM vuelo v "
                   + "JOIN destino d ON v.id_destino = d.id_destino "
                   + "JOIN puerta p ON v.id_puerta = p.id_puerta "
                   + "JOIN estado_vuelo e ON v.id_estado = e.id_estado";

        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String numero = rs.getString("numero");
                String destino = rs.getString("nombre_destino");
                String hora = rs.getString("hora");
                String puerta = rs.getString("codigo_puerta");
                String estado = rs.getString("descripcion_estado");

                System.out.println(numero + " | " + destino + " | " + hora + " | " + puerta + " | " + estado);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}