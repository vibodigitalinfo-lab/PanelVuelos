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
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.sql.PreparedStatement;
import javafx.scene.control.Alert;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

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
    
    private List<String> cargarOpciones(String sql, String columna) {
        List<String> opciones = new ArrayList<>();

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:vuelos.db");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                opciones.add(rs.getString(columna));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return opciones;
    }
    
    private void guardarVuelo(String numero, String hora, String destino, String puerta, String estado)
            throws SQLException {

        String sql = "INSERT INTO vuelo (numero, id_destino, hora, id_puerta, id_estado) VALUES ("
                   + "?, "
                   + "(SELECT id_destino FROM destino WHERE nombre_destino = ?), "
                   + "?, "
                   + "(SELECT id_puerta FROM puerta WHERE codigo_puerta = ?), "
                   + "(SELECT id_estado FROM estado_vuelo WHERE descripcion_estado = ?))";

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:vuelos.db");
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numero);
            ps.setString(2, destino);
            ps.setString(3, hora);
            ps.setString(4, puerta);
            ps.setString(5, estado);
            ps.executeUpdate();
        }
    }
    
    private void eliminarVuelo(String numero) throws SQLException {
        String sql = "DELETE FROM vuelo WHERE numero = ?";

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:vuelos.db");
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numero);
            ps.executeUpdate();
        }
    }
    
    private void cambiarEstado(String numero, String estado) throws SQLException {
        String sql = "UPDATE vuelo "
                   + "SET id_estado = (SELECT id_estado FROM estado_vuelo WHERE descripcion_estado = ?) "
                   + "WHERE numero = ?";

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:vuelos.db");
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setString(2, numero);
            ps.executeUpdate();
        }
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
        
        TextField campoNumero = new TextField();
        campoNumero.setPromptText("Nº vuelo (ej. IB3104)");

        TextField campoHora = new TextField();
        campoHora.setPromptText("Hora (ej. 12:30)");

        ComboBox<String> comboDestino = new ComboBox<>();
        comboDestino.setPromptText("Destino");
        comboDestino.getItems().addAll(
            cargarOpciones("SELECT nombre_destino FROM destino ORDER BY nombre_destino", "nombre_destino"));

        ComboBox<String> comboPuerta = new ComboBox<>();
        comboPuerta.setPromptText("Puerta");
        comboPuerta.getItems().addAll(
            cargarOpciones("SELECT codigo_puerta FROM puerta ORDER BY codigo_puerta", "codigo_puerta"));

        ComboBox<String> comboEstado = new ComboBox<>();
        comboEstado.setPromptText("Estado");
        comboEstado.getItems().addAll(
            cargarOpciones("SELECT descripcion_estado FROM estado_vuelo ORDER BY id_estado", "descripcion_estado"));

        Button botonAnadir = new Button("Añadir vuelo");
        
        Button botonEliminar = new Button("Eliminar vuelo seleccionado");
        botonEliminar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());

        Button botonEstado = new Button("Cambiar estado");
        botonEstado.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
        
        HBox formulario = new HBox(10, campoNumero, campoHora, comboDestino, comboPuerta, comboEstado, botonAnadir);
        
        botonAnadir.setOnAction(e -> {
            String numero = campoNumero.getText().trim().toUpperCase();
            String hora = campoHora.getText().trim();
            String destino = comboDestino.getValue();
            String puerta = comboPuerta.getValue();
            String estado = comboEstado.getValue();

            if (numero.isEmpty() || hora.isEmpty() || destino == null || puerta == null || estado == null) {
                new Alert(Alert.AlertType.WARNING, "Rellena todos los campos.").show();
                return;
            }
            
            if (!hora.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Formato incorrecto");
                alerta.setHeaderText(null);
                alerta.setContentText("La hora debe tener exactamente 5 caracteres con formato HH:mm (ej. 14:30 o 09:15).");
                alerta.showAndWait();
                return;
            }

            try {
                guardarVuelo(numero, hora, destino, puerta, estado);
                tabla.setItems(cargarVuelos());

                campoNumero.clear();
                campoHora.clear();
                comboDestino.setValue(null);
                comboPuerta.setValue(null);
                comboEstado.setValue(null);

            } catch (SQLException ex) {
                new Alert(Alert.AlertType.ERROR,
                        "No se pudo guardar el vuelo. ¿Ya existe ese número de vuelo?").show();
                ex.printStackTrace();
            }
        });
        
        botonEliminar.setOnAction(e -> {
            Vuelo seleccionado = tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                new Alert(Alert.AlertType.WARNING, "Selecciona primero un vuelo de la tabla.").show();
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar el vuelo " + seleccionado.getNumero() + " a " + seleccionado.getDestino() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);

            Optional<ButtonType> respuesta = confirmacion.showAndWait();

            if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
                try {
                    eliminarVuelo(seleccionado.getNumero());
                    tabla.setItems(cargarVuelos());
                } catch (SQLException ex) {
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar el vuelo.").show();
                    ex.printStackTrace();
                }
            }
        });
        
        botonEstado.setOnAction(e -> {
            Vuelo seleccionado = tabla.getSelectionModel().getSelectedItem();

            List<String> estados = cargarOpciones(
                "SELECT descripcion_estado FROM estado_vuelo ORDER BY id_estado", "descripcion_estado");

            ChoiceDialog<String> dialogo = new ChoiceDialog<>(seleccionado.getEstado(), estados);
            dialogo.setTitle("Cambiar estado");
            dialogo.setHeaderText("Vuelo " + seleccionado.getNumero() + " a " + seleccionado.getDestino());
            dialogo.setContentText("Nuevo estado:");

            Optional<String> respuesta = dialogo.showAndWait();

            if (respuesta.isPresent()) {
                try {
                    cambiarEstado(seleccionado.getNumero(), respuesta.get());
                    tabla.setItems(cargarVuelos());
                } catch (SQLException ex) {
                    new Alert(Alert.AlertType.ERROR, "No se pudo cambiar el estado.").show();
                    ex.printStackTrace();
                }
            }
        });

        HBox botones = new HBox(10, botonEstado, botonEliminar);
        VBox raiz = new VBox(10, titulo, tabla, botones, formulario);
        raiz.setStyle("-fx-padding: 15;");

        Scene escena = new Scene(raiz, 900, 450);
        stage.setTitle("Panel de Vuelos");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}