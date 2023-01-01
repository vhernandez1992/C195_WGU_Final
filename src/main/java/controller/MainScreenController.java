package controller;

import access.AppointmentsAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import model.Appointments;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/** Controller for mainscreen after login
 *
 */
public class MainScreenController implements Initializable {

    public Button customersButton;
    public Button appointmentsButton;
    public Button loginReportsButton;
    public Button logoutButton;
    public TableColumn nameColumn;
    public TableColumn startTimeColumn;
    public TableColumn locationColumn;
    public TableColumn typeColumn;

    /** Will send to the main Customer screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void customersButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/customersScreen.fxml"));
        Scene scene = new Scene(root, 700, 600);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** Will send to the main Appointments screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void appointmentButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/appointmentsScreen.fxml"));
        Scene scene = new Scene(root, 1250, 700);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** Logs user out, sending back to the login screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void logoutButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/loginScreen.fxml"));
        Scene scene = new Scene(root, 640, 480);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
