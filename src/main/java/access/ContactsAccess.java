package access;

import hernandez.c195_wgu_final.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contacts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Access to the contacts database
 *
 */
public class ContactsAccess {

    /** Gathers all contacts from the database
     *
     * @return list of all contacts
     * @throws SQLException
     */
    public static ObservableList<Contacts> getAllContacts() throws SQLException {
        ObservableList<Contacts> contactsList = FXCollections.observableArrayList();

        String query = "SELECT * FROM contacts";
        PreparedStatement statement = JDBC.connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");

            Contacts contact = new Contacts(contactID, contactName, contactEmail);
            contactsList.add(contact);
        }

        return contactsList;
    }
}
