package access;

import controller.LoginScreenController;
import hernandez.c195_wgu_final.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointments;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/** Access to the appointments section of the database */
public class AppointmentsAccess {

    /** Gathers all appointments from the database and returns the list
     *
     * @return List of all appointments
     * @throws SQLException
     */
    public static ObservableList<Appointments> getAllAppointments() throws SQLException {
        ObservableList<Appointments> appointmentsList = FXCollections.observableArrayList();

        String query = "SELECT * FROM appointments ";
        PreparedStatement statement = JDBC.connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            LocalDateTime startDateTime = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime endDateTime = rs.getTimestamp("End").toLocalDateTime();
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            ZonedDateTime startTimeUTC = startDateTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime convertedStartTime = startTimeUTC.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            LocalDateTime localStartDateTime = convertedStartTime.toLocalDateTime();

            ZonedDateTime endTimeUTC = endDateTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime convertedEndTime = endTimeUTC.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            LocalDateTime localEndDateTime = convertedEndTime.toLocalDateTime();

            Appointments appointment = new Appointments(appointmentID, title, description, location, contactID, type, localStartDateTime, localEndDateTime, customerID, userID);
            appointmentsList.add(appointment);
        }
        return appointmentsList;
    }

    /** Creates a new unique appointment ID
     *
     * @return unique appointment ID
     * @throws SQLException
     */
    public static int newAppointmentID() throws SQLException {
        int newAppointmentID = 0;
        for (Appointments appointment : AppointmentsAccess.getAllAppointments()) {
            newAppointmentID = newAppointmentID + 1;
            if (!(newAppointmentID == appointment.getAppointmentID())) {
                return newAppointmentID;
            }
        }
        return newAppointmentID + 1;
    }

    /** Adds new appiontment to the database
     *
     * @param appointmentID
     * @param title
     * @param description
     * @param location
     * @param type
     * @param appointmentStart
     * @param appointmentEnd
     * @param customerID
     * @param userID
     * @param contactID
     * @throws SQLException
     */
    public static void addNewAppointment(int appointmentID, String title, String description, String location, String type, LocalDateTime appointmentStart, LocalDateTime appointmentEnd, int customerID, int userID, int contactID) throws SQLException {
        String insertQuery = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = JDBC.connection.prepareStatement(insertQuery);

        statement.setInt(1, appointmentID);
        statement.setString(2, title);
        statement.setString(3, description);
        statement.setString(4, location);
        statement.setString(5, type);
        statement.setTimestamp(6, Timestamp.valueOf(appointmentStart));
        statement.setTimestamp(7, Timestamp.valueOf(appointmentEnd));
        statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(9, LoginScreenController.getLoggedInUser());
        statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(11, LoginScreenController.getLoggedInUser());
        statement.setInt(12, customerID);
        statement.setInt(13, userID);
        statement.setInt(14, contactID);
        statement.execute();
    }

    /** Updates appointment in the database
     *
     * @param appointmentID
     * @param title
     * @param description
     * @param location
     * @param type
     * @param appointmentStart
     * @param appointmentEnd
     * @param customerID
     * @param userID
     * @param contactID
     * @throws SQLException
     */
    public static void updateAppointment(int appointmentID, String title, String description, String location, String type, LocalDateTime appointmentStart, LocalDateTime appointmentEnd, int customerID, int userID, int contactID) throws SQLException {
        String updateQuery = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement statement = JDBC.connection.prepareStatement(updateQuery);

        statement.setInt(1, appointmentID);
        statement.setString(2, title);
        statement.setString(3, description);
        statement.setString(4, location);
        statement.setString(5, type);
        statement.setTimestamp(6, Timestamp.valueOf(appointmentStart));
        statement.setTimestamp(7, Timestamp.valueOf(appointmentEnd));
        statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(9, LoginScreenController.getLoggedInUser());
        statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(11, LoginScreenController.getLoggedInUser());
        statement.setInt(12, customerID);
        statement.setInt(13, userID);
        statement.setInt(14, contactID);
        statement.setInt(15, appointmentID);
        statement.execute();
    }

}
