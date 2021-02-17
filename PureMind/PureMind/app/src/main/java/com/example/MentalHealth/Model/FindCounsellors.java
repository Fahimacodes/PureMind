package com.example.MentalHealth.Model;

public class FindCounsellors {


    public String profileimage, username, firstname, lastname, counsellorcode, location;
    public int usertype;

    public FindCounsellors(){}

    public FindCounsellors(String profileimage, String username, String firstname, String lastname, String counsellorcode, String location, int usertype) {
        this.profileimage = profileimage;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.counsellorcode = counsellorcode;
        this.location = location;
        this.usertype = usertype;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCounsellorcode() {
        return counsellorcode;
    }

    public void setCounsellorcode(String counsellorcode) {
        this.counsellorcode = counsellorcode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

}
