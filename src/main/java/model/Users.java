package model;

public class Users {
    private int userID;
    private String username;
    private String password;

    /** Class for users from database, used for login and selecting for appointments
     *
     * @param userID
     * @param username
     * @param password
     */
    public Users(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return username;
    }
}
