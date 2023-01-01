package model;

public class Countries {
    private int countryID;
    private String countryName;


    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /** Class for the Countries provided in the database
     *
     * @param countryID
     * @param countryName
     */
    public Countries(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }
    public String toString() {
        return countryName;
    }
}
