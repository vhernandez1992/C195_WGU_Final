package controller;

import access.AppointmentsAccess;
import access.ContactsAccess;
import access.CustomersAccess;
import access.UsersAccess;
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
import javafx.stage.Stage;
import model.Appointments;
import model.Contacts;
import model.Customers;
import model.Users;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static controller.AddAppointmentController.convertTimeFormat;

/** Controller for appointment updating screen
 *
 */
public class UpdateAppointmentController implements Initializable {
    @FXML private Button updateAppointmentButton;
    @FXML private Button cancelAppointmentButton;
    @FXML private TextField titleField;
    @FXML private TextField DescriptionField;
    @FXML private TextField locationField;
    @FXML private TextField typeField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeHour;
    @FXML private TextField startTimeMinute;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeHour;
    @FXML private TextField endTimeMinute;
    @FXML private ComboBox customerComboBox;
    @FXML private ComboBox userComboBox;
    @FXML private ComboBox companyContactCombo;
    @FXML private RadioButton startTimeAMRadio;
    @FXML private RadioButton endTimeAMRadio;
    @FXML private RadioButton startTimePMRadio;
    @FXML private RadioButton endTimePMRadio;
    public Appointments selectedAppointment;
    public int currIndex = 0;

    /** Passes the selected appointment to update and its index in the list
     *
     * @param selectedAppointment
     * @param currIndex
     * @throws SQLException
     */
    public void appointmentToUpdate(Appointments selectedAppointment, int currIndex) throws SQLException {
        this.selectedAppointment = selectedAppointment;
        this.currIndex = currIndex;
        String currCustomerName = "";
        String currUserName = "";
        String currContactName = "";

        for (Customers customers : CustomersAccess.getAllCustomers()) {
            if (selectedAppointment.getCustomerID() == customers.getCustomerID()) {
                currCustomerName = customers.getCustomerName();
            }
        }
        for (Users users : UsersAccess.getAllUsers()) {
            if (selectedAppointment.getUserID() == users.getUserID()) {
                currUserName = users.getUsername();
            }
        }
        for (Contacts contacts : ContactsAccess.getAllContacts()) {
            if (selectedAppointment.getContactID() == contacts.getContactID()) {
                currContactName = contacts.getContactName();
            }
        }

        customerComboBox.setValue(currCustomerName);
        userComboBox.setValue(currUserName);
        companyContactCombo.setValue(currContactName);
        titleField.setText(selectedAppointment.getTitle());
        DescriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        typeField.setText(selectedAppointment.getType());
        startDatePicker.setValue(selectedAppointment.getStartDateTime().toLocalDate());

        if (selectedAppointment.getStartDateTime().getHour() > 12) {
            startTimeHour.setText(String.valueOf(selectedAppointment.getStartDateTime().getHour() - 12));
            startTimePMRadio.setSelected(true);
        }
        else {
            startTimeHour.setText(String.valueOf(selectedAppointment.getStartDateTime().getHour()));
            startTimeAMRadio.setSelected(true);
        }

        startTimeMinute.setText(String.valueOf(selectedAppointment.getStartDateTime().getMinute()));
        endDatePicker.setValue(selectedAppointment.getEndDateTime().toLocalDate());

        if (selectedAppointment.getEndDateTime().getHour() > 12) {
            endTimeHour.setText(String.valueOf(selectedAppointment.getEndDateTime().getHour() - 12));
            endTimePMRadio.setSelected(true);
        }
        else {
            endTimeHour.setText(String.valueOf(selectedAppointment.getEndDateTime().getHour()));
            endTimeAMRadio.setSelected(true);
        }
        endTimeMinute.setText(String.valueOf(selectedAppointment.getEndDateTime().getMinute()));
    }

    /** Pre-fills the Customer, Contact and User list
     * Lambda used to add each customer, contact and user to their appropriate list
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<String> customersList = FXCollections.observableArrayList();
            ObservableList<String> contactsList = FXCollections.observableArrayList();
            ObservableList<String> usersList = FXCollections.observableArrayList();


    //  Changed from for-each statement to use a lambda to add each object to their appropriate list
            CustomersAccess.getAllCustomers().forEach(customers -> {customersList.add(customers.getCustomerName());});
            ContactsAccess.getAllContacts().forEach(contacts -> {contactsList.add(contacts.getContactName());});
            UsersAccess.getAllUsers().forEach(users -> {usersList.add(users.getUsername());});
    /*
            for (Customers customers : CustomersAccess.getAllCustomers()) {
                customersList.add(customers.getCustomerName());
            }
            for (Contacts contacts : ContactsAccess.getAllContacts()) {
                contactsList.add(contacts.getContactName());
            }
            for (Users users : UsersAccess.getAllUsers()) {
                usersList.add(users.getUsername());
            }

     */

