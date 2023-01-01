package controller;

import access.AppointmentsAccess;
import access.UsersAccess;
import hernandez.c195_wgu_final.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointments;
import model.Users;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/** Login screen controller, first screen seen upon launch
 *
 */
public class LoginScreenController implements Initializable {

    public Button loginButton;
    public Label companyNameText;
    public Label userLocationText;
    public TextField usernameField;
    public TextField passwordField;
    private static String username = "";

    /** Prefills text on screen based upon whether users Locale and system language is in English or French
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        resourceBundle = ResourceBundle.getBundle("languages/login", Locale.getDefault());
        userLocationText.setText(resourceBundle.getString("location") + ZoneId.systemDefault());
        usernameField.setPromptText(resourceBundle.getString("username"));
        passwordField.setPromptText(resourceBundle.getString("password"));
        loginButton.setText(resourceBundle.getString("login"));

    }

    /** Will verify if username and password match the database to allow user to login
     *
     * @param actionEvent
     * @throws IOException
     * @throws Exception
     * @throws SQLException
     */
    public void loginClicked(ActionEvent actionEvent) throws IOException, Exception, SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("languages/login", Locale.getDefault());

        try {
            if (UsersAccess.validateUser(username, password)) {
                int appointmentsFound = 0;
                for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
                    if (appointments.getStartDateTime().isBefore(LocalDateTime.now().plusMinutes(15)) && appointments.getStartDateTime().isAfter(LocalDateTime.now())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(resourceBundle.getString("loginAppointmentIn15") + "\n" +
                                resourceBundle.getString("appointmentID") + appointments.getAppointmentID() + "\n" +
                                resourceBundle.getString("appointmentDateTime") + appointments.getStartDateTime());
                        alert.showAndWait();

                        appointmentsFound = appointmentsFound + 1;
                    }
                }

                if (appointmentsFound == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText(resourceBundle.getString("loginNoAppointments"));
                    alert.showAndWait();
                }

                FileWriter fw = new FileWriter("src/main/java/login_activity.txt", true);
                fw.write("LOGIN ATTEMPT --- User: " + username + " --- " + LocalDateTime.now() + " --- Attempt Successful: Yes\n");
                fw.close();

                setLoggedInUser(username);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/hernandez/c195_wgu_final/mainScreen.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 400, 500);
                stage.setScene(scene);
                stage.show();
            }
            else {
                FileWriter fw = new FileWriter("src/main/java/login_activity.txt", true);
                fw.write("LOGIN ATTEMPT --- User: " + username + " --- " + LocalDateTime.now() + " --- Attempt Successful: No\n");
                fw.close();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(resourceBundle.getString("error"));
                alert.setContentText(resourceBundle.getString("incorrectLogin"));
                alert.showAndWait();
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /** Will Save the logged in user for use if any data is modified
     *
     * @param username
     */
    public void setLoggedInUser(String username) {
        this.username = username;
    }
    public static String getLoggedInUser() {
        return username;
    }

}