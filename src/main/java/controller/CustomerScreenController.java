package controller;

import access.CountriesAccess;
import access.CustomersAccess;
import access.FirstLevelDivisionsAccess;
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
import model.Countries;
import model.Customers;
import model.FirstLevelDivisions;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

/** Main screen for the Customers
 *
 */
public class CustomerScreenController implements Initializable {
    @FXML private Button backButton;
    @FXML private TableColumn<Customers, Integer> customerIDCol;
    @FXML private TableColumn nameCol;
    @FXML private TableColumn addressCol;
    @FXML private TableColumn postalCol;
    @FXML private TableColumn phoneCol;
    @FXML private TableColumn firstLevelCol;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField postalCodeField;
    @FXML private TextField phoneField;
    @FXML private ComboBox countryComboBox;
    @FXML private ComboBox firstLevelComboBox;
    @FXML private Button addButton;
    @FXML private TableView<Customers> customerTable;

    /** Pre-fills the Customer table with all customers from the database
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<Customers> customersList = CustomersAccess.getAllCustomers();
            ObservableList<String> firstLevelDivisionsList = FXCollections.observableArrayList();

            customerTable.setItems(customersList);

            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            postalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            firstLevelCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

            countryComboBox.setItems(CountriesAccess.getAllCountries());

            for (FirstLevelDivisions firstLevelDivision : FirstLevelDivisionsAccess.getAllFirstLevelDivisions()) {
                firstLevelDivisionsList.add(firstLevelDivision.getDivisionName());
            }
            firstLevelComboBox.setItems(firstLevelDivisionsList);

            nameField.setTooltip(new Tooltip("First and Last Name"));
            addressField.setTooltip(new Tooltip("Address Format:\n•  U.S. address: 123 ABC Street, White Plains\n" +
                    "\n" +
                    "•  Canadian address: 123 ABC Street, Newmarket\n" +
                    "\n" +
                    "•  UK address: 123 ABC Street, Greenwich, London"));
            phoneField.setTooltip(new Tooltip("Format: 999-999-9999"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /** Will send the the Update screen to update and/or view all info for selected customer
     *
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void updateButtonClicked(ActionEvent actionEvent) throws IOException, SQLException {
        Customers selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        int currIndex = customerTable.getSelectionModel().getSelectedIndex();

        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setContentText("No customer selected to update!");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hernandez/c195_wgu_final/updateCustomersScreen.fxml"));
        Parent root = loader.load();
        UpdateCustomerController updateCustomerController = loader.getController();
        updateCustomerController.customerToModify(currIndex, selectedCustomer);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 500, 600);
        stage.setScene(scene);
        stage.show();
    }

    /** Will delete selected customer from the database and customer table
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteButtonClicked(ActionEvent actionEvent) throws SQLException {
        Customers selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setContentText("No customer selected to delete!");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this customer?\n" +
                "If this customer has any appointments, the appointments will also be deleted.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            String deleteQuery = "DELETE FROM customers WHERE Customer_ID = ?";
            PreparedStatement statement = JDBC.connection.prepareStatement(deleteQuery);
            int deletedCustomerID = customerTable.getSelectionModel().getSelectedItem().getCustomerID();

            statement.setInt(1, deletedCustomerID);
            statement.execute();

            ObservableList<Customers> updatedCustomersList = CustomersAccess.getAllCustomers();
            customerTable.setItems(updatedCustomersList);

        }
    }

    /** Will add new customer to database and customer table using inputted information
     *
     * @param actionEvent
     */
    public void addButtonPressed(ActionEvent actionEvent) {
        try {
            if (!nameField.getText().isEmpty() || !addressField.getText().isEmpty() || !postalCodeField.getText().isEmpty() || !phoneField.getText().isEmpty() || !countryComboBox.getSelectionModel().isEmpty() || !firstLevelComboBox.getSelectionModel().isEmpty()) {
                int customerID = CustomersAccess.newCustomerID();
                String customerName = nameField.getText();
                String address = addressField.getText();
                String postalCode = postalCodeField.getText();
                String phoneNumber = phoneField.getText();
                int divisionID = 0;


                for (FirstLevelDivisions firstLevelDiv : FirstLevelDivisionsAccess.getAllFirstLevelDivisions()) {
                    if (firstLevelComboBox.getSelectionModel().getSelectedItem().equals(firstLevelDiv.getDivisionName())) {
                        divisionID = firstLevelDiv.getDivisionID();
                    }
                }

                CustomersAccess.addCustomer(customerID, customerName, address, postalCode, phoneNumber, divisionID);


                nameField.clear();
                nameField.setPromptText("Name");
                addressField.clear();
                addressField.setPromptText("Address");
                phoneField.clear();
                phoneField.setPromptText("Phone Number");
                postalCodeField.clear();
                postalCodeField.setPromptText("Postal Code");
                countryComboBox.setPromptText("Country");
                firstLevelComboBox.setPromptText("State/Province");

                ObservableList<Customers> updatedCustomersList = CustomersAccess.getAllCustomers();
                customerTable.setItems(updatedCustomersList);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setContentText("Not all fields filled out.");
                alert.showAndWait();
            }

        }
        catch(NumberFormatException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Incorrect format for input");
            alert.showAndWait();
    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Sends back to the main screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void backButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/mainScreen.fxml"));
        Scene scene = new Scene(root, 400, 500);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** Will filter the first level divisions list based on which country is selected
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void countryComboSelected(ActionEvent actionEvent) throws SQLException {
        try {
            String selectedCountry = countryComboBox.getSelectionModel().getSelectedItem().toString();


            if (selectedCountry.equals("U.S")) {
                firstLevelComboBox.setItems(FirstLevelDivisionsAccess.getFilteredFirstLevelDivisions(1));
            }
            else if (selectedCountry.equals("UK")) {
                firstLevelComboBox.setItems(FirstLevelDivisionsAccess.getFilteredFirstLevelDivisions(2));
            }
            else if (selectedCountry.equals("Canada")) {
                firstLevelComboBox.setItems(FirstLevelDivisionsAccess.getFilteredFirstLevelDivisions(3));
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
