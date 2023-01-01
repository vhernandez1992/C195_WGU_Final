package model;

public class Contacts {
    public int contactID;
    public String contactName;
    public String contactEmail;


    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /** Class for the contacts in the database to be chosen from for appointments
     *
     * @param contactID
     * @param contactName
     * @param contactEmail
     */
    public Contacts(int contactID, String contactName, String contactEmail) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    public String toString () {
        return contactName;
    }
}
