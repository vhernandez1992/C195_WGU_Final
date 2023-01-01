package controller;

import access.CountriesAccess;
import access.CustomersAccess;
import access.FirstLevelDivisionsAccess;
import hernandez.c195_wgu_final.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import model.Countries;
import model.Customers;
import model.FirstLevelDivisions;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

/** Controller for Updating customer screen
 *
 */
public class UpdateCustomerController implements Initializable {
    public Button updateButton;
    public Button cancelButton;
    public TextField customerIDField;
    public TextField nameField;
    public TextField phoneField;
    public TextField addressField;
    public TextField postalCodeField;
    public ComboBox countryComboBox;
    public ComboBox firstLevelComboBox;
    public Customers selectedCustomer;
    private int currIndex = 0;

    /** Takes all input in fields and saves them to database for the passed through customer
     *
     * @param actionEvent
     */
    public void updateButtonClicked(ActionEvent actionEvent) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText());
            String customerName = nameField.getText();
            String phoneNumber = phoneField.getText();
            String address = addressField.getText();
            String postalCode = postalCodeField.getText();
            String createTime = null;
            String createdBy = "";
            int divisionID = 0;
            String countryName = "";

            for (FirstLevelDivisions firstLevelDiv : FirstLevelDivisionsAccess.getAllFirstLevelDivisions()) {
                if (firstLevelComboBox.getSelectionModel().getSelectedItem().equals(firstLevelDiv.getDivisionName())) {
                    divisionID = firstLevelDiv.getDivisionID();
                }
            }

            for (Countries country : CountriesAccess.getAllCountries()) {
                if (countryComboBox.getSelectionModel().getSelectedItem().equals(country.getCountryName())) {
                    countryName = country.getCountryName();
                }
            }


            Customers updatedCustomer = new Customers(customerID, customerName, address, postalCode, phoneNumber, divisionID, countryName);

            CustomersAccess.updateCustomer(customerID, customerName, address, postalCode, phoneNumber, divisionID);

            Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/customersScreen.fxml"));
            Scene scene = new Scene(root, 700, 600);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** Cancels update and sends back to main customer screen
     *
     * @param actionEvent
     * @throws IOException
     */
    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/hernandez/c195_wgu_final/customersScreen.fxml"));
        Scene scene = new Scene(root, 700, 600);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /** Passes the selected customer and its index in the list
     *
     * @param currIndex
     * @param selectedCustomer
     * @throws SQLException
     */
    public void customerToModify(int currIndex, Customers selectedCustomer) throws SQLException {
        this.selectedCustomer = selectedCustomer;
        this.currIndex = currIndex;
        int currentCountryID = 0;
        String currentCountryName = "";

        customerIDField.setText(Integer.toString(selectedCustomer.getCustomerID()));
        nameField.setText(selectedCustomer.getCustomerName());
        phoneField.setText(selectedCustomer.getPhoneNumber());
        addressField.setText(selectedCustomer.getAddress());
        postalCodeField.setText(selectedCustomer.getPostalCode());
        firstLevelComboBox.setValue(selectedCustomer.getDivisionName());


        for (FirstLevelDivisions firstLevDiv : FirstLevelDivisionsAccess.getAllFirstLevelDivisions()) {
            if (firstLevDiv.getDivisionID() == selectedCustomer.getDivisionID()) {
                currentCountryID = firstLevDiv.getCountryID();
            }
        }

        for (Countries country : CountriesAccess.getAllCountries()) {
            if (currentCountryID == country.getCountryID()) {
                currentCountryName = country.getCountryName();
            }
        }
        countryComboBox.setValue(currentCountryName);
    }

    /** Pre-fills the fields with the selected customer information
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            countryComboBox.setItems(CountriesAccess.getAllCountries());
            firstLevelComboBox.setItems(FirstLevelDivisionsAccess.getAllFirstLevelDivisions());

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

    /** Filters the first level divisions list depending on the country selected
     *
     * @param actionEvent
     */
    public void countryComboSelected(ActionEvent actionEvent) {
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
