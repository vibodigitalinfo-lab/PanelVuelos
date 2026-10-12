import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {

    // Clase interna que representa un vuelo
    public static class Vuelo {
        private final String numero;
        private final String destino;
        private final String hora;
        private final String puerta;
        private final String estado;

        public Vuelo(String numero, String destino, String hora, String puerta, String estado) {
            this.numero = numero;
            this.destino = destino;
            this.hora = hora;
            this.puerta = puerta;
            this.estado = estado;
        }

        public String getNumero() { return numero; }
        public String getDestino() { return destino; }
        public String getHora() { return hora; }
        public String getPuerta() { return puerta; }
        public String getEstado() { return estado; }
    }
    
    private ObservableList<Vuelo> cargarVuelos() {
        ObservableList<Vuelo> lista = FXCollections.observableArrayList();

        String url = "jdbc:sqlite:vuelos.db";
        String sql = "SELECT v.numero, d.nombre_destino, v.hora, p.codigo_puerta, e.descripcion_estado "
                   + "FROM vuelo v "
                   + "JOIN destino d ON v.id_destino = d.id_destino "
                   + "JOIN puerta p ON v.id_puerta = p.id_puerta "
                   + "JOIN estado_vuelo e ON v.id_estado = e.id_estado "
                   + "ORDER BY v.hora";

        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Vuelo(
                    rs.getString("numero"),
                    rs.getString("nombre_destino"),
                    rs.getString("hora"),
                    rs.getString("codigo_puerta"),
                    rs.getString("descripcion_estado")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public void start(Stage stage) {
        Label titulo = new Label("Aeropuerto de Asturias");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<Vuelo> tabla = new TableView<>();

        TableColumn<Vuelo, String> colNumero = new TableColumn<>("Vuelo");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

        TableColumn<Vuelo, String> colDestino = new TableColumn<>("Destino");
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destino"));

        TableColumn<Vuelo, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

        TableColumn<Vuelo, String> colPuerta = new TableColumn<>("Puerta");
        colPuerta.setCellValueFactory(new PropertyValueFactory<>("puerta"));

        TableColumn<Vuelo, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        colEstado.setCellFactory(columna -> new TableCell<Vuelo, String>() {
            @Override
            protected void updateItem(String estado, boolean vacio) {
                super.updateItem(estado, vacio);

                if (vacio || estado == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                    setStyle("");
                    return;
                }

                setText(estado);
                setStyle("-fx-font-weight: bold;");

                if (estado.equals("A tiempo")) {
                    setTextFill(Color.GREEN);
                } else if (estado.equals("Embarcando")) {
                    setTextFill(Color.ORANGE);
                } else if (estado.equals("Retrasado")) {
                    setTextFill(Color.RED);
                } else if (estado.equals("Cancelado")){
                		setTextFill (Color.GREY);
                } else {
                    setTextFill(Color.BLACK);
                }
            }
        });

        tabla.getColumns().addAll(colNumero, colDestino, colHora, colPuerta, colEstado);

        ObservableList<Vuelo> vuelos = cargarVuelos();
        tabla.setItems(vuelos);

        VBox raiz = new VBox(10, titulo, tabla);
        raiz.setStyle("-fx-padding: 15;");

        Scene escena = new Scene(raiz, 600, 350);
        stage.setTitle("Panel de Vuelos");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}