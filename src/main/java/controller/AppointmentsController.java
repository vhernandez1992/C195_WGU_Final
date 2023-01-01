package controller;

import access.AppointmentsAccess;
import hernandez.c195_wgu_final.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

/** Controller for the main appointment screen
 *
 */
public class AppointmentsController implements Initializable {
    public Button reportsButton;
    @FXML private RadioButton viewAllRadio;
    @FXML private RadioButton currentMonthRadio;
    @FXML private RadioButton currentWeekRadio;
    @FXML private TableView<Appointments> appointmentsTable;
    @FXML private TableColumn appointmentIDCol;
    @FXML private TableColumn titleCol;
    @FXML private TableColumn descriptionCol;
    @FXML private TableColumn locationCol;
    @FXML private TableColumn contactCol;
    @FXML private TableColumn typeCol;
    @FXML private TableColumn startCol;
    @FXML private TableColumn endCol;
    @FXML private TableColumn customerIDCol;
    @FXML private TableColumn userIDCol;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button createAppointmentButton;
    @FXML private Button backButton;

    /** If selected, all appointments will show in the table
     *
     * @param actionEvent
     */
    public void viewAllFilterSelected(ActionEvent actionEvent) {
        try {
            ObservableList<Appointments> allAppointmentsList = AppointmentsAccess.getAllAppointments();

            appointmentsTable.setItems(allAppointmentsList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** If selected, will show all appointments in the table for the current month
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void currentMonthFilterSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<Appointments> allAppointmentsList = AppointmentsAccess.getAllAppointments();
        ObservableList<Appointments> currentMonthAppointmentsList = FXCollections.observableArrayList();

        for (Appointments appointments : allAppointmentsList) {
            if (appointments.getStartDateTime().getMonth() == LocalDate.now().getMonth()) {
                currentMonthAppointmentsList.add(appointments);
            }
        }
        appointmentsTable.setItems(currentMonthAppointmentsList);
    }

    /** If selected, will show all appointments in the table for the current week
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void currentWeekFilterSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<Appointments> allAppointmentsList = AppointmentsAccess.getAllAppointments();
        ObservableList<Appointments> currentWeekAppointmentsList = FXCollections.observableArrayList();

        for (Appointments appointments : allAppointmentsList) {
            if (appointments.getStartDateTime().isAfter(LocalDateTime.now().minusDays(1)) && appointments.getStartDateTime().isBefore(LocalDateTime.now().plusDays(7))) {
                currentWeekAppointmentsList.add(appointments);
            }
        }

        appointmentsTable.setItems(currentWeekAppointmentsList);

    }

    /** Will take the selected appointment and send to the Update screen to change and/or view all appointment info
     *
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void updateButtonSelected(ActionEvent actionEvent) throws IOException, SQLException {
        Appointments selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        int currIndex = appointmentsTable.getSelectionModel().getSelectedIndex();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hernandez/c195_wgu_final/updateAppointmentScreen.fxml"));
        Parent root = loader.load();
        UpdateAppointmentController appointmentController = loader.getController();
        appointmentController.appointmentToUpdate(selectedAppointment, currIndex);
        Scene scene = new Scene(root, 640, 480);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();


    }

    /** Will delete the selected appointment from the database and from the table
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteButtonSelected(ActionEvent actionEvent) throws SQLException {
        Appointments selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setContentText("No appointment selected to delete!");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this appointment?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            String deleteQuery = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement statement = JDBC.connection.prepareStatement(deleteQuery);
            int deletedAppointmentID = ((Appointments) appointmentsTable.getSelectionModel().getSelectedItem()).getAppointmentID();

            statement.setInt(1, deletedAppointmentID);
            statement.execute();

            ObservableList<Appointments> updatedAppointmentsList = AppointmentsAccess.getAllAppointments();
            appointmentsTable.setItems(updatedAppointmentsList);

        }
    }

    /** Will send to the Create new appointment screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void createAppointmentSelected(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/AddAppointmentScreen.fxml"));
        Scene scene = new Scene(root, 640, 480);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** prefills the appointment table with all appointments from the database
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<Appointments> appointmentsList = AppointmentsAccess.getAllAppointments();

            appointmentsTable.setItems(appointmentsList);

            appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            startCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
            endCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Sends back to the main screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void backButtonSelected(ActionEvent actionEvent) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/mainScreen.fxml"));
        Scene scene = new Scene(root, 400, 500);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** Sends to the Reports screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void reportsButtonSelected(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/reportsScreen.fxml"));
        Scene scene = new Scene(root, 900, 700);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
