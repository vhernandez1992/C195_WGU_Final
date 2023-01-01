package controller;

import access.AppointmentsAccess;
import access.ContactsAccess;
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
import model.Contacts;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.*;

/** Controller for reports screen
 *
 */
public class ReportsController implements Initializable {
    @FXML private TableColumn appointmentIDCol;
    @FXML private TableColumn titleCol;
    @FXML private TableColumn typeCol;
    @FXML private TableColumn descriptionCol;
    @FXML private TableColumn startCol;
    @FXML private TableColumn endCol;
    @FXML private TableColumn customerIDCol;
    @FXML private ComboBox reportComboBox;
    @FXML private RadioButton typeReportRadio;
    @FXML private RadioButton monthReportRadio;
    @FXML private RadioButton contactReportRadio;
    @FXML private RadioButton locationReportRadio;
    @FXML private Label totalAppointmentsLabel;
    @FXML private TableView appointmentsTable;

    /** Will filter the appointment list based upon a selected appointment type
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void typeRadioSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<String> appointmentTypeList = FXCollections.observableArrayList();

        for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
            if (!appointmentTypeList.contains(appointments.getType())) {
                appointmentTypeList.add(appointments.getType());
            }
        }
        reportComboBox.setPromptText("Choose Appointment Type");
        reportComboBox.setItems(appointmentTypeList);
    }

    /** Will filer the appointment list based on the selected month
     * Lambda was used to add each month to a list to choose for the filter
     * @param actionEvent
     * @throws SQLException
     */
    public void monthRadioSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<String> appointmentMonthList = FXCollections.observableArrayList();
        List<String> months = Arrays.asList(new DateFormatSymbols().getMonths());


    //Used lambda to replace a for statement to add months to a list
        months.forEach(month -> appointmentMonthList.add(month));
    /*
        for (int i = 0; i < 12; ++i) {
            appointmentMonthList.add(months[i]);
        }
     */

        reportComboBox.setPromptText("Choose Appointment Month");
        reportComboBox.setItems(appointmentMonthList);
    }

    /** Will filter the list based upon a selected Contact
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void contactRadioSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<String> appointmentContactList = FXCollections.observableArrayList();

        for (Contacts contacts : ContactsAccess.getAllContacts()) {
            appointmentContactList.add(contacts.getContactName());
        }

        reportComboBox.setPromptText("Choose Company Contact");
        reportComboBox.setItems(appointmentContactList);
    }

    /** Will filter appointment list based upon a selected appointment location
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void locationRadioSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<String> appointmentLocationList = FXCollections.observableArrayList();


        for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
            if (!appointmentLocationList.contains(appointments.getLocation())) {
                appointmentLocationList.add(appointments.getLocation());
            }
        }

        reportComboBox.setPromptText("Select Appointment Location");
        reportComboBox.setItems(appointmentLocationList);
    }

    /** Sends back to the main Appointments screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void backButtonSelected(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/mainScreen.fxml"));
        Scene scene = new Scene(root, 400, 500);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** Pre-fills the table with all appointments prior to filter being selected
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

    }

    /** Once a radio button is selected, will add all selectable strings from appointments
     *
     * @param actionEvent
     */
    public void reportComboBoxSelected(ActionEvent actionEvent) {
        try {
            String selectedItem = reportComboBox.getSelectionModel().getSelectedItem().toString();
            int totalAppointments = 0;
            ObservableList<Appointments> filteredList = FXCollections.observableArrayList();

            if (typeReportRadio.isSelected()) {
                for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
                    if (selectedItem.equals(appointments.getType())) {
                        filteredList.add(appointments);
                        totalAppointments = totalAppointments + 1;
                    }
                }
            }
            else if (monthReportRadio.isSelected()) {

                for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
                    if (appointments.getStartDateTime().getMonth().name().equalsIgnoreCase(selectedItem)) {
                        filteredList.add(appointments);
                        totalAppointments = totalAppointments + 1;
                    }
                }
            }
            else if (contactReportRadio.isSelected()) {
                int selectedContact = 0;
                for (Contacts contacts : ContactsAccess.getAllContacts()) {
                    if (selectedItem.equals(contacts.getContactName())) {
                        selectedContact = contacts.getContactID();
                    }
                }

                for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
                    if (selectedContact == appointments.getContactID()) {
                        filteredList.add(appointments);
                        totalAppointments = totalAppointments + 1;
                    }
                }
            }
            else if (locationReportRadio.isSelected()) {
                for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
                    if (selectedItem.equals(appointments.getLocation())) {
                        filteredList.add(appointments);
                        totalAppointments = totalAppointments + 1;
                    }
                }
            }

            appointmentsTable.setItems(filteredList);


            totalAppointmentsLabel.setText("Total Appointments: " + totalAppointments);
            totalAppointmentsLabel.setVisible(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
