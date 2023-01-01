package access;

import hernandez.c195_wgu_final.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Countries;
import model.Customers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Access to all countries
 *
 */
public class CountriesAccess {

    /** Gathers all countries from the database
     *
     * @return list of countries
     * @throws SQLException
     */
    public static ObservableList<Countries> getAllCountries() throws SQLException {
            ObservableList<Countries> countriesList = FXCollections.observableArrayList();

            String query = "SELECT countries.Country_ID, countries.Country FROM countries";
            PreparedStatement statement = JDBC.connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int countryID = rs.getInt("Country_ID");
                String countryName = rs.getString("Country");

                Countries country = new Countries(countryID, countryName);
                countriesList.add(country);
            }
            return countriesList;
        }



}
