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

        ObservableList<Vuelo> vuelos = FXCollections.observableArrayList(
            new Vuelo("IB3104", "Londres", "10:15", "B12", "A tiempo"),
            new Vuelo("VY1234", "París", "10:40", "C03", "Cancelado"),
            new Vuelo("FR5678", "Roma", "11:05", "D21", "Retrasado"),
            new Vuelo("UX1092", "Lisboa", "11:30", "A05", "A tiempo")
        );
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