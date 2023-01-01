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
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/** Controller for adding appointment to database
 *
 */
public class AddAppointmentController implements Initializable {
    @FXML private Button createAppointmentButton;
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


    /** Cancels all inputted information and sends back to the Appointments screen
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

    /** Pre-fills the Customer, Contacts and Users combo boxes
     * Lambda was used for adding each customer/user/contact to the list that would fill the combobox
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

    /** Adds the new appointment to the database, checking to make sure all fields and boxes are filled out
     *
     * @param actionEvent
     */
    public void createAppointmentSelected(ActionEvent actionEvent) {
        try {
            if (!titleField.getText().isEmpty() || !DescriptionField.getText().isEmpty() || !locationField.getText().isEmpty() || !typeField.getText().isEmpty() || !(startDatePicker.getValue() == null) || !startTimeHour.getText().isEmpty() || !startTimeMinute.getText().isEmpty() || !(endDatePicker.getValue() == null) ||
            !endTimeHour.getText().isEmpty() || !endTimeMinute.getText().isEmpty() || !customerComboBox.getSelectionModel().isEmpty() || !companyContactCombo.getSelectionModel().isEmpty() ||
            !userComboBox.getSelectionModel().isEmpty()) {
                int appointmentID = AppointmentsAccess.newAppointmentID();
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
                    AppointmentsAccess.addNewAppointment(appointmentID, title, description, location, type, convertedAppointmentStartTime, convertedAppointmentEndTime, customerID, userID, contactID);

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


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch(NumberFormatException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Incorrect format for input");
            alert.showAndWait();
        }
    }

    /** Converts the Hour and Minute fields into the proper format to be added into a DateTime
     *
     * @param hour
     * @param minute
     * @return the converted time in correct format
     */
    public static String convertTimeFormat(int hour, int minute) {
        String convertedTime = hour + ":" + minute + ":00";
        return convertedTime;
    }

    /** Checks the test DateTime to verify that it is not outside business hours
     *
     * @param testTime
     * @return true or false depending on result
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

    /** Checks to see if the Start and End date overlaps with another existing appointment for the same customer
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
