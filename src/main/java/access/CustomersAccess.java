package access;

import controller.LoginScreenController;
import hernandez.c195_wgu_final.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customers;
import model.Users;

import java.sql.*;
import java.time.LocalDateTime;

/** Access to customers list in the database
 *
 */
public class CustomersAccess {

    /** Gathers all customers from the database
     *
     * @return list of customers
     * @throws SQLException
     */
    public static ObservableList<Customers> getAllCustomers() throws SQLException {
        ObservableList<Customers> customersList = FXCollections.observableArrayList();

        String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID, first_level_divisions.Division FROM customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID";
        PreparedStatement statement = JDBC.connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();


        while (rs.next()) {
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String postalCode = rs.getString("Postal_Code");
            String phoneNumber = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");

            Customers customer = new Customers(customerID, customerName, address, postalCode, phoneNumber, divisionID, divisionName);
            customersList.add(customer);
        }
        return customersList;
    }

    /** Generates a new unique customer ID
     *
     * @return customer ID
     * @throws SQLException
     */
    public static int newCustomerID() throws SQLException {
        int newCustomerID = 0;
        for (Customers customer : CustomersAccess.getAllCustomers()) {
            newCustomerID = newCustomerID + 1;
            if (!(newCustomerID == customer.getCustomerID())) {
                return newCustomerID;
            }
        }
        return newCustomerID + 1;
    }

    /** Adds customer to the database
     *
     * @param customerID
     * @param customerName
     * @param address
     * @param postalCode
     * @param phoneNumber
     * @param divisionID
     * @throws SQLException
     */
    public static void addCustomer(int customerID, String customerName, String address, String postalCode, String phoneNumber, int divisionID) throws SQLException {
        String insertQuery = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = JDBC.connection.prepareStatement(insertQuery);

        statement.setInt(1, customerID);
        statement.setString(2, customerName);
        statement.setString(3, address);
        statement.setString(4, postalCode);
        statement.setString(5, phoneNumber);
        statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(7, LoginScreenController.getLoggedInUser());
        statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(9, LoginScreenController.getLoggedInUser());
        statement.setInt(10, divisionID);
        statement.execute();
    }

    /** Updates the customer in the database
     *
     * @param customerID
     * @param customerName
     * @param address
     * @param postalCode
     * @param phoneNumber
     * @param divisionID
     * @throws SQLException
     */
    public static void updateCustomer(int customerID, String customerName, String address, String postalCode, String phoneNumber, int divisionID) throws SQLException {
        String updateQuery = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement statement = JDBC.connection.prepareStatement(updateQuery);

        statement.setInt(1, customerID);
        statement.setString(2, customerName);
        statement.setString(3, address);
        statement.setString(4, postalCode);
        statement.setString(5, phoneNumber);
        statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(7, "test");
        statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
        statement.setString(9, "update_Test");
        statement.setInt(10, divisionID);
        statement.setInt(11, customerID);
        statement.execute();
    }
}
