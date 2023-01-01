package access;

import hernandez.c195_wgu_final.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Access to users in database
 *
 */
public class UsersAccess {

    /** Generates list of users from database
     *
     * @return list of users
     * @throws SQLException
     */
    public static ObservableList<Users> getAllUsers() throws SQLException {
        ObservableList<Users> usersList = FXCollections.observableArrayList();

        String query = "SELECT * FROM users";
        PreparedStatement statement = JDBC.connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String username = rs.getString("User_Name");
            String password = rs.getString("Password");

            Users user = new Users(userID, username, password);
            usersList.add(user);
        }
        return usersList;
    }

    /** Verifies that username and password are in the database and correct to login
     *
     * @param username
     * @param password
     * @return true or false depending on validity of username/password
     * @throws SQLException
     */
    public static boolean validateUser(String username, String password) throws SQLException {
        try {
            String query = "Select * FROM users WHERE user_name = \"" + username + "\" AND password = \"" + password + "\"";

            PreparedStatement statement = JDBC.connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            rs.next();

            if (rs.getString("user_name").equals(username)) {
                if (rs.getString("password").equals(password)) {
                    return true;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }


}
