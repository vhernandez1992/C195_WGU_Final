package access;

import hernandez.c195_wgu_final.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.FirstLevelDivisions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Access to all states/provinces in database
 *
 */
public class FirstLevelDivisionsAccess {

    /** Generates a list of all states/provinces
     *
     * @return list of states/provinces
     * @throws SQLException
     */
    public static ObservableList<FirstLevelDivisions> getAllFirstLevelDivisions() throws SQLException {
        ObservableList<FirstLevelDivisions> firstLevelDivList = FXCollections.observableArrayList();

        String query = "SELECT first_level_divisions.Division_ID, first_level_divisions.Division, first_level_divisions.Country_ID FROM first_level_divisions";
        PreparedStatement statement = JDBC.connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int countryID = rs.getInt("Country_ID");

            FirstLevelDivisions firstLevelDivisions = new FirstLevelDivisions(divisionID, divisionName, countryID);
            firstLevelDivList.add(firstLevelDivisions);
        }
        return firstLevelDivList;
    }

    /** generates a list of filtered states/provinces depending on country chosen
     *
     * @param selectedCountryID
     * @return filtered list of states/provinces
     * @throws SQLException
     */
    public static ObservableList<String> getFilteredFirstLevelDivisions(int selectedCountryID) throws SQLException {
        ObservableList<String> filteredList = FXCollections.observableArrayList();

        String query = "SELECT first_level_divisions.Division_ID, first_level_divisions.Division, first_level_divisions.Country_ID FROM first_level_divisions";
        PreparedStatement statement = JDBC.connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int countryID = rs.getInt("Country_ID");

            if (selectedCountryID == countryID) {
                FirstLevelDivisions firstLevelDivisions = new FirstLevelDivisions(divisionID, divisionName, countryID);
                filteredList.add(firstLevelDivisions.getDivisionName());
            }

        }
        return filteredList;
    }

}
