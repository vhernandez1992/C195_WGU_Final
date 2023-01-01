package model;

public class FirstLevelDivisions {
    private int divisionID;
    private String divisionName;
    private int countryID;


    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    /** Class for first level divisions for customer information
     *
     * @param divisionID
     * @param divisionName
     * @param countryID
     */
    public FirstLevelDivisions(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    @Override
    public String toString() {
        return divisionName;
    }
}