            customerComboBox.setItems(customersList);
            userComboBox.setItems(usersList);
            companyContactCombo.setItems(contactsList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Takes all inputted data and will update in the database, sends back to appointment screen
     *
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void updateAppointmentSelected(ActionEvent actionEvent) throws SQLException, IOException {
        try {
            if (!titleField.getText().isEmpty() || !DescriptionField.getText().isEmpty() || !locationField.getText().isEmpty() || !typeField.getText().isEmpty() || !(startDatePicker.getValue() == null) || !startTimeHour.getText().isEmpty() || !startTimeMinute.getText().isEmpty() || !(endDatePicker.getValue() == null) || !endTimeHour.getText().isEmpty() || !endTimeMinute.getText().isEmpty() || !customerComboBox.getSelectionModel().isEmpty() || !companyContactCombo.getSelectionModel().isEmpty() ||
                    !userComboBox.getSelectionModel().isEmpty()) {
                int appointmentID = selectedAppointment.getAppointmentID();
                String title = titleField.getText();
                String description = DescriptionField.getText();
                String location = locationField.getText();
                String type = typeField.getText();
                Timestamp appointmentStart = null;
                Timestamp appointmentEnd = null;

                if (startTimePMRadio.isSelected()) {
                    appointmentStart = Timestamp.valueOf(startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + convertTimeFormat(Integer.parseInt(startTimeHour.getText()) + 12, Integer.parseInt(startTimeMinute.getText())));
                } else {
                    appointmentStart = Timestamp.valueOf(startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + convertTimeFormat(Integer.parseInt(startTimeHour.getText()), Integer.parseInt(startTimeMinute.getText())));
                }

                if (endTimePMRadio.isSelected()) {
                    appointmentEnd = Timestamp.valueOf(startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + convertTimeFormat(Integer.parseInt(endTimeHour.getText()) + 12, Integer.parseInt(endTimeMinute.getText())));
                } else {
                    appointmentEnd = Timestamp.valueOf(startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + convertTimeFormat(Integer.parseInt(endTimeHour.getText()), Integer.parseInt(endTimeMinute.getText())));
                }

                LocalDateTime appointmentStartDateTime = appointmentStart.toLocalDateTime();
                ZonedDateTime zoneStartTime = appointmentStartDateTime.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
                ZonedDateTime startDateTimeUTC = zoneStartTime.withZoneSameInstant(ZoneId.of("UTC"));
                LocalDateTime convertedAppointmentStartTime = startDateTimeUTC.toLocalDateTime();

                LocalDateTime appointmentEndDateTime = appointmentEnd.toLocalDateTime();
                ZonedDateTime zoneEndTime = appointmentEndDateTime.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
                ZonedDateTime endDateTimeUTC = zoneEndTime.withZoneSameInstant(ZoneId.of("UTC"));
                LocalDateTime convertedAppointmentEndTime = endDateTimeUTC.toLocalDateTime();

                int customerID = 0;
                int userID = 0;
                int contactID = 0;

                for (Customers customer : CustomersAccess.getAllCustomers()) {
                    if (customerComboBox.getSelectionModel().getSelectedItem().equals(customer.getCustomerName())) {
                        customerID = customer.getCustomerID();
                    }
                }

                for (Users user : UsersAccess.getAllUsers()) {
                    if (userComboBox.getSelectionModel().getSelectedItem().equals(user.getUsername())) {
                        userID = user.getUserID();
                    }
                }

                for (Contacts contact : ContactsAccess.getAllContacts()) {
                    if (companyContactCombo.getSelectionModel().getSelectedItem().equals(contact.getContactName())) {
                        contactID = contact.getContactID();
                    }
                }

                if (checkIfDuringBusHours(convertedAppointmentStartTime) == false || checkIfDuringBusHours(convertedAppointmentEndTime) == false) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setContentText("Appointment not within business hours\n" +
                            "Business hours: 8:00AM - 10:00PM EST");
                    alert.showAndWait();
                } else if (checkIfOverlapping(appointmentStartDateTime, appointmentEndDateTime, customerID)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setContentText("Start or End time overlapping with another appointment for this customer");
                    alert.showAndWait();
                } else {
                    AppointmentsAccess.updateAppointment(appointmentID, title, description, location, type, convertedAppointmentStartTime, convertedAppointmentEndTime, customerID, userID, contactID);

                    Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/appointmentsScreen.fxml"));
                    Scene scene = new Scene(root, 1250, 700);
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Not all fields filled out");
                alert.showAndWait();
            }
        }
        catch(NumberFormatException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Incorrect format for input");
            alert.showAndWait();
        }
    }

    /** Cancels and sends back to appointment screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void cancelSelected(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/appointmentsScreen.fxml"));
        Scene scene = new Scene(root, 1250, 700);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** Checks if DateTime is during business hours
     *
     * @param testTime
     * @return
     */
    public static boolean checkIfDuringBusHours(LocalDateTime testTime) {
        ZonedDateTime testUTCTime = testTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime testESTTime = testUTCTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDateTime convertedToESTTime = testESTTime.toLocalDateTime();

        if (convertedToESTTime.getHour() < 8 || (convertedToESTTime.getHour() >= 22 && convertedToESTTime.getMinute() > 0)) {
            return false;
        }

        return true;
    }

    /** Checks if Start or End time of appointment overlap different appointment for same customer
     *
     * @param testStartTime
     * @param testEndTime
     * @param customerID
     * @return true or false depending on result
     * @throws SQLException
     */
    public static boolean checkIfOverlapping(LocalDateTime testStartTime, LocalDateTime testEndTime, int customerID) throws SQLException {
        ObservableList<Appointments> customerAppointments = FXCollections.observableArrayList();


        for (Appointments appointments : AppointmentsAccess.getAllAppointments()) {
            if (appointments.getCustomerID() == customerID) {
                customerAppointments.add(appointments);
            }
        }

        for (Appointments appointments : customerAppointments) {
            if ((appointments.getStartDateTime().isBefore(testEndTime) && appointments.getStartDateTime().isAfter(testStartTime)) || (appointments.getEndDateTime().isBefore(testEndTime) && (appointments.getEndDateTime().isAfter(testStartTime)))) {
                return true;
            }

        }

        return false;
    }
}
